package com.hedvig.rapio.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Profile("noauth")
@Configuration
@EnableWebSecurity
class UnSecureConfig : WebSecurityConfigurerAdapter() {


    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.antMatcher("/").securityContext().disable()
                //.authorizeRequests()
                //.antMatchers("/v1/quote")
                //.anyRequest().authenticated()
                //.and()
                //.httpBasic()
                //.realmName("hedvig")
                //.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

    }
}