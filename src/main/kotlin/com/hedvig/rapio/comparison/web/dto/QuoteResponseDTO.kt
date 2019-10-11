package com.hedvig.rapio.comparison.web.dto

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import javax.money.MonetaryAmount

@JsonTypeName("QuoteResponse")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "object")
data class QuoteResponseDTO(
        val requestId: String,
        val quoteId: String,
        val validUntil:Long?,
        val price:MonetaryAmount
)