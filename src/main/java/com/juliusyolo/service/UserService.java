package com.juliusyolo.service;


import com.juliusyolo.model.UserModel;
import com.juliusyolo.model.UserPermissionAuthenticationToken;

public interface UserService {

    UserModel verifyToken(UserPermissionAuthenticationToken token);

    boolean verifyAuthorization(UserPermissionAuthenticationToken token, String path);

}
