package com.juliusyolo.configuration;

import com.juliusyolo.component.EndpointProperties;
import com.juliusyolo.component.UserAuthenticationManager;
import com.juliusyolo.component.UserAuthorizationManager;
import com.juliusyolo.component.UserPermissionAuthenticationConverter;
import com.juliusyolo.exception.UserAuthorizationException;
import com.juliusyolo.service.UserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableWebFluxSecurity
public class WebFluxSecurityConfiguration {

    @Bean
    public UserPermissionAuthenticationConverter userPermissionAuthenticationConverter() {
        return new UserPermissionAuthenticationConverter();
    }

    @Bean
    public UserAuthenticationManager userAuthenticationManager(UserService userService) {
        return new UserAuthenticationManager(userService);
    }

    @Bean
    public UserAuthorizationManager userAuthorizationManager(UserService userService) {
        return new UserAuthorizationManager(userService);
    }

    @Bean
    public ServerAuthenticationFailureHandler serverAuthenticationFailureHandler() {
        return (webFilterExchange, exception) -> {
            ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            DataBuffer buffer = response.bufferFactory()
                    .wrap(String.format("{\"code\":\"403\",message:\"%s\"}", exception.getMessage()).getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        };
    }

    @Bean
    public ServerAccessDeniedHandler serverAccessDeniedHandler() {
        return (exchange, denied) -> {
            if (!(denied instanceof UserAuthorizationException)) {
                denied = new UserAuthorizationException("USER_NOT_AUTHORIZATION");
            }
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            DataBuffer buffer = response.bufferFactory().
                    wrap(String.format("{\"code\":\"401\",message:\"%s\"}", denied.getMessage()).getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        };
    }

    @Bean
    public AuthenticationWebFilter authenticationWebFilter(UserPermissionAuthenticationConverter userPermissionAuthenticationConverter,
                                                           UserAuthenticationManager userAuthenticationManager,
                                                           ServerAuthenticationFailureHandler serverAuthenticationFailureHandler) {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(userAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(userPermissionAuthenticationConverter);
        authenticationWebFilter.setAuthenticationFailureHandler(serverAuthenticationFailureHandler);
        return authenticationWebFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity,
                                                         EndpointProperties endpointProperties,
                                                         AuthenticationWebFilter authenticationWebFilter,
                                                         UserAuthorizationManager userAuthorizationManager,
                                                         ServerAccessDeniedHandler serverAccessDeniedHandler) {
        return httpSecurity
                .authorizeExchange(authorizeExchangeSpec -> {
                    authorizeExchangeSpec.pathMatchers(endpointProperties.permitPaths()).permitAll();
                    authorizeExchangeSpec.pathMatchers(endpointProperties.authorizationPaths()).access(userAuthorizationManager).anyExchange().authenticated();
                })
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec.accessDeniedHandler(serverAccessDeniedHandler))
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .build();

    }
}