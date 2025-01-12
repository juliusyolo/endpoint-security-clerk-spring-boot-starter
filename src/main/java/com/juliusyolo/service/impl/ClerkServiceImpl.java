package com.juliusyolo.service.impl;

import com.clerk.backend_api.Clerk;
import com.clerk.backend_api.models.components.Client;
import com.clerk.backend_api.models.components.Session;
import com.clerk.backend_api.models.components.Status;
import com.clerk.backend_api.models.operations.GetUserResponse;
import com.clerk.backend_api.models.operations.UsersGetOrganizationMembershipsResponse;
import com.clerk.backend_api.models.operations.VerifyClientRequestBody;
import com.clerk.backend_api.models.operations.VerifyClientResponse;
import com.juliusyolo.component.ClerkProperties;
import com.juliusyolo.component.EndpointProperties;
import com.juliusyolo.exception.UserAuthenticationException;
import com.juliusyolo.model.UserModel;
import com.juliusyolo.model.UserPermissionAuthenticationToken;
import com.juliusyolo.service.ClerkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * a clerk implement
 * </p>
 *
 * @author julius.yolo
 * @version : ClerkService v0.1
 */
public class ClerkServiceImpl implements ClerkService {

    private final static Logger LOGGER =
            LoggerFactory.getLogger(ClerkServiceImpl.class);

    private final Clerk clerk;

    private final ClerkProperties clerkProperties;

    private final EndpointProperties endpointProperties;

    public ClerkServiceImpl(Clerk clerk, ClerkProperties clerkProperties, EndpointProperties endpointProperties) {
        this.clerk = clerk;
        this.clerkProperties = clerkProperties;
        this.endpointProperties = endpointProperties;
    }

    @Override
    public String getActiveClerkUserId(String token) {
        LOGGER.info("ClerkServiceImpl#getActiveClerkUserId token:{}", token);
        try {
            VerifyClientRequestBody req = VerifyClientRequestBody.builder().token(token).build();
            VerifyClientResponse res = clerk.clients().verify().request(req).call();
            if (res.client().isPresent()) {
                Client client = res.client().get();
                if (CollectionUtils.isEmpty(client.sessions())) {
                    return null;
                }
                List<Session> sessions = client.sessions().stream().filter(session -> Objects.equals(session.status(), Status.ACTIVE)).toList();
                if (CollectionUtils.isEmpty(sessions)) {
                    return null;
                }
                return sessions.getFirst().userId();
            }
            return null;
        } catch (Exception e) {
            throw new UserAuthenticationException("No client found", e);
        }
    }

    @Override
    public UserModel verifyToken(UserPermissionAuthenticationToken token) {
        String userId = this.getActiveClerkUserId(token.getToken());
        if (userId == null) {
            throw new UserAuthenticationException("No active clerk user found");
        }
        try {
            UserModel userModel = null;
            GetUserResponse userRes = clerk.users().get()
                    .userId(userId)
                    .call();

            if (userRes.user().isPresent()) {
                userModel = new UserModel();
                userModel.setUser(userRes.user().get());
                UsersGetOrganizationMembershipsResponse membershipsRes = clerk.users().getOrganizationMemberships()
                        .userId(userId)
                        .limit(clerkProperties.maxOrganizationCount())
                        .offset(0L)
                        .call();

                userModel.setOrganizationMemberships(membershipsRes.organizationMemberships());
            }
            return userModel;
        } catch (Exception e) {
            throw new UserAuthenticationException("Fetch user failed", e);
        }

    }

    @Override
    public boolean verifyAuthorization(UserPermissionAuthenticationToken token, String path) {
        if (path.startsWith(endpointProperties.prefix())) {
            path = path.substring(endpointProperties.prefix().length());
        }
        if (path.startsWith("/")) {
            path = path.substring( 1);
        }
        String[] split = path.split("/");
        if (split.length >= 2) {
            path = "org:" + split[0] + ":" + Arrays.stream(split).skip(1).collect(Collectors.joining("_"));
        } else {
            path = "org:default:" + split[0];
        }
        return token.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(path::equals);
    }
}
