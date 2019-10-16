package com.hedvig.rapio.externalservices.underwriter

import arrow.core.Either
import arrow.core.Right
import com.hedvig.rapio.apikeys.Partners
import com.hedvig.rapio.externalservices.underwriter.transport.ErrorResponse
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteHomeQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.LineOfBusiness
import com.hedvig.rapio.externalservices.underwriter.transport.ProductType
import com.hedvig.rapio.externalservices.underwriter.transport.SignedQuoteResponseDto
import org.javamoney.moneta.Money
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@Profile("fakes")
@Component
class FakeUnderwriter : Underwriter {

    override fun signQuote(id: String, email: String, startsAt: LocalDate?, firstName: String, lastName: String): Either<ErrorResponse, SignedQuoteResponseDto> {
        return Right(SignedQuoteResponseDto(id.toString(), Instant.now()))
    }

    override fun updateQuote(quoteId: String, quoteData: IncompleteQuoteDto): IncompleteQuoteDto {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun completeQuote(quoteId: String): Either<ErrorResponse, CompleteQuoteReference> {
            return Either.Right(CompleteQuoteReference(
                    "",
                    Money.of(123, "SEK"),
                    Instant.now().atZone(ZoneId.of("Europe/Stockholm")).plusMonths(1).toInstant())
            )
    }

    override fun createQuote(productType: ProductType, lineOfBusiness: LineOfBusiness, quoteData: IncompleteHomeQuoteDataDto, sourceId: UUID, source: Partners, ssn:String): IncompleteQuoteReference {
        return IncompleteQuoteReference("someId")
    }
}