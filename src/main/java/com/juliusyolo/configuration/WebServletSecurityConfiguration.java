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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFilter;

/**
 * <p>
 * web servlet security configuration
 * </p>
 *
 * @author julius.yolo
 * @version : WebServletSecurityConfiguration v0.1
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebServletSecurityConfiguration {


    @Bean
    public UserAuthorizationManager userAuthorizationManager(UserService userService) {
        return new UserAuthorizationManager(userService);
    }

    @Bean
    public UserAuthenticationManager userAuthenticationManager(UserService userService) {
        return new UserAuthenticationManager(userService);
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(String.format("{\"code\":\"403\",\"message\":\"%s\"}", exception.getMessage()));
        };
    }

    @Bean
    public UserPermissionAuthenticationConverter userPermissionAuthenticationConverter() {
        return new UserPermissionAuthenticationConverter();
    }


    public AuthenticationFilter authenticationFilter(UserPermissionAuthenticationConverter userPermissionAuthenticationConverter,
                                                     UserAuthenticationManager userAuthenticationManager,
                                                     AuthenticationFailureHandler authenticationFailureHandler) {
        AuthenticationFilter authenticationWebFilter = new AuthenticationFilter(userAuthenticationManager, userPermissionAuthenticationConverter);
        authenticationWebFilter.setFailureHandler(authenticationFailureHandler);
        return authenticationWebFilter;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            if (!(accessDeniedException instanceof UserAuthorizationException)) {
                accessDeniedException = new UserAuthorizationException("USER_NOT_AUTHORIZATION");
            }
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(String.format("{\"code\":\"401\",\"message\":\"%s\"}",
                    accessDeniedException.getMessage()));
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   EndpointProperties endpointProperties,
                                                   UserAuthorizationManager userAuthorizationManager,
                                                   UserPermissionAuthenticationConverter userPermissionAuthenticationConverter,
                                                   UserAuthenticationManager userAuthenticationManager,
                                                   AuthenticationFailureHandler authenticationFailureHandler,
                                                   AccessDeniedHandler accessDeniedHandler) throws Exception {
        return httpSecurity.authorizeHttpRequests(authorizeHttp -> {
                    authorizeHttp.requestMatchers(endpointProperties.authorizationPaths()).permitAll();
                    authorizeHttp.requestMatchers(endpointProperties.authorizationPaths())
                            .access(userAuthorizationManager)
                            .anyRequest().authenticated();
                })
                .addFilterAt(authenticationFilter(userPermissionAuthenticationConverter, userAuthenticationManager, authenticationFailureHandler),
                        AuthenticationFilter.class)
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer.accessDeniedHandler(accessDeniedHandler))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .build();
    }
}