package com.hedvig.rapio.quotes

import arrow.core.Either
import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.externalservices.underwriter.Underwriter
import com.hedvig.rapio.externalservices.underwriter.transport.*
import com.hedvig.rapio.quotes.web.dto.*
import org.springframework.stereotype.Service
import java.util.*


@Service
class QuoteServiceImpl(
        val underwriter: Underwriter
) : QuoteService {

    override fun createQuote(requestDTO: QuoteRequestDTO, partner: Partner): Either<String, QuoteResponseDTO> {

        val lineOfBusiness = if (requestDTO.quoteData.productSubType == ProductSubType.RENT) ApartmentProductSubType.RENT else ApartmentProductSubType.BRF


        val quoteData = IncompleteApartmentQuoteDataDto(
                street = requestDTO.quoteData.street,
                zipCode = requestDTO.quoteData.zipCode,
                city = requestDTO.quoteData.city,
                livingSpace = requestDTO.quoteData.livingSpace,
                householdSize = requestDTO.quoteData.householdSize,
                floor = 0,
                subType = lineOfBusiness
        )
        val request = IncompleteQuoteDTO(
                incompleteApartmentQuoteData = quoteData,
                firstName = null,
                lastName = null,
                quotingPartner = partner.name,
                birthDate = null,
                ssn = requestDTO.quoteData.personalNumber,
                currentInsurer = null
                )

        val quote = underwriter.createQuote(request)

        val completeQuote = underwriter.completeQuote(quoteId = quote.id)

        return completeQuote.bimap(
                { it.errorMessage },
                {
                    QuoteResponseDTO(
                            requestDTO.requestId,
                            it.id,
                            it.validTo.epochSecond,
                            it.price)
                })
    }

    override fun signQuote(quoteId: UUID, request: SignRequestDTO): Either<String, SignResponseDTO> {


        val response = this.underwriter.signQuote(
                quoteId.toString(),
                request.email,
                request.startsAt.date,
                request.firstName,
                request.lastName
        )

        return when (response) {
            is Either.Right -> {

                Either.Right(SignResponseDTO(requestId = request.requestId,
                        quoteId = response.b.id, signedAt = response.b.signedAt.epochSecond))
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
}