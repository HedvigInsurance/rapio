package com.hedvig.rapio.externalservices.underwriter

import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteHomeQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.LineOfBusiness
import com.hedvig.rapio.externalservices.underwriter.transport.ProductType
import org.javamoney.moneta.Money
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Profile("fakes")
@Component()
class FakeUnderwriter(
) : Underwriter {

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
                completeQuoteData = CompleteHomeQuoteData("Somewhere!", "3", "", 3, 3, "", 3),
                price = 134,
                quoteInitiatedFrom = QuoteInitiatedFrom.PARTNER)
    }

    override fun createQuote(productType: ProductType, lineOfBusiness: LineOfBusiness, quoteData: IncompleteHomeQuoteDataDto, sourceId: UUID): IncompleteQuoteReference {
        return IncompleteQuoteReference("someId")
    }
}