package com.juliusyolo.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * <p>
 * UserBearerToken
 * </p>
 *
 * @author julius.yolo
 * @version : UserBearerToken v0.1
 */
public class UserBearerToken extends AbstractAuthenticationToken {
    private final String token;

    public UserBearerToken(String token) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.token = token;
    }

    @Override
    public String getCredentials() {
        return this.token;
    }

    @Override
    public String getPrincipal() {
        return this.token;
    }
}
