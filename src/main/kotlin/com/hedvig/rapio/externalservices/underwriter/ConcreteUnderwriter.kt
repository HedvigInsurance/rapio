package com.hedvig.rapio.externalservices.underwriter

import com.hedvig.rapio.externalservices.underwriter.transport.*
import org.javamoney.moneta.Money
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.*

data class IncompleteQuoteReference(
        val id: String
)

@Profile("!fakes")
@Component
class ConcreteUnderwriter(private val client:UnderwriterClient) :Underwriter {
    override fun createQuote(productType: ProductType, lineOfBusiness: LineOfBusiness, quoteData: IncompleteHomeQuoteDataDto, sourceId: UUID, ssn:String): IncompleteQuoteReference {

        val result = client.postIncompleteQuote(PostIncompleteQuoteRequest(productType, lineOfBusiness, ssn = ssn, incompleteQuoteDataDto = quoteData))
        val body = result.body!!

        return IncompleteQuoteReference(
                id = body.id)
    }

    override fun updateQuote(quoteId: String, quoteData: IncompleteQuoteDto): IncompleteQuoteDto {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun completeQuote(quoteId: String): CompleteQuoteReference {
        val response = this.client.createCompleteQuote(quoteId)
        if (response.statusCode.is2xxSuccessful) {
            return CompleteQuoteReference(
                    id = response.body!!.id,
                    price = Money.of(response.body!!.price, "SEK")
            )
        }
        throw RuntimeException("Could not complete incomplete quote with id $quoteId")
    }

    override fun signQuote(id: String, email: String, startsAt: LocalDate?, firstName: String, lastName: String): SignQuoteResponse? {

        val response = this.client.signQuote(id, SignQuoteRequest(Name(firstName, lastName), null, email))

        return SignQuoteResponse(id)
    }

}