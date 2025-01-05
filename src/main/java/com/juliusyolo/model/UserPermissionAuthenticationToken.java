package com.juliusyolo.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class UserPermissionAuthenticationToken extends AbstractAuthenticationToken {
    private Object principal;
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
