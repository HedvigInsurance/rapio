package com.hedvig.rapio.quotes.web.dto

import arrow.optics.optics
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.hedvig.rapio.externalservices.underwriter.transport.ExtraBuildingRequestDto
import javax.validation.Valid
import javax.validation.constraints.*

@optics
data class QuoteRequestDTO(
    val requestId :String,
    @get: Valid val productType: ProductType,
    @set:JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "productType")
    @JsonSubTypes(
            JsonSubTypes.Type(value = ApartmentQuoteRequestData::class, name = "SWEDISH_APARTMENT"),
            JsonSubTypes.Type(value = HouseQuoteRequestData::class, name = "SWEDISH_HOUSE"),
            JsonSubTypes.Type(value = ApartmentQuoteRequestData::class, name = "HOME"), // Deprecated use SWEDISH_APARTMENT
            JsonSubTypes.Type(value = HouseQuoteRequestData::class, name = "HOUSE")  // Deprecated use SWEDISH_HOUSE
    ) var quoteData: QuoteRequestData
) {
    companion object
}

sealed class QuoteRequestData {
}

@optics
data class ApartmentQuoteRequestData(
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
): QuoteRequestData() {
    companion object
}

@optics
data class HouseQuoteRequestData(
    @get:NotBlank val street: String,

    @get:Digits(integer = 5, fraction = 0)
    @get:Size(min=5, max=5)
    @get:NotBlank val zipCode: String,

    @get:NotBlank val city: String,

    @get:Min(1) @get:Max(1000) val livingSpace: Int,

    @get:Size(min=12, max=12) val personalNumber: String,

    @get:Min(1) @get:Max(100)
    val householdSize: Int,

    val ancillaryArea: Int,
    val yearOfConstruction: Int,

    @get:Min(0) @get:Max(20) val numberOfBathrooms: Int,

    val extraBuildings: List<ExtraBuildingRequestDto>?,
    val isSubleted: Boolean,
    val floor: Int
): QuoteRequestData() {
    companion object
}

enum class ProductSubType {
    BRF,
    RENT,
    STUDENT_RENT,
    STUDENT_BRF
}

enum class ProductType {
    SWEDISH_APARTMENT,
    SWEDISH_HOUSE,
    HOME, // Deprecated use SWEDISH_APARTMENT
    HOUSE // Deprecated use SWEDISH_HOUSE
}
