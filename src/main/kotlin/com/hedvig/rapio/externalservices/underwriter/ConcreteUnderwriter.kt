package com.hedvig.rapio.externalservices.underwriter

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.hedvig.rapio.externalservices.underwriter.transport.ErrorCodes
import com.hedvig.rapio.externalservices.underwriter.transport.ErrorResponse
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteQuoteDTO
import com.hedvig.rapio.externalservices.underwriter.transport.InsuranceCompanyDto
import com.hedvig.rapio.externalservices.underwriter.transport.QuoteBundleRequestDto
import com.hedvig.rapio.externalservices.underwriter.transport.QuoteBundleResponseDto
import com.hedvig.rapio.externalservices.underwriter.transport.SignQuoteBundleRequest
import com.hedvig.rapio.externalservices.underwriter.transport.SignQuoteRequest
import com.hedvig.rapio.externalservices.underwriter.transport.SignedQuoteBundleResponseDto
import com.hedvig.rapio.externalservices.underwriter.transport.SignedQuoteResponseDto
import com.hedvig.rapio.externalservices.underwriter.transport.UnderwriterClient
import com.neovisionaries.i18n.CountryCode
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
    override fun createQuote(data: IncompleteQuoteDTO): CompleteQuoteReference {
        val result = client.createQuote(data)
        val body = result.body!!

        return CompleteQuoteReference(
            id = body.id,
            price = Money.of(body.price, body.currency),
            validTo = Instant.now().atZone(ZoneId.of("Europe/Stockholm")).plusMonths(1).toInstant()
        )
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

    override fun signQuote(
        id: String,
        email: String,
        startsAt: LocalDate?,
        insuranceCompany: String?,
        firstName: String,
        lastName: String,
        ssn: String?,
        memberId: String?
    ): Either<ErrorResponse, SignedQuoteResponseDto> {
        try {
            val response = this.client.signQuote(
                id,
                SignQuoteRequest(
                    name = SignQuoteRequest.Name(firstName.trim(), lastName.trim()),
                    ssn = ssn?.trim(),
                    startDate = startsAt,
                    insuranceCompany = insuranceCompany,
                    email = email.trim(),
                    memberId = memberId
                )
            )
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
        currency: String?,
        memberId: String?
    ): Either<ErrorResponse, SignedQuoteBundleResponseDto> {

        try {
            val response = client.signQuoteBundle(
                SignQuoteBundleRequest(
                    quoteIds = ids,
                    name = SignQuoteBundleRequest.Name(firstName.trim(), lastName.trim()),
                    ssn = ssn?.trim(),
                    startDate = startsAt,
                    email = email.trim(),
                    price = price,
                    currency = currency,
                    memberId = memberId
                )
            )
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

    override fun getInsuranceCompanies(countryCode: CountryCode): List<InsuranceCompanyDto> {
        return client.getInsuranceCompanies(countryCode).body!!
    }
}
