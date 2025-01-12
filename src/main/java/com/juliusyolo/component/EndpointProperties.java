package com.juliusyolo.component;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * endpoint configuration properties
 * </p>
 *
 * @author julius.yolo
 * @version : EndpointProperties v0.1
 */
@ConfigurationProperties("yolo.endpoint")
public record EndpointProperties(
        String prefix,
        String permitPaths,
        String authorizationPaths
) {
}
