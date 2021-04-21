package com.hedvig.rapio.apikeys

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration()
@ConfigurationProperties("hedvig.rapio")
class Keys {

    var apikeys: Map<String, String>? = null
}
