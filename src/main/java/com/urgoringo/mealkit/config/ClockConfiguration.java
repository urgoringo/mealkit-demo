package com.urgoringo.mealkit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/**
 * Configuration for Clock bean.
 * Provides UTC clock for production to avoid depending on system timezone settings.
 */
@Configuration
public class ClockConfiguration {

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("UTC"));
    }
}
