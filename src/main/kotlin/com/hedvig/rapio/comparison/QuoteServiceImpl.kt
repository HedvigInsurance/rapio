package com.hedvig.rapio.comparison

import arrow.core.Either
import com.hedvig.rapio.comparison.domain.ComparisonQuote
import com.hedvig.rapio.comparison.domain.QuoteData
import com.hedvig.rapio.comparison.domain.QuoteRequestRepository
import com.hedvig.rapio.comparison.web.dto.*
import com.hedvig.rapio.externalservices.underwriter.Underwriter
import com.hedvig.rapio.externalservices.underwriter.transport.ErrorCodes
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteHomeQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.LineOfBusiness
import com.hedvig.rapio.externalservices.underwriter.transport.ProductType
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.kotlin.attach
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class QuoteServiceImpl (
        val jdbi: Jdbi,
        val underwriter:Underwriter

) : QuoteService {

    override fun createQuote(requestDTO: QuoteRequestDTO): Either<String, QuoteResponseDTO> {

        val request = ComparisonQuote(UUID.randomUUID(), Instant.now(), requestDTO.requestId, QuoteData.from(requestDTO))

        inTransaction<QuoteRequestRepository, Unit, RuntimeException> { repo ->
            repo.insert(request)
        }

        val quote = underwriter.createQuote(
                ProductType.HOME,
                lineOfBusiness = if (request.quoteData.brf) LineOfBusiness.BRF else LineOfBusiness.RENT,
                quoteData = IncompleteHomeQuoteDataDto(
                        street = request.quoteData.street,
                        zipCode = request.quoteData.zipCode,
                        city = request.quoteData.city,
                        livingSpace =  request.quoteData.livingSpace,
                        householdSize = request.quoteData.householdSize,
                        isStudent = null
                ),
                sourceId = request.id,
                ssn = request.quoteData.personalNumber)

        val completeQuote = underwriter.completeQuote(quoteId = quote.id)

        return when(completeQuote) {
            is Either.Left -> Either.Left(completeQuote.a.errorMessage)
            is Either.Right -> {
                val cq = request.copy(underwriterQuoteId = completeQuote.b.id, validTo = completeQuote.b.validTo)

                inTransaction<QuoteRequestRepository, Unit, RuntimeException> { repo ->
                    repo.updateQuoteRequest(cq)
                }

                Either.Right(QuoteResponseDTO(
                        cq.requestId,
                        cq.id.toString(),
                        cq.validTo!!.epochSecond,
                        completeQuote.b.price)
                )
            }
        }
    }

    override fun signQuote(quoteId: UUID, request: SignRequestDTO): Either<String, SignResponseDTO> {

        val quote = inTransaction<QuoteRequestRepository, ComparisonQuote, RuntimeException> {
            repo -> repo.loadQuoteRequest(quoteId)
        }

        val response = this.underwriter.signQuote(
                quote.underwriterQuoteId!!,
                request.email,
                request.startsAt.date,
                request.firstName,
                request.lastName
        )

        return when (response) {
            is Either.Right -> {
                val signedQuote = quote.copy(signed = true)
                inTransaction<QuoteRequestRepository, Unit, RuntimeException> { repo ->
                    repo.updateQuoteRequest(signedQuote)
                }
                Either.Right(SignResponseDTO(requestId = request.requestId,
                        quoteId = quote.id.toString(), signedAt =  response.b.signedAt.epochSecond))
            }
            is Either.Left -> {
                return when (response.a.errorCode) {
                    ErrorCodes.MEMBER_BREACHES_UW_GUIDELINES -> Either.Left("Cannot sign quote, breaches underwriting guidelines")
                    ErrorCodes.MEMBER_QUOTE_HAS_EXPIRED -> Either.Left("Cannot sign quote, quote has expired")
                    ErrorCodes.MEMBER_HAS_EXISTING_INSURANCE -> Either.Left("Cannot sign quote, already a Hedvig member")
                    ErrorCodes.UNKNOWN_ERROR_CODE -> Either.Left("Something went wrong..")
                }
            }
        }
    }

    private inline fun <reified T : Any, R, E : Exception> inTransaction(crossinline f: (T) -> R) : R {
        return jdbi.inTransaction<R, E> { h ->
            val repository: T = h.attach()
            f(repository)
        }
    }
}