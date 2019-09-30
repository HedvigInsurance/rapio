package com.hedvig.rapio.externalservices.underwriter

import org.javamoney.moneta.Money
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Component
class FakeUnderwriter():Underwriter {
    override fun signQuote(id: UUID, email: String, startsAt: LocalDate?): SignQuoteResponse? {
        return SignQuoteResponse(id.toString())
    }

    override fun updateQuote(quoteId: String, quoteData: IncompleteQuoteDto): IncompleteQuoteDto {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun completeQuote(quoteId: String): CompleteQuoteDto {
        return CompleteQuoteDto(
                QuoteState.QUOTED,
                Instant.now(),
                productType = ProductType.HOME,
                lineOfBusiness = LineOfBusiness.BRF,
                completeQuoteData = completeQuoteDataDto(completeHomeQuoteDataDto = completeHomeQuoteDataDto("Somewhere!", 3), completeHouseQuoteDataDto = null),
                price = 134,
                quoteInitiatedFrom = QuoteInitiatedFrom.PARTNER)
    }

    override fun createQuote(productType: ProductType, lineOfBusiness: LineOfBusiness, quoteData: IncompleteHomeQuoteDataDto, sourceId: UUID): QuoteCreateResult {
        return QuoteCreateResult("someId", Money.of(133, "SEK"))
    }
}