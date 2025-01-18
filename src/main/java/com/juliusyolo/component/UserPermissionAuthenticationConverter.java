package com.juliusyolo.component;

import com.juliusyolo.exception.UserAuthenticationException;
import com.juliusyolo.model.UserPermissionAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.ObjectUtils;

import java.util.Objects;


/**
 * <p>
 * UserPermissionAuthenticationConverter
 * </p>
 *
 * @author julius.yolo
 * @version : UserPermissionAuthenticationConverter v0.1
 */
public class UserPermissionAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.isNull(authorization) || !authorization.startsWith("Bearer ")) {
            throw new UserAuthenticationException("No token found");
        }
        String token = authorization.substring(7);
        if (ObjectUtils.isEmpty(token)) {
            throw new UserAuthenticationException("No token found");
        }
        return UserPermissionAuthenticationToken.unauthenticated(token);
    }
}
