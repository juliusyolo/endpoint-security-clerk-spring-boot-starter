package com.juliusyolo.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * <p>
 * a {@link AbstractAuthenticationToken} implement that uses user's token to authenticate
 * when unauthenticated, it only has token attribute.And when authenticated,it has a principal that represents the
 * authenticated user.
 * </p>
 *
 * @author julius.yolo
 * @version : UserPermissionAuthenticationToken v0.1
 */
public class UserPermissionAuthenticationToken extends AbstractAuthenticationToken {
    /**
     * the authenticated user principal,such as clerk user model
     */
    private Object principal;
    /**
     * a token,such as clerk token to authenticate user
     */
    private String token;

    public UserPermissionAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
    }

    public UserPermissionAuthenticationToken(Object principal, String token) {
        super(Collections.emptyList());
        this.principal = principal;
        this.token = token;
    }


    public static UserPermissionAuthenticationToken unauthenticated(String token) {
        return new UserPermissionAuthenticationToken(null, token);
    }

    public static UserPermissionAuthenticationToken unauthenticated(Object principal, String token) {
        return new UserPermissionAuthenticationToken(principal, token);
    }

    public static UserPermissionAuthenticationToken authenticated(Object principal,
                                                                  Collection<? extends GrantedAuthority> authorities) {
        return new UserPermissionAuthenticationToken(principal, authorities);
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public void setPrincipal(Object principal) {
        this.principal = principal;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
