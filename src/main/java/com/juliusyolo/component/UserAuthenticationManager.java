package com.juliusyolo.component;


import com.juliusyolo.exception.UserAuthenticationException;
import com.juliusyolo.model.UserPermissionAuthenticationToken;
import com.juliusyolo.service.UserService;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * <p>
 * UserAuthenticationManager
 * </p>
 *
 * @author julius.yolo
 * @version : UserAuthenticationManager v0.1
 */
@Component
public class UserAuthenticationManager implements ReactiveAuthenticationManager {

    private final UserService userService;

    public UserAuthenticationManager(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .cast(UserPermissionAuthenticationToken.class)
                .map(userService::verifyToken)
                .switchIfEmpty(UserAuthenticationException.error("No User Found"))
                .map(userModel -> UserPermissionAuthenticationToken.authenticated(userModel, userModel.getAuthorities()));
    }
}
