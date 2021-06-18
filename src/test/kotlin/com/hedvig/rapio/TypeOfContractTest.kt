package com.hedvig.rapio

import com.hedvig.rapio.externalservices.productPricing.TypeOfContract
import java.util.Locale
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TypeOfContractTest {

    @Test
    // This needs to be true since we parse country code from
    // the TypeOfContract to get the correct terms
    fun `all TypeOfContract values begin with a country code`() {
        TypeOfContract.values().map {
            val cc = it.toString().split("_").firstOrNull()
            assertThat(cc).isNotNull().withFailMessage("$it does not have the expected format")
            assertThat(Locale.getISOCountries().contains(cc)).isTrue().withFailMessage("$it does not start with a country code")
        }
    }
}