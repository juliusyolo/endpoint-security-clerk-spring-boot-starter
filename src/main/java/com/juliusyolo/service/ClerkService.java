package com.juliusyolo.service;

public interface ClerkService extends UserService {

    String getActiveClerkUserId(String token) throws Exception;
}
