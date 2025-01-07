package com.juliusyolo.component;

import com.juliusyolo.exception.UserAuthenticationException;
import com.juliusyolo.model.UserModel;
import com.juliusyolo.model.UserPermissionAuthenticationToken;
import com.juliusyolo.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Objects;

/**
 * <p>
 * UserAuthenticationManager
 * </p>
 *
 * @author julius.yolo
 * @version : UserAuthenticationManager v0.1
 */
public class UserAuthenticationManager implements AuthenticationManager {


    private final UserService userService;

    public UserAuthenticationManager(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserPermissionAuthenticationToken userPermissionAuthenticationToken =
                (UserPermissionAuthenticationToken) authentication;
        UserModel userModel = userService.verifyToken(userPermissionAuthenticationToken);
        if (Objects.isNull(userModel)) {
            throw new UserAuthenticationException("No user found");
        }
        return UserPermissionAuthenticationToken.authenticated(userModel, userModel.getAuthorities());
    }
}
