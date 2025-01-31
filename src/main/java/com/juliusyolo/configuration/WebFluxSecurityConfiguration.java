package com.juliusyolo.configuration;

import com.juliusyolo.component.EndpointProperties;
import com.juliusyolo.component.ReactiveUserAuthenticationManager;
import com.juliusyolo.component.ReactiveUserAuthorizationManager;
import com.juliusyolo.component.UserPermissionServerAuthenticationConverter;
import com.juliusyolo.exception.UserAuthorizationException;
import com.juliusyolo.model.UserPermissionAuthenticationToken;
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

/**
 * <p>
 * reactive web security configuration
 * </p>
 *
 * @author julius.yolo
 * @version : WebFluxSecurityConfiguration v0.1
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableWebFluxSecurity
public class WebFluxSecurityConfiguration {


    /**
     * bearer token transform to user permission token
     *
     * @return UserPermissionServerAuthenticationConverter
     */
    @Bean
    public UserPermissionServerAuthenticationConverter userPermissionAuthenticationConverter() {
        return new UserPermissionServerAuthenticationConverter();
    }

    /**
     * authenticate user by {@link UserPermissionAuthenticationToken}
     *
     * @param userService user service
     * @return ReactiveUserAuthenticationManager
     */
    @Bean
    public ReactiveUserAuthenticationManager userAuthenticationManager(UserService userService) {
        return new ReactiveUserAuthenticationManager(userService);
    }

    /**
     * authorize user by {@link UserPermissionAuthenticationToken}
     *
     * @param userService user service
     * @return ReactiveUserAuthorizationManager
     */
    @Bean
    public ReactiveUserAuthorizationManager userAuthorizationManager(UserService userService) {
        return new ReactiveUserAuthorizationManager(userService);
    }

    /**
     * a handler to process authentication failure in spring security
     *
     * @return ServerAuthenticationFailureHandler
     */
    @Bean
    public ServerAuthenticationFailureHandler serverAuthenticationFailureHandler() {
        return (webFilterExchange, exception) -> {
            ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            DataBuffer buffer = response.bufferFactory()
                    .wrap(String.format("{\"code\":\"403\",\"message\":\"%s\"}", exception.getMessage()).getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        };
    }

    /**
     * a handler to process authorize access denied in spring security
     *
     * @return ServerAccessDeniedHandler
     */
    @Bean
    public ServerAccessDeniedHandler serverAccessDeniedHandler() {
        return (exchange, denied) -> {
            if (!(denied instanceof UserAuthorizationException)) {
                denied = new UserAuthorizationException("No authorization found");
            }
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            DataBuffer buffer = response.bufferFactory().
                    wrap(String.format("{\"code\":\"401\",\"message\":\"%s\"}", denied.getMessage()).getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        };
    }

    /**
     * a spring security filter to authenticate user
     *
     * @param userPermissionServerAuthenticationConverter userPermissionServerAuthenticationConverter
     * @param reactiveUserAuthenticationManager           reactiveUserAuthenticationManager
     * @param serverAuthenticationFailureHandler          serverAuthenticationFailureHandler
     * @return AuthenticationWebFilter
     */
    public AuthenticationWebFilter authenticationWebFilter(UserPermissionServerAuthenticationConverter userPermissionServerAuthenticationConverter,
                                                           ReactiveUserAuthenticationManager reactiveUserAuthenticationManager,
                                                           ServerAuthenticationFailureHandler serverAuthenticationFailureHandler) {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(reactiveUserAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(userPermissionServerAuthenticationConverter);
        authenticationWebFilter.setAuthenticationFailureHandler(serverAuthenticationFailureHandler);
        return authenticationWebFilter;
    }

    /**
     * configure web security filter chain
     *
     * @param httpSecurity                                httpSecurity
     * @param endpointProperties                          endpointProperties
     * @param userPermissionServerAuthenticationConverter userPermissionServerAuthenticationConverter
     * @param reactiveUserAuthenticationManager           reactiveUserAuthenticationManager
     * @param serverAuthenticationFailureHandler          serverAuthenticationFailureHandler
     * @param reactiveUserAuthorizationManager            reactiveUserAuthorizationManager
     * @param serverAccessDeniedHandler                   serverAccessDeniedHandler
     * @return SecurityWebFilterChain
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity,
                                                         EndpointProperties endpointProperties,
                                                         UserPermissionServerAuthenticationConverter userPermissionServerAuthenticationConverter,
                                                         ReactiveUserAuthenticationManager reactiveUserAuthenticationManager,
                                                         ServerAuthenticationFailureHandler serverAuthenticationFailureHandler,
                                                         ReactiveUserAuthorizationManager reactiveUserAuthorizationManager,
                                                         ServerAccessDeniedHandler serverAccessDeniedHandler) {
        return httpSecurity
                .authorizeExchange(authorizeExchangeSpec -> {
                    authorizeExchangeSpec.pathMatchers(endpointProperties.permitPaths()).permitAll();
                    authorizeExchangeSpec.pathMatchers(endpointProperties.authorizationPaths()).access(reactiveUserAuthorizationManager).anyExchange().authenticated();
                })
                .addFilterAt(authenticationWebFilter(
                        userPermissionServerAuthenticationConverter,
                        reactiveUserAuthenticationManager,
                        serverAuthenticationFailureHandler
                ), SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec.accessDeniedHandler(serverAccessDeniedHandler))
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .anonymous(ServerHttpSecurity.AnonymousSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .build();

    }
}