package com.juliusyolo.model;


import org.springframework.security.core.GrantedAuthority;

/**
 * <p>
 * custom user permission model which holds a permission attribute and implements {@link GrantedAuthority }
 * </p>
 *
 * @author julius.yolo
 * @version : PermissionModel v0.1
 */
public class PermissionModel implements GrantedAuthority {
    private String permission;

    public PermissionModel(String permission) {
        this.permission = permission;
    }

    @Override
    public String getAuthority() {
        return this.permission;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
