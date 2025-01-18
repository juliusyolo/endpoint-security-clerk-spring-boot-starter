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
        // clerk api endpoint url
        @DefaultValue("https://api.clerk.com/v1")
        String serverUrl,
        // clerk secret key
        String secretKey,
        // max organization count that clerk's user can have
        @DefaultValue("10")
        Long maxOrganizationCount,
        // clerk permission prefix
        @DefaultValue("org:")
        String permissionPrefix) {
}
