package com.hedvig.rapio.externalservices.underwriter.transport

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.hedvig.rapio.externalservices.underwriter.IncompleteHouseQuoteDataDto
import java.util.*

enum class ProductType {
    HOME,
    HOUSE,
    OBJECT,
    UNKNOWN
}
enum class LineOfBusiness {
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


data class PostIncompleteQuoteRequest (
        val productType: ProductType,
        val lineOfBusiness: LineOfBusiness?,
        val ssn: String?,
        val incompleteQuoteDataDto: QuoteData?
)


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(JsonSubTypes.Type(value = IncompleteHouseQuoteDataDto::class, name =  "house"),
JsonSubTypes.Type(value = IncompleteHomeQuoteDataDto::class, name =  "home"))
sealed class QuoteData

data class IncompleteHouseQuoteDataDto(
        val street: String?,
        val zipCode: String?,
        val city: String?,
        val livingSpace: Int?,
        val householdSize: Int?,
        val isStudent: Boolean?
) : QuoteData()
data class IncompleteHomeQuoteDataDto(
        val street: String?,
        val city: String?,
        val zipCode: String?,
        val isStudent: Boolean?,
        val livingSpace: Int?,
        val houseHoldSize: Int?
) : QuoteData()

data class CompleteQuoteResponseDto (
        val id: UUID,
        val price: Int
)
