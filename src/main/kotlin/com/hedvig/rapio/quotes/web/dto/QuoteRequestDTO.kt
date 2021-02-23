package com.hedvig.rapio.quotes.web.dto

import arrow.optics.optics
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.hedvig.rapio.externalservices.underwriter.transport.ExtraBuildingRequestDto
import javax.validation.Valid
import javax.validation.constraints.*

@optics
data class QuoteRequestDTO(
    @get:NotBlank val requestId: String,
    @get:Valid val productType: ProductType,
    @get:Valid
    @set:JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "productType")
    @JsonSubTypes(
        JsonSubTypes.Type(value = ApartmentQuoteRequestData::class, name = "HOME"), // Deprecated use SWEDISH_APARTMENT
        JsonSubTypes.Type(value = HouseQuoteRequestData::class, name = "HOUSE"),  // Deprecated use SWEDISH_HOUSE
        JsonSubTypes.Type(value = ApartmentQuoteRequestData::class, name = "SWEDISH_APARTMENT"),
        JsonSubTypes.Type(value = HouseQuoteRequestData::class, name = "SWEDISH_HOUSE"),
        JsonSubTypes.Type(value = NorwegianTravelQuoteRequestData::class, name = "NORWEGIAN_TRAVEL"),
        JsonSubTypes.Type(value = NorwegianHomeContentQuoteRequestData::class, name = "NORWEGIAN_HOME_CONTENT")
    )
    var quoteData: QuoteRequestData
) {
    companion object
}

sealed class QuoteRequestData {
}

@optics
data class ApartmentQuoteRequestData(
    @get:NotBlank val street: String,

    @get:NotBlank val zipCode: String,

    @get:NotBlank val city: String,

    @get:Min(1) @get:Max(1000) val livingSpace: Int,

    @get:NotBlank val personalNumber: String,

    @get:Min(1) @get:Max(100)
    val householdSize: Int,

    val productSubType: ProductSubType
): QuoteRequestData() {
    companion object
}

@optics
data class HouseQuoteRequestData(
    @get:NotBlank val street: String,

    @get:NotBlank val zipCode: String,

    @get:NotBlank val city: String,

    @get:Min(1) @get:Max(1000) val livingSpace: Int,

    @get:NotBlank val personalNumber: String,

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

@optics
data class NorwegianTravelQuoteRequestData(
    @get:NotBlank @get:Pattern(regexp = """\d{4}-\d{2}-\d{2}""")
    val birthDate: String,
    @get:Min(0) @get:Max(100)
    val coInsured: Int,
    val youth: Boolean

): QuoteRequestData() {
    companion object
}

@optics
data class NorwegianHomeContentQuoteRequestData(
    @get:NotBlank val street: String,
    @get:NotBlank @get:Pattern(regexp = """\d{4}""") val zipCode: String,
    @get:NotBlank val city: String,
    @get:Min(1) @get:Max(1000) val livingSpace: Int,
    @get:NotBlank @get:Pattern(regexp = """\d{4}-\d{2}-\d{2}""") val birthDate: String,
    @get:Min(0) @get:Max(100) val coInsured: Int,
    val youth: Boolean,
    @get:NotBlank @get:Pattern(regexp = """(OWN|RENT)""") val productSubType: String

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
    HOME, // Deprecated use SWEDISH_APARTMENT
    HOUSE, // Deprecated use SWEDISH_HOUSE
    SWEDISH_APARTMENT,
    SWEDISH_HOUSE,
    NORWEGIAN_TRAVEL,
    NORWEGIAN_HOME_CONTENT
}
