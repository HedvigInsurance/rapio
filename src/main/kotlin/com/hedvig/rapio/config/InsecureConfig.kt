package com.hedvig.rapio.config

import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.apikeys.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.User

@Profile("noauth")
@Configuration
@EnableWebSecurity
class InsecureConfig(
    @Qualifier("insecureUserName")
    @Autowired(required = false)
    val userName: Partner?,
    @Qualifier("insecureUserRole")
    @Autowired(required = false)
    val userRole: Role?
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        val userDetails =
            User.withUsername(userName?.name ?: Partner.HEDVIG.name)
                .roles(userRole?.name ?: Role.COMPARISON.name)
                .password("")
                .build()
        http
            .csrf().disable()
            .httpBasic().and()
            .authorizeRequests()
            .antMatchers("/actuator/health/**").permitAll()
            .antMatchers("/**").anonymous()
            .and().anonymous().principal(userDetails)
    }
}
