package com.hedvig.rapio.insuranceinfo.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class IsMemberResponse(
    @get:JsonProperty("isMember")
    val isMember: Boolean
)
