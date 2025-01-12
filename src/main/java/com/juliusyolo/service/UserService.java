package com.juliusyolo.service;


import com.juliusyolo.exception.UserAuthenticationException;
import com.juliusyolo.model.UserModel;
import com.juliusyolo.model.UserPermissionAuthenticationToken;

/**
 * <p>
 * user service interface to verify user information and so on.
 * </p>
 *
 * @author julius.yolo
 * @version : UserService v0.1
 */
public interface UserService {

    /**
     * verify user's token
     *
     * @param token user's token to verify
     * @return return a user info model that infers to this token
     * @throws UserAuthenticationException throw this exception when occur internal exception or not correct token to
     *                                     verify
     */
    UserModel verifyToken(UserPermissionAuthenticationToken token);

    /**
     * verify whether the user's token has permission to a request path
     *
     * @param token user's authenticated user token
     * @param path  the user's request path,likes /api/say/hello/world
     * @return return true if it has this path permission ,or else return false.
     */
    boolean verifyAuthorization(UserPermissionAuthenticationToken token, String path);

}
