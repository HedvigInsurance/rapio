package com.hedvig.rapio.externalservices.underwriter

import com.hedvig.rapio.comparison.web.dto.Date
import java.time.Instant
import java.time.LocalDate
import java.util.*
import javax.money.MonetaryAmount

interface Underwriter {

    fun createQuote(
            productType: ProductType,
            lineOfBusiness: LineOfBusiness,
            quoteData: IncompleteHomeQuoteDataDto,
            sourceId: UUID): QuoteCreateResult
    fun updateQuote(quoteId:String, quoteData: IncompleteQuoteDto): IncompleteQuoteDto
    fun completeQuote(quoteId: String): CompleteQuoteDto
    fun signQuote(id: UUID, email: String, startsAt: LocalDate?) : SignQuoteResponse?
}

data class SignQuoteResponse (
        val id : String
)

data class QuoteCreateResult (
        val id: String,
        val price: MonetaryAmount
)

data class IncompleteQuoteDto(
        val id : String? = null,
        val quoteState: QuoteState,
        val createdAt: Instant,
        val productType: ProductType,
        val lineOfBusiness: LineOfBusiness?,
        val incompleteQuoteDataDto: IncompleteQuoteDataDto?,
        val quoteInitiatedFrom: QuoteInitiatedFrom?,
        val birthDate: LocalDate?,
        val livingSpace: Int?,
        val houseHoldSize: Int?,
        val isStudent: Boolean?
)

data class IncompleteQuoteDataDto(
        val incompleteHouseQuoteDataDto: IncompleteHouseQuoteDataDto?,
        val incompleteHomeQuoteDataDto: IncompleteHomeQuoteDataDto?
)

data class IncompleteHouseQuoteDataDto(
        val street: String?,
        val zipcode: String?,
        val city: String?,
        val livingSpace: Int?,
        val personalNumber: String?,
        val householdSize: Int?
)

data class IncompleteHomeQuoteDataDto(
        val personalNumber: String?,
        val address: String?,
        val livingSpace: Int?,
        val zipCode: String?
)

enum class QuoteInitiatedFrom {
    PARTNER,
    WEBONBOARDING,
    APP
}

enum class ProductType {
    HOME,
    HOUSE,
    OBJECT,
    UNKNOWN
}

enum class QuoteState {
    INCOMPLETE,
    QUOTED,
    SIGNED,
    EXPIRED
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

data class CompleteQuoteDto (
        val quoteState: QuoteState,
        val quoteCreatedAt: Instant,
        val productType: ProductType,
        val lineOfBusiness: LineOfBusiness,
        val completeQuoteData: completeQuoteDataDto,
        val price: Int,
        val quoteInitiatedFrom: QuoteInitiatedFrom
)

data class completeQuoteDataDto(
        val completeHouseQuoteDataDto: completeHouseQuoteDataDto?,
        val completeHomeQuoteDataDto: completeHomeQuoteDataDto
)

data class completeHouseQuoteDataDto(
        val street: String,
        val zipcode: String,
        val city: String,
        val livingSpace: Int,
        val personalNumber: String,
        val householdSize: Int
)

data class completeHomeQuoteDataDto(
        val address: String,
        val numberOfRooms: Int
)