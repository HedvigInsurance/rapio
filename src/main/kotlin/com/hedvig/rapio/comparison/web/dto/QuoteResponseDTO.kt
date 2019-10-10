package com.hedvig.rapio.comparison.web.dto

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.util.*
import javax.money.MonetaryAmount

@JsonTypeName("QuoteResponse")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "object")
data class QuoteResponseDTO(
        val requestId: String,
        val quoteId: String,
        val validUntil:Long?,
        val price:MonetaryAmount
)