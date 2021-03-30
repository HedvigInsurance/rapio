package com.hedvig.rapio.quotes.web.dto

import arrow.optics.optics
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.hedvig.libs.logging.masking.Masked
import com.hedvig.rapio.externalservices.underwriter.transport.ExtraBuildingRequestDto
import javax.validation.Valid
import javax.validation.constraints.*

@optics
data class QuoteRequestDTO(
    val requestId: String,
    @get:Valid val productType: ProductType,
    @get:Valid
    @set:JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "productType")
    @JsonSubTypes(
        JsonSubTypes.Type(value = ApartmentQuoteRequestData::class, name = "HOME"), // Deprecated use SWEDISH_APARTMENT
        JsonSubTypes.Type(value = HouseQuoteRequestData::class, name = "HOUSE"),  // Deprecated use SWEDISH_HOUSE
        JsonSubTypes.Type(value = ApartmentQuoteRequestData::class, name = "SWEDISH_APARTMENT"),
        JsonSubTypes.Type(value = HouseQuoteRequestData::class, name = "SWEDISH_HOUSE"),
        JsonSubTypes.Type(value = NorwegianTravelQuoteRequestData::class, name = "NORWEGIAN_TRAVEL"),
        JsonSubTypes.Type(value = NorwegianHomeContentQuoteRequestData::class, name = "NORWEGIAN_HOME_CONTENT"),
        JsonSubTypes.Type(value = DanishHomeContentQuoteRequestData::class, name = "DANISH_HOME_CONTENT"),
        JsonSubTypes.Type(value = DanishTravelQuoteRequestData::class, name = "DANISH_TRAVEL"),
        JsonSubTypes.Type(value = DanishAccidentQuoteRequestData::class, name = "DANISH_ACCIDENT")
    )
    var quoteData: QuoteRequestData
) {
    companion object
}

sealed class QuoteRequestData {
}

@optics
data class ApartmentQuoteRequestData(
    @get:NotBlank @Masked val street: String,

    @get:NotBlank val zipCode: String,

    @get:NotBlank val city: String,

    @get:Min(1) @get:Max(1000) val livingSpace: Int,

    @get:NotBlank @Masked val personalNumber: String,

    @get:Min(1) @get:Max(100)
    val householdSize: Int,

    val productSubType: ProductSubType
): QuoteRequestData() {
    companion object
}

@optics
data class HouseQuoteRequestData(
    @get:NotBlank @Masked val street: String,

    @get:NotBlank val zipCode: String,

    @get:NotBlank val city: String,

    @get:Min(1) @get:Max(1000) val livingSpace: Int,

    @get:NotBlank @Masked val personalNumber: String,

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
    @get:NotBlank @Masked val street: String,
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

@optics
data class DanishHomeContentQuoteRequestData(
    @get:NotBlank @Masked val street: String,
    val apartment: String?,
    @get:NotBlank @get:Pattern(regexp = """\d{3,4}""") val zipCode: String,
    val city: String?,
    val bbrId: String?,
    @get:Min(1) @get:Max(1000) val livingSpace: Int?,
    @get:Min(0) @get:Max(100) val coInsured: Int,
    @get:NotBlank @get:Pattern(regexp = """\d{4}-\d{2}-\d{2}""")
    val birthDate: String,
    val student: Boolean,
    @get:NotBlank @get:Pattern(regexp = """(OWN|RENT)""") val productSubType: String

): QuoteRequestData() {
    companion object
}

@optics
data class DanishTravelQuoteRequestData(
    @get:NotBlank @Masked val street: String,
    val apartment: String?,
    @get:NotBlank @get:Pattern(regexp = """\d{3,4}""") val zipCode: String,
    val city: String?,
    @Masked val bbrId: String?,
    @get:Min(0) @get:Max(100) val coInsured: Int,
    @get:NotBlank @get:Pattern(regexp = """\d{4}-\d{2}-\d{2}""")
    val birthDate: String,
    val student: Boolean,
    @get:NotBlank @get:Pattern(regexp = """(WHOLE_WORLD|NON_US_CANADA)""") val travelArea: String

): QuoteRequestData() {
    companion object
}

@optics
data class DanishAccidentQuoteRequestData(
    @get:NotBlank @Masked val street: String,
    val apartment: String?,
    @get:NotBlank @get:Pattern(regexp = """\d{3,4}""") val zipCode: String,
    val city: String?,
    @Masked val bbrId: String?,
    @get:Min(0) @get:Max(100) val coInsured: Int,
    @get:NotBlank @get:Pattern(regexp = """\d{4}-\d{2}-\d{2}""")
    val birthDate: String,
    val student: Boolean

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
    NORWEGIAN_HOME_CONTENT,
    DANISH_HOME_CONTENT,
    DANISH_TRAVEL,
    DANISH_ACCIDENT
}
