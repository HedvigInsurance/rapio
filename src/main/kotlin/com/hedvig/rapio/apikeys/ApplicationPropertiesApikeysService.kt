package com.hedvig.rapio.apikeys

import org.springframework.context.annotation.Profile
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

@Component
@Profile("auth")
class ApplicationPropertiesApikeysService(val config: Keys) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        val apiUserName = config.apikeys?.get(username)

        return User.withDefaultPasswordEncoder().username(apiUserName)
                .password("")
                .roles(Roles.COMPARISON.name).build()
    }
}