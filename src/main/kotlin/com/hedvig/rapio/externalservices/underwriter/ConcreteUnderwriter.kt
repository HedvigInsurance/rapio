package com.hedvig.rapio.externalservices.underwriter

import com.hedvig.rapio.externalservices.underwriter.transport.UnderwriterClient
import java.time.LocalDate
import java.util.*

import com.hedvig.rapio.externalservices.underwriter.transport.*
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant

data class IncompleteQuoteReference(
        val id: String
)

@Profile("!fakes")
@Component
class ConcreteUnderwriter(private val client:UnderwriterClient) :Underwriter {
    override fun createQuote(productType: ProductType, lineOfBusiness: LineOfBusiness, quoteData: IncompleteHomeQuoteDataDto, sourceId: UUID): IncompleteQuoteReference {

        val result = client.postIncompleteQuote(PostIncompleteQuoteRequest(productType, lineOfBusiness, ssn = "", incompleteQuoteDataDto = quoteData))
        val body = result.body!!

        return IncompleteQuoteReference(
                id = body.id)
    }

    override fun updateQuote(quoteId: String, quoteData: IncompleteQuoteDto): IncompleteQuoteDto {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun completeQuote(quoteId: String): CompleteQuoteDto {

        return CompleteQuoteDto(
                quoteState =  QuoteState.QUOTED,
                quoteCreatedAt = Instant.now(),
                productType = ProductType.HOME,
                lineOfBusiness = LineOfBusiness.BRF,
                quoteInitiatedFrom = QuoteInitiatedFrom.PARTNER,
                price = 123,
                completeQuoteData = CompleteHomeQuoteData(
                        street = "",
                        householdSize = 1,
                        livingSpace = 122,
                        zipCode = "12345",
                        city = "Stokholm",
                        personalNumber = "",
                        numberOfRooms = 3
                ))
    }

    override fun signQuote(id: UUID, email: String, startsAt: LocalDate?): SignQuoteResponse? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}