package com.hedvig.rapio.externalservices.underwriter

import arrow.core.Either
import arrow.core.Right
import com.hedvig.rapio.externalservices.underwriter.transport.ErrorResponse
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteQuoteDTO
import com.hedvig.rapio.externalservices.underwriter.transport.SignedQuoteResponseDto
import org.javamoney.moneta.Money
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Profile("fakes")
@Component
class FakeUnderwriter : Underwriter {

    override fun signQuote(id: String, email: String, startsAt: LocalDate, firstName: String, lastName: String, ssn: String?): Either<ErrorResponse, SignedQuoteResponseDto> {
        return Right(SignedQuoteResponseDto(id, "1234", Instant.now()))
    }

    override fun createQuote(data: IncompleteQuoteDTO): Either<ErrorResponse, CompleteQuoteReference> {
        return Right(CompleteQuoteReference("someId", Money.of(10, "SEK"), Instant.now()))
    }
}