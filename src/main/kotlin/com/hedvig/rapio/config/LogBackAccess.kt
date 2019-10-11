package com.hedvig.rapio.config

import ch.qos.logback.access.tomcat.LogbackValve
import javax.servlet.Filter
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("runtime")
@Configuration
class LogBackAccess {

    @Bean(name = ["TeeFilter"])
    fun teeFilter(): Filter {
        return ch.qos.logback.access.servlet.TeeFilter()
    }

    @Bean
    fun servletContainer(): ServletWebServerFactory {
        val tomcat = TomcatServletWebServerFactory()

        val logbackValve = LogbackValve()

        // point to logback-access.xml
        logbackValve.filename = "logback-access.xml"

        tomcat.addContextValves(logbackValve)

        return tomcat
    }

}
