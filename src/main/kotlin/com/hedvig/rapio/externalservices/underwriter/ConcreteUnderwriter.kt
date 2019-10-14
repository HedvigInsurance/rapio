package com.hedvig.rapio.externalservices.underwriter

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.hedvig.rapio.comparison.web.dto.ErrorResponse
import com.hedvig.rapio.externalservices.underwriter.transport.*
import feign.FeignException
import mu.KotlinLogging
import org.javamoney.moneta.Money
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

data class IncompleteQuoteReference(
        val id: String
)

private val logger = KotlinLogging.logger{}

@Profile("!fakes")
@Component
class ConcreteUnderwriter(private val client:UnderwriterClient,
                          private val objectMapper:ObjectMapper) :Underwriter {
    override fun createQuote(productType: ProductType, lineOfBusiness: LineOfBusiness, quoteData: IncompleteHomeQuoteDataDto, sourceId: UUID, ssn:String): IncompleteQuoteReference {

        val result = client.postIncompleteQuote(PostIncompleteQuoteRequest(productType, lineOfBusiness, ssn = ssn, incompleteQuoteDataDto = quoteData))
        val body = result.body!!

        return IncompleteQuoteReference(
                id = body.id)
    }

    override fun updateQuote(quoteId: String, quoteData: IncompleteQuoteDto): IncompleteQuoteDto {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun completeQuote(quoteId: String): Either<ErrorResponse, CompleteQuoteReference> {
        try {
            val response = this.client.createCompleteQuote(quoteId)
            if (response.statusCode.is2xxSuccessful) {
                return Either.Right(CompleteQuoteReference(
                        id = response.body!!.id,
                        price = Money.of(response.body!!.price, "SEK"),
                        validTo = response.body!!.validTo ?: Instant.now().atZone(ZoneId.of("Europe/Stockholm")).plusMonths(1).toInstant()
                ))
            }

        }catch (ex:FeignException) {
            logger.error("Got error calling underwriter: ", ex)

            if(ex.status() == 402) {
                val error = objectMapper.readValue<ErrorResponse>(ex.contentUTF8())
                return Either.Left(error)
            }

        }
        throw RuntimeException("Could not complete incomplete quote with id $quoteId")
    }

    override fun signQuote(id: String, email: String, startsAt: LocalDate?, firstName: String, lastName: String): Either<ErrorResponse, SignedQuoteResponseDto> {
        try {
            val response = this.client.signQuote(id, SignQuoteRequest(Name(firstName, lastName), null, email))
                return Either.right(response.body!!)
        } catch (ex: FeignException) {
            if (ex.status() == 402) {
                val error = objectMapper.readValue<ErrorResponse>(ex.contentUTF8())
                return Either.left(error)
            }
        }
        throw RuntimeException("Couldn't sign member")
    }

}