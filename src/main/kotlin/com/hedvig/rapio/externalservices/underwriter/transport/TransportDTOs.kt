package com.hedvig.rapio.externalservices.underwriter.transport

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.hedvig.libs.logging.masking.Masked
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

enum class ApartmentProductSubType {
    BRF,
    RENT,
    STUDENT_BRF,
    STUDENT_RENT
}

data class IncompleteQuoteDTO(
    @Masked val firstName: String? = null,
    @Masked val lastName: String? = null,
    val currentInsurer: String? = null,
    val birthDate: LocalDate?,
    @Masked val ssn: String?,
    val quotingPartner: String?,
    val productType: ProductType,
    val incompleteQuoteData: IncompleteQuoteRequestData,
    val shouldComplete: Boolean
)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = IncompleteApartmentQuoteDataDto::class, name = "apartment"),
    JsonSubTypes.Type(value = IncompleteHouseQuoteDataDto::class, name = "house"),
    JsonSubTypes.Type(value = IncompleteNorwegianTravelQuoteDataDto::class, name = "norwegianTravel"),
    JsonSubTypes.Type(value = IncompleteNorwegianHomeContentQuoteDataDto::class, name = "norwegianHomeContents"),
    JsonSubTypes.Type(value = IncompleteDanishHomeContentQuoteDataDto::class, name = "danishHomeContents"),
    JsonSubTypes.Type(value = IncompleteDanishTravelQuoteDataDto::class, name = "danishTravel"),
    JsonSubTypes.Type(value = IncompleteDanishAccidentQuoteDataDto::class, name = "danishAccident")
)
sealed class IncompleteQuoteRequestData

data class IncompleteHouseQuoteDataDto(
    @Masked val street: String?,
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
    @Masked val street: String?,
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
    @Masked val street: String?,
    val zipCode: String?,
    val city: String?,
    val livingSpace: Int?,
    val coInsured: Int?,
    val youth: Boolean?,
    val subType: String
) : IncompleteQuoteRequestData()

data class IncompleteDanishHomeContentQuoteDataDto(
    @Masked val street: String?,
    val apartment: String?,
    val floor: String?,
    val zipCode: String?,
    val city: String?,
    @Masked val bbrId: String?,
    val livingSpace: Int?,
    val coInsured: Int?,
    val student: Boolean?,
    val subType: String
) : IncompleteQuoteRequestData()

data class IncompleteDanishTravelQuoteDataDto(
    @Masked val street: String?,
    val apartment: String?,
    val floor: String?,
    val zipCode: String?,
    val city: String?,
    @Masked val bbrId: String?,
    val coInsured: Int?,
    val student: Boolean?,
    val travelArea: String?
) : IncompleteQuoteRequestData()

data class IncompleteDanishAccidentQuoteDataDto(
    @Masked val street: String?,
    val apartment: String?,
    val floor: String?,
    val zipCode: String?,
    val city: String?,
    @Masked val bbrId: String?,
    val coInsured: Int?,
    val student: Boolean?
) : IncompleteQuoteRequestData()

data class CompleteQuoteResponse(
    val id: String,
    val price: BigDecimal,
    val currency: String,
    val validTo: Instant?
)