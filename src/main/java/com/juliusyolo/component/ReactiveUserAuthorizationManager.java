package com.juliusyolo.component;

import com.juliusyolo.model.UserPermissionAuthenticationToken;
import com.juliusyolo.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

/**
 * <p>
 * ReactiveUserAuthorizationManager
 * </p>
 *
 * @author julius.yolo
 * @version : ReactiveUserAuthorizationManager v0.1
 */
public class ReactiveUserAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final UserService userService;

    public ReactiveUserAuthorizationManager(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext context) {
        return authentication
                .cast(UserPermissionAuthenticationToken.class)
                .map(token -> {
                    String path = context.getExchange().getRequest().getPath().value();
                    return userService.verifyAuthorization(token, path);
                })
                .map(AuthorizationDecision::new);
    }

    @Override
    public Mono<Void> verify(Mono<Authentication> authentication, AuthorizationContext object) {
        return check(authentication, object)
                .filter(AuthorizationDecision::isGranted)
                .switchIfEmpty(Mono.error(new AccessDeniedException("Access Denied")))
                .flatMap((decision) -> Mono.empty());
    }
}
