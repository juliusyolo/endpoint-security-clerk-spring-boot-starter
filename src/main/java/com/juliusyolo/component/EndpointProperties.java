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
        // endpoint prefix
        String prefix,
        // permit paths
        String permitPaths,
        // should authorize paths
        String authorizationPaths,
        // whether enable path authorization
        boolean pathAuthorizationEnable
) {
}
