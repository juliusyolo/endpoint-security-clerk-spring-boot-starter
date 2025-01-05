package com.juliusyolo.configuration;

import com.clerk.backend_api.Clerk;
import com.juliusyolo.component.ClerkProperties;
import com.juliusyolo.component.EndpointProperties;
import com.juliusyolo.service.UserService;
import com.juliusyolo.service.impl.ClerkServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableConfigurationProperties({ClerkProperties.class, EndpointProperties.class})
@Import({WebFluxSecurityConfiguration.class, WebServletSecurityConfiguration.class})
public class EndpointSecurityConfiguration {


    @Bean
    public Clerk clerk(ClerkProperties clerkProperties) {
        return Clerk.builder().bearerAuth(clerkProperties.secretKey()).serverURL(clerkProperties.serverUrl()).build();
    }

    @Bean
    @ConditionalOnMissingBean(UserService.class)
    public UserService userService(Clerk clerk, ClerkProperties clerkProperties, EndpointProperties endpointProperties) {
        return new ClerkServiceImpl(clerk, clerkProperties, endpointProperties);
    }
}
