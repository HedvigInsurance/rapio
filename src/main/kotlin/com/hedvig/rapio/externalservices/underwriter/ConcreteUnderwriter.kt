package com.hedvig.rapio.externalservices.underwriter

import com.hedvig.rapio.externalservices.underwriter.transport.UnderwriterClient
import java.time.LocalDate
import java.util.*

import com.hedvig.rapio.externalservices.underwriter.transport.*
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import javax.money.MonetaryAmount

data class Quote(
        val id: String,
        val price: MonetaryAmount
)

@Profile("!fakes")
@Component
class ConcreteUnderwriter(private val client:UnderwriterClient) :Underwriter {
    override fun createQuote(productType: ProductType, lineOfBusiness: LineOfBusiness, quoteData: IncompleteHomeQuoteDataDto, sourceId: UUID): Quote {

        val result = client.postIncompleteQuote(PostIncompleteQuoteRequest(productType, lineOfBusiness, ssn = "", incompleteQuoteDataDto = quoteData))
        val body = result.body!!

        return Quote(id = body.id, price = body.price)
    }

    override fun updateQuote(quoteId: String, quoteData: IncompleteQuoteDto): IncompleteQuoteDto {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun completeQuote(quoteId: String): CompleteQuoteDto {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun signQuote(id: UUID, email: String, startsAt: LocalDate?): SignQuoteResponse? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}