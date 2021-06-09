package com.hedvig.rapio.config

import com.hedvig.rapio.apikeys.Roles
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.User

@Profile("noauth")
@Configuration
@EnableWebSecurity
class UnSecureConfig : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        val userDetails = User.withUsername("HEDVIG").roles(Roles.COMPARISON.name).password("").build()
        http
            .csrf().disable()
            .httpBasic().and()
            .authorizeRequests()
            .antMatchers("/actuator/health/**").permitAll()
            .antMatchers("/**").anonymous()
            .and().anonymous().principal(userDetails)
    }
}
