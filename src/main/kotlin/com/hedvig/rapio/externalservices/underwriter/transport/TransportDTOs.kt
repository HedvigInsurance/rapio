package com.hedvig.rapio.externalservices.underwriter.transport

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

enum class ApartmentProductSubType {
    BRF,
    RENT,
    STUDENT_BRF,
    STUDENT_RENT,
    UNKNOWN
}

data class IncompleteQuoteDTO(
    val firstName: String? = null,
    val lastName: String? = null,
    val currentInsurer: String? = null,
    val birthDate: LocalDate?,
    val ssn: String?,
    val quotingPartner: String?,
    val productType: ProductType,
    val incompleteQuoteData: IncompleteQuoteRequestData,
    val shouldComplete: Boolean,
    val underwritingGuidelinesBypassedBy: String?
)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = IncompleteApartmentQuoteDataDto::class, name = "apartment"),
    JsonSubTypes.Type(value = IncompleteHouseQuoteDataDto::class, name = "house"),
    JsonSubTypes.Type(value = IncompleteNorwegianTravelQuoteDataDto::class, name = "norwegianTravel"),
    JsonSubTypes.Type(value = IncompleteNorwegianHomeContentQuoteDataDto::class, name = "norwegianHomeContents")
)
sealed class IncompleteQuoteRequestData {
}

data class IncompleteHouseQuoteDataDto(
    val street: String?,
    val zipCode: String?,
    val city: String?,
    val livingSpace: Int?,
    val householdSize: Int?,
    val ancillaryArea: Int?,
    val yearOfConstruction: Int?,
    val numberOfBathrooms: Int?,
    val extraBuildings: List<ExtraBuildingRequestDto>?,
    val isSubleted: Boolean?,
    val floor: Int?
) : IncompleteQuoteRequestData()

data class IncompleteApartmentQuoteDataDto(
    val street: String?,
    val zipCode: String?,
    val city: String?,
    val livingSpace: Int?,
    val householdSize: Int?,
    val floor: Int?,
    val subType: ApartmentProductSubType?
) : IncompleteQuoteRequestData()

data class IncompleteNorwegianTravelQuoteDataDto(
    val coInsured: Int?,
    val youth: Boolean?
) : IncompleteQuoteRequestData()

data class IncompleteNorwegianHomeContentQuoteDataDto(
    val street: String?,
    val zipCode: String?,
    val city: String?,
    val livingSpace: Int?,
    val coInsured: Int?,
    val youth: Boolean?,
    val subType: String
) : IncompleteQuoteRequestData()

data class CompleteQuoteResponse(
    val id: String,
    val price: BigDecimal,
    val validTo: Instant?
)

data class PostIncompleteQuoteResult(
    val id: String
)

enum class QuoteInitiatedFrom {
    RAPIO,
    WEBONBOARDING,
    APP
}

