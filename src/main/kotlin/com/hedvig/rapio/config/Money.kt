package com.hedvig.productPricing.config

import com.fasterxml.jackson.databind.Module
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.zalando.jackson.datatype.money.MoneyModule

@Configuration
class Money {
    @Bean
    fun monetaModule(): Module {
        return MoneyModule().withQuotedDecimalNumbers()
    }
}