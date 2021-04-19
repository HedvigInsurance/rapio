package com.hedvig.rapio.config

import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("runtime")
@Configuration
@EnableFeignClients("com.hedvig")
class FeignClients()
