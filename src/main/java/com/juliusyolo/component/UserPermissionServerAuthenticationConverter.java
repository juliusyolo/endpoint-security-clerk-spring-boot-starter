package com.juliusyolo.component;

import com.juliusyolo.exception.UserAuthenticationException;
import com.juliusyolo.model.UserPermissionAuthenticationToken;
import io.micrometer.common.util.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * <p>
 * UserPermissionServerAuthenticationConverter
 * </p>
 *
 * @author julius.yolo
 * @version : UserPermissionServerAuthenticationConverter v0.1
 */
public class UserPermissionServerAuthenticationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(bearerStr -> StringUtils.isNotBlank(bearerStr) && bearerStr.startsWith("Bearer "))
                .map(bearerStr -> bearerStr.substring(7))
                .switchIfEmpty(UserAuthenticationException.error("No token found"))
                .map(UserPermissionAuthenticationToken::unauthenticated);
    }
}
