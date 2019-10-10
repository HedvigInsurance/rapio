package com.hedvig.rapio.comparison.web.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import javax.validation.Valid
import javax.validation.constraints.*

data class QuoteRequestDTO(
        val requestId :String,
        @get:Valid val quoteData: QuoteRequestData,
        @get:NotBlank val productType: String
)

data class QuoteRequestData(
        @get:NotBlank val street: String,

        @get:Digits(integer = 5, fraction = 0)
        @get:Size(min=5, max=5)
        @get:NotBlank val zipCode: String,

        @get:NotBlank val city: String,

        @get:Min(1) @Max(1000) val livingSpace: Int,

        @get:Size(min=12, max=12) val personalNumber: String,

        @get:Min(1) @Max(100)
        val householdSize: Int,

        val productSubType: ProductSubType
)

enum class ProductSubType {
    BRF,
    RENT
}
