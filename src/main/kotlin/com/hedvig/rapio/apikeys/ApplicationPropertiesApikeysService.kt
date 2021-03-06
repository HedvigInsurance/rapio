package com.hedvig.rapio.apikeys

import io.sentry.Sentry
import mu.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import io.sentry.event.User as SentryUser

private val logger = KotlinLogging.logger {}

@Component
@Profile("auth")
class ApplicationPropertiesApikeysService(val config: Keys) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        val sentry = Sentry.getContext()

        val apiUserName = config.apikeys?.get(username)

        val partner: Partner
        try {
            partner = Partner.valueOf(apiUserName!!)
        } catch (ex: Exception) {
            logger.error("Could not find any partner named $apiUserName")
            throw UsernameNotFoundException("Could not find any partner named $apiUserName")
        }

        val user = User.withDefaultPasswordEncoder().username(apiUserName)
            .password("")
            .roles(partner.role.name).build()

        sentry.user = SentryUser(null, apiUserName, null, null)
        return user
    }
}
