package com.hedvig.rapio.externalservices.memberService.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.hedvig.rapio.externalservices.productPricing.TypeOfContract

/**
 * TypeOfContract value that are allowed for trials
 */
enum class TrialType {
    @JsonProperty("SE_APARTMENT_RENT")
    SE_APARTMENT_RENT,

    @JsonProperty("SE_APARTMENT_BRF")
    SE_APARTMENT_BRF
}

fun TrialType.toContractType() = TypeOfContract.valueOf(name)
