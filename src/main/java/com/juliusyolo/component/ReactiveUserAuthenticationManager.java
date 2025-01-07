package com.juliusyolo.component;


import com.juliusyolo.exception.UserAuthenticationException;
import com.juliusyolo.model.UserPermissionAuthenticationToken;
import com.juliusyolo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

/**
 * <p>
 * ReactiveUserAuthenticationManager
 * </p>
 *
 * @author julius.yolo
 * @version : ReactiveUserAuthenticationManager v0.1
 */
public class ReactiveUserAuthenticationManager implements ReactiveAuthenticationManager {

    private final static Logger LOGGER =
            LoggerFactory.getLogger(ReactiveUserAuthenticationManager.class);

    private final UserService userService;

    public ReactiveUserAuthenticationManager(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .cast(UserPermissionAuthenticationToken.class)
                .map(userService::verifyToken)
                .switchIfEmpty(UserAuthenticationException.error("No user found"))
                .doOnError(e -> LOGGER.error("UserAuthenticationManager#authenticate ", e))
                .map(userModel -> UserPermissionAuthenticationToken.authenticated(userModel, userModel.getAuthorities()));
    }
}
