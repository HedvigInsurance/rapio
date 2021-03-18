package com.hedvig.rapio.quotes

import arrow.core.Either
import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.externalservices.apigateway.ApiGateway
import com.hedvig.rapio.externalservices.underwriter.Underwriter
import com.hedvig.rapio.externalservices.underwriter.transport.*
import com.hedvig.rapio.externalservices.underwriter.transport.ProductType
import com.hedvig.rapio.quotes.web.dto.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
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
          subType = when (quoteData.productSubType) {
            ProductSubType.RENT -> ApartmentProductSubType.RENT
            ProductSubType.BRF -> ApartmentProductSubType.BRF
            ProductSubType.STUDENT_RENT -> ApartmentProductSubType.STUDENT_RENT
            ProductSubType.STUDENT_BRF -> ApartmentProductSubType.STUDENT_BRF
          }
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
      is NorwegianTravelQuoteRequestData ->
        IncompleteNorwegianTravelQuoteDataDto(
          coInsured = quoteData.coInsured,
          youth = quoteData.youth
        )
      is NorwegianHomeContentQuoteRequestData ->
        IncompleteNorwegianHomeContentQuoteDataDto(
          street = quoteData.street,
          zipCode = quoteData.zipCode,
          city = quoteData.city,
          livingSpace = quoteData.livingSpace,
          coInsured = quoteData.coInsured,
          youth = quoteData.youth,
          subType = quoteData.productSubType
        )
      is DanishHomeContentQuoteRequestData ->
        IncompleteDanishHomeContentQuoteDataDto(
          street = quoteData.street,
          apartmentNumber = quoteData.apartmentNumber,
          floor = quoteData.floor,
          zipCode = quoteData.zipCode,
          city = quoteData.city,
          livingSpace = quoteData.livingSpace,
          coInsured = quoteData.coInsured,
          student = quoteData.student,
          subType = quoteData.productSubType
        )
      is DanishTravelQuoteRequestData ->
        IncompleteDanishTravelQuoteDataDto(
          street = quoteData.street,
          apartmentNumber = quoteData.apartmentNumber,
          floor = quoteData.floor,
          zipCode = quoteData.zipCode,
          city = quoteData.city,
          coInsured = quoteData.coInsured,
          student = quoteData.student
        )
      is DanishAccidentQuoteRequestData ->
        IncompleteDanishAccidentQuoteDataDto(
          street = quoteData.street,
          apartmentNumber = quoteData.apartmentNumber,
          floor = quoteData.floor,
          zipCode = quoteData.zipCode,
          city = quoteData.city,
          coInsured = quoteData.coInsured,
          student = quoteData.student
        )
    }

    val request = IncompleteQuoteDTO(
      incompleteQuoteData = requestQuoteData,
      firstName = null,
      lastName = null,
      quotingPartner = partner.name,
      birthDate = when (quoteData) {
        is ApartmentQuoteRequestData -> null
        is HouseQuoteRequestData -> null
        is NorwegianTravelQuoteRequestData -> LocalDate.parse(quoteData.birthDate)
        is NorwegianHomeContentQuoteRequestData -> LocalDate.parse(quoteData.birthDate)
        is DanishHomeContentQuoteRequestData -> null
        is DanishTravelQuoteRequestData -> null
        is DanishAccidentQuoteRequestData -> null
      },
      ssn = when (quoteData) {
        is ApartmentQuoteRequestData -> quoteData.personalNumber
        is HouseQuoteRequestData -> quoteData.personalNumber
        is NorwegianTravelQuoteRequestData -> null
        is NorwegianHomeContentQuoteRequestData -> null
        is DanishHomeContentQuoteRequestData -> null
        is DanishTravelQuoteRequestData -> null
        is DanishAccidentQuoteRequestData -> null
      },
      productType = when (quoteData) {
        is ApartmentQuoteRequestData -> ProductType.APARTMENT
        is HouseQuoteRequestData -> ProductType.HOUSE
        is NorwegianTravelQuoteRequestData -> ProductType.TRAVEL
        is NorwegianHomeContentQuoteRequestData -> ProductType.HOME_CONTENT
        is DanishHomeContentQuoteRequestData -> ProductType.HOME_CONTENT
        is DanishTravelQuoteRequestData -> ProductType.TRAVEL
        is DanishAccidentQuoteRequestData -> ProductType.ACCIDENT
      },
      currentInsurer = null,
      shouldComplete = true
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

  override fun bundleQuotes(request: BundleQuotesRequestDTO): Either<String, BundleQuotesResponseDTO> {

    return when (val response = underwriter.quoteBundle(QuoteBundleRequestDto(request.quoteIds))) {
      is Either.Right -> {
        with(response.b.bundleCost.monthlyNet) {
          Either.right(BundleQuotesResponseDTO.from(request.requestId, amount, currency))
        }
      }
      is Either.Left -> {
        logger.info("Failed to sign bundle: ${response.a.errorCode} - ${response.a.errorMessage}")
        Either.Left(response.a.errorMessage)
      }
    }
  }

  override fun signQuote(quoteId: UUID, request: SignRequestDTO): Either<String, SignResponseDTO> {
    val response = this.underwriter.signQuote(
      quoteId.toString(),
      request.email,
      request.startsAt.date,
      request.firstName,
      request.lastName,
      request.personalNumber
    )

    return when (response) {
      is Either.Right -> {
        val completionUrlMaybe: String? = apiGateway.setupPaymentLink(response.b.memberId, response.b.market)

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
        logger.info("Failed to sign bundle: ${response.a.errorCode} - ${response.a.errorMessage}")
        return when (response.a.errorCode) {
          ErrorCodes.MEMBER_BREACHES_UW_GUIDELINES -> Either.Left("Cannot sign quote, breaches underwriting guidelines")
          ErrorCodes.MEMBER_QUOTE_HAS_EXPIRED -> Either.Left("Cannot sign quote, quote has expired")
          ErrorCodes.MEMBER_HAS_EXISTING_INSURANCE -> Either.Left("Cannot sign quote, already a Hedvig member")
          ErrorCodes.NO_SUCH_QUOTE -> Either.Left("Quote not found..")
          ErrorCodes.INVALID_BUNDLING -> Either.Left("Bundling not supported..")
          ErrorCodes.UNKNOWN_ERROR_CODE -> Either.Left("Something went wrong..")
        }
      }
    }
  }

  override fun signBundle(request: SignBundleRequestDTO): Either<String, SignBundleResponseDTO> {
    val response = underwriter.signBundle(
      request.quoteIds,
      request.email,
      request.startsAt.date,
      request.firstName,
      request.lastName,
      request.personalNumber,
      request.monthlyPremium.amount,
      request.monthlyPremium.currency)

    return response.bimap(
      {
        logger.info("Failed to sign bundle: ${it.errorCode} - ${it.errorMessage}")
        when (it.errorCode) {
          ErrorCodes.MEMBER_BREACHES_UW_GUIDELINES -> "Cannot sign quote, breaches underwriting guidelines"
          ErrorCodes.MEMBER_QUOTE_HAS_EXPIRED -> "Cannot sign quote, quote has expired"
          ErrorCodes.MEMBER_HAS_EXISTING_INSURANCE -> "Cannot sign quote, already a Hedvig member"
          ErrorCodes.NO_SUCH_QUOTE -> "Quote not found.."
          ErrorCodes.INVALID_BUNDLING -> "Bundling not supported.."
          ErrorCodes.UNKNOWN_ERROR_CODE -> "Something went wrong.."
        }
      },
      {
        val completionUrlMaybe: String? = apiGateway.setupPaymentLink(it.memberId, it.market)

        SignBundleResponseDTO(
          requestId = request.requestId,
          productIds = it.contracts.map { it.id.toString() }.toList(),
          signedAt = it.contracts.first().signedAt.epochSecond,
          completionUrl = completionUrlMaybe
        )
      }
    )
  }

  companion object {
    val logger = LoggerFactory.getLogger(this::class.java)!!
  }

}
