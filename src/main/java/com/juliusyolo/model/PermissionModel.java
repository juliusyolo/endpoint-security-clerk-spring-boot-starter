package com.juliusyolo.model;


import org.springframework.security.core.GrantedAuthority;


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
