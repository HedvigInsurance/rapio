package com.hedvig.rapio

import com.hedvig.rapio.externalservices.memberService.model.TrialType
import com.hedvig.rapio.externalservices.memberService.model.toContractType
import com.hedvig.rapio.externalservices.productPricing.TypeOfContract
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TrialTypeTest {

    @Test
    fun `all TrialTypes have a matching TypeOfContract`() {
        TrialType.values().forEach {
            Assertions.assertDoesNotThrow {
                TypeOfContract.valueOf(it.name)
            }
        }
    }
}