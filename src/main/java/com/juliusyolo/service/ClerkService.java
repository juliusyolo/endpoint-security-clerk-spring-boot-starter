package com.juliusyolo.service;

/**
 * <p>
 * a clerk service interface that extends {@link UserService}
 * </p>
 *
 * @author julius.yolo
 * @version : ClerkService v0.1
 */
public interface ClerkService extends UserService {
    /**
     * get current active clerk user id from the specific token
     *
     * @param token user's token
     * @return the user's id
     * @throws Exception maybe throws an exception when acquires user's id. such as network exception
     */
    String getActiveClerkUserId(String token) throws Exception;
}
