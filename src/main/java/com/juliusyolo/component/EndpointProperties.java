package com.juliusyolo.component;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("yolo.endpoint")
public record EndpointProperties(
        String prefix,
        String permitPaths,
        String authorizationPaths
) {
}
