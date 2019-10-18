package com.hedvig.rapio.externalservices.underwriter

import arrow.core.Either
import com.hedvig.rapio.externalservices.underwriter.transport.*
import java.time.Instant
import java.time.LocalDate
import javax.money.MonetaryAmount

interface Underwriter {

    fun completeQuote(quoteId: String): Either<ErrorResponse, CompleteQuoteReference>
    fun signQuote(id: String, email: String, startsAt: LocalDate, firstName: String, lastName: String) : Either<ErrorResponse, SignedQuoteResponseDto>
    fun createQuote(data: IncompleteQuoteDTO): IncompleteQuoteReference
}

data class CompleteQuoteReference(
        val id: String,
        val price: MonetaryAmount,
        val validTo: Instant
)

