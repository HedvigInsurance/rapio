package com.hedvig.rapio.quotes.web.dto

import arrow.optics.optics
import javax.validation.Valid
import javax.validation.constraints.*

@optics
data class QuoteRequestDTO(
        val requestId :String,
        @get:Valid val quoteData: QuoteRequestData,
        val productType: ProductType
) {
    companion object
}

@optics
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
) {
    companion object
}

    enum class ProductSubType {
    BRF,
    RENT
    }

    enum class ProductType {
        HOME,
        HOUSE
    }