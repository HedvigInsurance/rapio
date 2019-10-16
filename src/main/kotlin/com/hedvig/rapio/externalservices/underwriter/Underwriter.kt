package com.hedvig.rapio.externalservices.underwriter

import arrow.core.Either
import com.hedvig.rapio.externalservices.underwriter.transport.ErrorResponse
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteHomeQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.LineOfBusiness
import com.hedvig.rapio.externalservices.underwriter.transport.ProductType
import com.hedvig.rapio.externalservices.underwriter.transport.SignedQuoteResponseDto
import java.time.Instant
import java.time.LocalDate
import java.util.*
import javax.money.MonetaryAmount

interface Underwriter {

    fun createQuote(
            productType: ProductType,
            lineOfBusiness: LineOfBusiness,
            quoteData: IncompleteHomeQuoteDataDto,
            sourceId: UUID,
            ssn:String): IncompleteQuoteReference
    fun updateQuote(quoteId:String, quoteData: IncompleteQuoteDto): IncompleteQuoteDto
    fun completeQuote(quoteId: String): Either<ErrorResponse, CompleteQuoteReference>
    fun signQuote(id: String, email: String, startsAt: LocalDate?, firstName: String, lastName: String) : Either<ErrorResponse, SignedQuoteResponseDto>
}

data class PostIncompleteQuoteResult (
        val id: String,
        val productType: ProductType,
        val quoteInitiatedFrom: QuoteInitiatedFrom
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

enum class QuoteInitiatedFrom {
    PARTNER,
    WEBONBOARDING,
    APP
}

//enum class ProductType {
//    HOME,
//    HOUSE,
//    OBJECT,
//    UNKNOWN
//}

enum class QuoteState {
    INCOMPLETE,
    QUOTED,
    SIGNED,
    EXPIRED
}

/*
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
} */

data class CompleteQuoteReference(
        val id: String,
        val price: MonetaryAmount,
        val validTo: Instant
)

sealed class CompleteQuoteData {}

data class CompleteHouseQuoteData(
        val street: String,
        val zipcode: String,
        val city: String,
        val livingSpace: Int,
        val personalNumber: String,
        val householdSize: Int
) : CompleteQuoteData()

data class CompleteHomeQuoteData(
        val street: String,
        val zipCode: String,
        val city: String,
        val numberOfRooms: Int,
        val livingSpace: Int,
        val personalNumber: String,
        val householdSize: Int
) : CompleteQuoteData()