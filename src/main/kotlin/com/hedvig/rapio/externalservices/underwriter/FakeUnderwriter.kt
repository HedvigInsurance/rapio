package com.hedvig.rapio.externalservices.underwriter

import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteHomeQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.LineOfBusiness
import com.hedvig.rapio.externalservices.underwriter.transport.ProductType
import com.hedvig.rapio.externalservices.underwriter.transport.SignedQuoteResponseDto
import org.javamoney.moneta.Money
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Profile("fakes")
@Component
class FakeUnderwriter : Underwriter {

    override fun signQuote(id: String, email: String, startsAt: LocalDate?, firstName: String, lastName: String): SignedQuoteResponseDto {
        return SignedQuoteResponseDto(id.toString(), Instant.now())
    }

    override fun updateQuote(quoteId: String, quoteData: IncompleteQuoteDto): IncompleteQuoteDto {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun completeQuote(quoteId: String): CompleteQuoteReference {
        return CompleteQuoteReference(
                "",
                Money.of(123, "SEK"),
                null
        )

    }

    override fun createQuote(productType: ProductType, lineOfBusiness: LineOfBusiness, quoteData: IncompleteHomeQuoteDataDto, sourceId: UUID, ssn:String): IncompleteQuoteReference {
        return IncompleteQuoteReference("someId")
    }
}