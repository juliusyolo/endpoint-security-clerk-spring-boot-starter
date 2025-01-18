package com.juliusyolo.model;

import com.clerk.backend_api.models.components.OrganizationMemberships;
import com.clerk.backend_api.models.components.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Optional;

/**
 * <p>
 * custom user model which implements {@link UserDetails }
 * just holds clerk user info and clerk organization memberships
 * </p>
 *
 * @author julius.yolo
 * @version : UserModel v0.1
 */
public class UserModel implements UserDetails {

    /**
     * clerk user model
     */
    private User user;

    /**
     * clerk user's organization memberships
     */
    private Optional<? extends OrganizationMemberships> organizationMemberships;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return organizationMemberships.map(OrganizationMemberships::data)
                .stream().flatMap(Collection::stream)
                .flatMap(organizationMembership ->
                        organizationMembership.permissions().stream().flatMap(Collection::stream).map(PermissionModel::new)
                )
                .toList();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return user.username().orElse(null);
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.locked().orElse(false);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isEnabled();
    }

    @Override
    public boolean isEnabled() {
        return !user.banned().orElse(false);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Optional<? extends OrganizationMemberships> getOrganizationMemberships() {
        return organizationMemberships;
    }

    public void setOrganizationMemberships(Optional<? extends OrganizationMemberships> organizationMemberships) {
        this.organizationMemberships = organizationMemberships;
    }
}
