package com.hedvig.rapio.comparison

import com.hedvig.rapio.comparison.domain.ComparisonQuote
import com.hedvig.rapio.comparison.domain.QuoteData
import com.hedvig.rapio.comparison.domain.QuoteRequestRepository
import com.hedvig.rapio.comparison.web.dto.QuoteRequestDTO
import com.hedvig.rapio.comparison.web.dto.QuoteResponseDTO
import com.hedvig.rapio.comparison.web.dto.SignRequestDTO
import com.hedvig.rapio.comparison.web.dto.SignResponseDTO
import com.hedvig.rapio.externalservices.underwriter.Underwriter
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteHomeQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.LineOfBusiness
import com.hedvig.rapio.externalservices.underwriter.transport.ProductType
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.kotlin.attach
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class QuoteServiceImpl(
        val jdbi: Jdbi,
        val underwriter:Underwriter
) : QuoteService {

    override fun createQuote(requestDTO: QuoteRequestDTO): QuoteResponseDTO {

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
        val cq = request.copy(underwriterQuoteId = completeQuote.id)


        inTransaction<QuoteRequestRepository, Unit, RuntimeException> { repo ->
            repo.updateQuoteRequest(cq)
        }

        return QuoteResponseDTO(
                cq.requestId,
                cq.id,
                cq.getValidTo().epochSecond,
                completeQuote.price)
    }

    override fun signQuote(quoteId: UUID, request: SignRequestDTO): SignResponseDTO {

        val quote = inTransaction<QuoteRequestRepository, ComparisonQuote, RuntimeException> {
            repo -> repo.loadQuoteRequest(quoteId)
        }

        val response = this.underwriter.signQuote(
                quote.underwriterQuoteId!!,
                request.email,
                request.startsAt.date,
                request.firstName,
                request.lastName)

        if(response != null ){

            val signedQuote = quote.copy(signed = true)
            inTransaction<QuoteRequestRepository, Unit, RuntimeException> { repo ->
                repo.updateQuoteRequest(signedQuote)
            }

            return SignResponseDTO(quote.id)
        }

        throw RuntimeException("Could not sign quote")
    }


    private inline fun <reified T : Any, R, E : Exception> inTransaction(crossinline f: (T) -> R) : R {
        return jdbi.inTransaction<R, E> { h ->
            val repository: T = h.attach()
            f(repository)
        }
    }
}