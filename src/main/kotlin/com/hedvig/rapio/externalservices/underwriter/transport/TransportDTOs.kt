package com.hedvig.rapio.externalservices.underwriter.transport

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import javax.validation.Valid

enum class ProductType {
    APARTMENT,
    HOUSE,
    OBJECT,
    UNKNOWN
}

enum class ApartmentProductSubType {
    BRF,
    RENT,
    RENT_BRF,
    SUBLET_RENTAL,
    SUBLET_BRF,
    STUDENT_BRF,
    STUDENT_RENT,
    LODGER,
    UNKNOWN
}

data class IncompleteQuoteDTO(
        val firstName: String? = null,
        val lastName: String? = null,
        val currentInsurer: String? = null,
        val birthDate: LocalDate?,
        val ssn: String?,
        val quotingPartner: String?,
        val productType: com.hedvig.rapio.quotes.web.dto.ProductType?,
        val incompleteQuoteData: IncompleteQuoteRequestData
)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = IncompleteApartmentQuoteDataDto::class, name = "apartment"),
        JsonSubTypes.Type(value = IncompleteHouseQuoteDataDto::class, name = "house")
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
): IncompleteQuoteRequestData()

data class IncompleteApartmentQuoteDataDto(
    val street: String?,
    val zipCode: String?,
    val city: String?,
    val livingSpace: Int?,
    val householdSize: Int?,
    val floor: Int?,
    val subType: ApartmentProductSubType?
): IncompleteQuoteRequestData()

data class CompleteQuoteResponse(
    val id: String,
    val price: BigDecimal,
    val validTo: Instant?
)
data class PostIncompleteQuoteResult (
    val id: String,
    val productType: ProductType,
    val quoteInitiatedFrom: QuoteInitiatedFrom?
)

enum class QuoteInitiatedFrom {
    RAPIO,
    WEBONBOARDING,
    APP
}

