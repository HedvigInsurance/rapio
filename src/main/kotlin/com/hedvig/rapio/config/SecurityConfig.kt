package com.hedvig.rapio.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@Profile("auth")
@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {


    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .csrf().disable()
                .httpBasic().and()
                .authorizeRequests()
                .antMatchers("/actuator/health/**").permitAll()
                .antMatchers("/**").authenticated()



                //.and()

                //.realmName("hedvig")
                    .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

    }
}