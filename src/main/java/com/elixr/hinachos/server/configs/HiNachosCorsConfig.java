package com.elixr.hinachos.server.configs;

import com.elixr.hinachos.server.constants.HiNachosServerConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for Cross-Origin Resource Sharing (CORS).
 */
@Configuration
public class HiNachosCorsConfig implements WebMvcConfigurer {

    /**
     * Add CORS mappings to the registry.
     *
     * @param registry The CorsRegistry to configure CORS mappings.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(HiNachosServerConstants.CORS_CONFIG_END_POINT) // Allow CORS for all endpoints
                .allowedOrigins(HiNachosServerConstants.ASTERISK) // Add your frontend domain here
                .allowedMethods(HiNachosServerConstants.ALLOWED_METHOD_GET, HiNachosServerConstants.ALLOWED_METHOD_POST,
                        HiNachosServerConstants.ALLOWED_METHOD_PUT, HiNachosServerConstants.ALLOWED_METHOD_DELETE) // Allowed HTTP methods
                .allowedHeaders(HiNachosServerConstants.ASTERISK); // Allowed headers
    }
}