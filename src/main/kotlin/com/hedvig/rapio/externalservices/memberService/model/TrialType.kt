package com.hedvig.rapio.externalservices.memberService.model

import com.fasterxml.jackson.annotation.JsonProperty

enum class TrialType {
    @JsonProperty("SE_APARTMENT_RENT")
    SE_APARTMENT_RENT,
    @JsonProperty("SE_APARTMENT_BRF")
    SE_APARTMENT_BRF
}
