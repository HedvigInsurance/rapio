package com.hedvig.rapio.externalservices.underwriter

import arrow.core.Either
import com.hedvig.rapio.externalservices.underwriter.transport.*
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import javax.money.MonetaryAmount

interface Underwriter {
    fun createQuote(data: IncompleteQuoteDTO): Either<ErrorResponse, CompleteQuoteReference>
    fun quoteBundle(request: QuoteBundleRequestDto) : Either<ErrorResponse, QuoteBundleResponseDto>
    fun signQuote(id: String, email: String, startsAt: LocalDate, firstName: String, lastName: String, ssn: String?) : Either<ErrorResponse, SignedQuoteResponseDto>
    fun signBundle(ids: List<UUID>, email: String, startsAt: LocalDate, firstName: String, lastName: String, ssn: String?, price: String?, currency: String?) : Either<ErrorResponse, SignedQuoteBundleResponseDto>
}

data class CompleteQuoteReference(
    val id: String,
    val price: MonetaryAmount,
    val validTo: Instant
)

