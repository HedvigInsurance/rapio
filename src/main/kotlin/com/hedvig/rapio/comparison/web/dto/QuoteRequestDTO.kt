package com.hedvig.rapio.comparison.web.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import javax.validation.Valid

data class QuoteRequestDTO(
        val requestId :String,
        val quoteData: QuoteRequestData,
        val productType: String
)

data class QuoteRequestData(
        val street: String,
        val zipCode: String,
        val city: String,
        val livingSpace: Int,
        val personalNumber: String,
        val householdSize: Int
)