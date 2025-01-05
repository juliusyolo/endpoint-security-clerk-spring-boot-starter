package com.juliusyolo.component;

import com.juliusyolo.model.UserPermissionAuthenticationToken;
import com.juliusyolo.service.UserService;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

/**
 * <p>
 * UserAuthorizationManager
 * </p>
 *
 * @author julius.yolo
 * @version : UserAuthorizationManager v0.1
 */
public class UserAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final UserService userService;

    public UserAuthorizationManager(UserService userService) {
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


}
