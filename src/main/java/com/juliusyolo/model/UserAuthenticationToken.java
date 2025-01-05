package com.juliusyolo.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * <p>
 * UserAuthenticationToken
 * </p>
 *
 * @author julius.yolo
 * @version : UserAuthenticationToken v0.1
 */
public class UserAuthenticationToken extends AbstractAuthenticationToken {

    private final UserModel userModel;

    public UserAuthenticationToken(UserModel userModel) {
        super(userModel.getAuthorities());
        this.userModel = userModel;
        super.setAuthenticated(true);

    }

    @Override
    public Object getCredentials() {
        return userModel.getPassword();
    }

    @Override
    public Object getPrincipal() {
        return userModel.getUsername();
    }


    public UserModel getUserModel() {
        return userModel;
    }

}
