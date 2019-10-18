package com.hedvig.rapio.externalservices.underwriter.transport

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

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
        val incompleteHouseQuoteData: IncompleteHouseQuoteDataDto? = null,
        val incompleteApartmentQuoteData: IncompleteApartmentQuoteDataDto?
)


data class IncompleteHouseQuoteDataDto(
        val street: String?,
        val zipCode: String?,
        val city: String?,
        val livingSpace: Int?,
        val personalNumber: String?,
        val householdSize: Int?
)

data class IncompleteApartmentQuoteDataDto(
        val street: String?,
        val zipCode: String?,
        val city: String?,
        val livingSpace: Int?,
        val householdSize: Int?,
        val floor: Int?,
        val subType: ApartmentProductSubType?
)

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

