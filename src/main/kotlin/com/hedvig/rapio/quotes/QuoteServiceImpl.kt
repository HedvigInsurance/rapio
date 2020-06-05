package com.hedvig.rapio.quotes

import arrow.core.Either
import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.externalservices.apigateway.ApiGateway
import com.hedvig.rapio.externalservices.underwriter.Underwriter
import com.hedvig.rapio.externalservices.underwriter.transport.ApartmentProductSubType
import com.hedvig.rapio.externalservices.underwriter.transport.ErrorCodes
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteApartmentQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteHouseQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteQuoteDTO
import com.hedvig.rapio.externalservices.underwriter.transport.ProductType
import com.hedvig.rapio.quotes.web.dto.ApartmentQuoteRequestData
import com.hedvig.rapio.quotes.web.dto.HouseQuoteRequestData
import com.hedvig.rapio.quotes.web.dto.ProductSubType
import com.hedvig.rapio.quotes.web.dto.QuoteRequestDTO
import com.hedvig.rapio.quotes.web.dto.QuoteResponseDTO
import com.hedvig.rapio.quotes.web.dto.SignRequestDTO
import com.hedvig.rapio.quotes.web.dto.SignResponseDTO
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class QuoteServiceImpl(
  val underwriter: Underwriter,
  val apiGateway: ApiGateway
) : QuoteService {

  override fun createQuote(requestDTO: QuoteRequestDTO, partner: Partner): Either<String, QuoteResponseDTO> {
    val quoteData = requestDTO.quoteData

    val requestQuoteData = when (quoteData) {
      is ApartmentQuoteRequestData -> {
        IncompleteApartmentQuoteDataDto(
          street = quoteData.street,
          zipCode = quoteData.zipCode,
          city = quoteData.city,
          livingSpace = quoteData.livingSpace,
          householdSize = quoteData.householdSize,
          floor = 0,
          subType = if (quoteData.productSubType == ProductSubType.RENT) ApartmentProductSubType.RENT else ApartmentProductSubType.BRF
        )
      }
      is HouseQuoteRequestData -> {
        IncompleteHouseQuoteDataDto(
          street = quoteData.street,
          zipCode = quoteData.zipCode,
          city = quoteData.city,
          livingSpace = quoteData.livingSpace,
          householdSize = quoteData.householdSize,
          ancillaryArea = quoteData.ancillaryArea,
          yearOfConstruction = quoteData.yearOfConstruction,
          numberOfBathrooms = quoteData.numberOfBathrooms,
          extraBuildings = quoteData.extraBuildings,
          isSubleted = quoteData.isSubleted,
          floor = quoteData.floor
        )
      }
    }

    val request = IncompleteQuoteDTO(
      incompleteQuoteData = requestQuoteData,
      firstName = null,
      lastName = null,
      quotingPartner = partner.name,
      birthDate = null,
      ssn = when (quoteData) {
        is ApartmentQuoteRequestData -> quoteData.personalNumber
        is HouseQuoteRequestData -> quoteData.personalNumber
      },
      productType = when (quoteData) {
        is ApartmentQuoteRequestData -> ProductType.APARTMENT
        is HouseQuoteRequestData -> ProductType.HOUSE
      },
      currentInsurer = null,
      shouldComplete = true,
      underwritingGuidelinesBypassedBy = null
    )

    val completeQuote = underwriter.createQuote(request)

    return completeQuote.bimap(
      { it.errorMessage },
      {
        QuoteResponseDTO(
          requestDTO.requestId,
          it.id,
          it.validTo.epochSecond,
          it.price
        )
      }
    )
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
        val completionUrlMaybe: String? = apiGateway.setupPaymentLink(response.b.memberId)

        Either.Right(
          SignResponseDTO(
            requestId = request.requestId,
            quoteId = response.b.id,
            productId = response.b.id,
            signedAt = response.b.signedAt.epochSecond,
            completionUrl = completionUrlMaybe
          )
        )
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