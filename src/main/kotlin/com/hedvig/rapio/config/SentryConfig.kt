package com.hedvig.rapio.config

import io.sentry.spring.SentryExceptionResolver
import io.sentry.spring.SentryServletContextInitializer
import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.servlet.HandlerExceptionResolver

@Configuration
@Profile("production", "staging")
class SentryConfig {
    @Bean
    fun sentryHandlerExceptionResolver(): HandlerExceptionResolver {
        return SentryExceptionResolver()
    }

    @Bean
    fun sentryServletContextInitializer(): ServletContextInitializer {
        return SentryServletContextInitializer()
    }
}