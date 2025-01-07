package com.juliusyolo.component;

import com.juliusyolo.model.UserPermissionAuthenticationToken;
import com.juliusyolo.service.UserService;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.function.Supplier;

/**
 * <p>
 * UserAuthenticationManager
 * </p>
 *
 * @author julius.yolo
 * @version : UserAuthenticationManager v0.1
 */
public class UserAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final UserService userService;

    public UserAuthorizationManager(UserService userService) {
        this.userService = userService;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        String path = context.getRequest().getServletPath();
        UserPermissionAuthenticationToken token = (UserPermissionAuthenticationToken) authentication.get();
        boolean decision = userService.verifyAuthorization(token, path);
        return new AuthorizationDecision(decision);
    }
}
