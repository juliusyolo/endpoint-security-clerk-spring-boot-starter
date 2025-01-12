package com.juliusyolo.component;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * <p>
 * clerk configuration properties
 * </p>
 *
 * @author julius.yolo
 * @version : ClerkProperties v0.1
 */
@ConfigurationProperties("yolo.clerk")
public record ClerkProperties(
        @DefaultValue("https://api.clerk.com/v1")
        String serverUrl,
        String secretKey,
        @DefaultValue("10")
        Long maxOrganizationCount,
        @DefaultValue("org:")
        String permissionPrefix) {
}
