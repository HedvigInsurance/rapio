package com.hedvig.rapio.externalservices.underwriter

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.hedvig.rapio.externalservices.underwriter.transport.*
import com.hedvig.rapio.externalservices.underwriter.transport.ErrorResponse
import feign.FeignException
import mu.KotlinLogging
import org.javamoney.moneta.Money
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Profile("!fakes")
@Component
class ConcreteUnderwriter(
    private val client: UnderwriterClient,
    private val objectMapper: ObjectMapper
) : Underwriter {
    override fun createQuote(data: IncompleteQuoteDTO): Either<ErrorResponse, CompleteQuoteReference> {
        try {
            val result = client.createQuote(data)
            val body = result.body!!

            return Either.Right(
                CompleteQuoteReference(
                    id = body.id,
                    price = Money.of(body.price, body.currency),
                    validTo = Instant.now().atZone(ZoneId.of("Europe/Stockholm")).plusMonths(1).toInstant()
                )
            )
        } catch (ex: FeignException) {
            logger.warn { "Failed to create quote calling Underwriter: $ex" }

            if (ex.status() == 422) {
                val error = objectMapper.readValue<ErrorResponse>(ex.contentUTF8())
                return Either.Left(error)
            }

            if (ex is FeignException.InternalServerError) {
                return Either.Left(ErrorResponse(ErrorCodes.UNKNOWN_ERROR_CODE, "Underwriter error"))
            }

            throw ex
        }
    }

    override fun quoteBundle(request: QuoteBundleRequestDto): Either<ErrorResponse, QuoteBundleResponseDto> {
        try {
            val response = this.client.quoteBundle(request)
            return Either.right(response.body!!)
        } catch (ex: FeignException) {
            logger.warn { "Failed to get quote bundle calling Underwriter: $ex" }

            if (ex is FeignException.InternalServerError) {
                return Either.Left(ErrorResponse(ErrorCodes.UNKNOWN_ERROR_CODE, "Underwriter error"))
            }

            throw ex
        }
    }

    override fun signQuote(id: String, email: String, startsAt: LocalDate?, firstName: String, lastName: String, ssn: String?): Either<ErrorResponse, SignedQuoteResponseDto> {
        try {
            val response = this.client.signQuote(id, SignQuoteRequest(Name(firstName, lastName), ssn, startsAt, email))
            return Either.right(response.body!!)
        } catch (ex: FeignException) {
            logger.warn { "Failed to sign quote calling Underwriter: $ex" }
            if (ex.status() == 422) {
                val error = objectMapper.readValue<ErrorResponse>(ex.contentUTF8())
                return Either.left(error)
            }

            if (ex is FeignException.InternalServerError) {
                return Either.Left(ErrorResponse(ErrorCodes.UNKNOWN_ERROR_CODE, "Underwriter error"))
            }

            throw ex
        }
    }

    override fun signBundle(
        ids: List<UUID>,
        email: String,
        startsAt: LocalDate,
        firstName: String,
        lastName: String,
        ssn: String?,
        price: String?,
        currency: String?
    ): Either<ErrorResponse, SignedQuoteBundleResponseDto> {

        try {
            val response = client.signQuoteBundle(SignQuoteBundleRequest(ids, SignQuoteBundleRequest.Name(firstName, lastName), ssn, startsAt, email, price, currency))
            return Either.right(response.body!!)
        } catch (ex: FeignException) {
            logger.warn { "Failed to get quote bundle calling Underwriter: $ex" }
            if (ex.status() == 422) {
                val error = objectMapper.readValue<ErrorResponse>(ex.contentUTF8())
                return Either.left(error)
            }

            if (ex is FeignException.InternalServerError) {
                return Either.Left(ErrorResponse(ErrorCodes.UNKNOWN_ERROR_CODE, "Underwriter error"))
            }

            throw ex
        }
    }
}
