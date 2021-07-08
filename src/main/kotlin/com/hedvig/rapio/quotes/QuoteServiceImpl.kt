package com.hedvig.rapio.quotes

import arrow.core.Either
import arrow.core.Left
import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.apikeys.Role
import com.hedvig.rapio.external.ExternalMember
import com.hedvig.rapio.external.ExternalMemberRepository
import com.hedvig.rapio.externalservices.apigateway.ApiGateway
import com.hedvig.rapio.externalservices.underwriter.Underwriter
import com.hedvig.rapio.externalservices.underwriter.transport.ApartmentProductSubType
import com.hedvig.rapio.externalservices.underwriter.transport.ErrorCodes
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteApartmentQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteDanishAccidentQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteDanishHomeContentQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteDanishTravelQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteHouseQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteNorwegianHomeContentQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteNorwegianTravelQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteQuoteDTO
import com.hedvig.rapio.externalservices.underwriter.transport.ProductType
import com.hedvig.rapio.externalservices.underwriter.transport.QuoteBundleRequestDto
import com.hedvig.rapio.quotes.web.dto.ApartmentQuoteRequestData
import com.hedvig.rapio.quotes.web.dto.BundleQuotesRequestDTO
import com.hedvig.rapio.quotes.web.dto.BundleQuotesResponseDTO
import com.hedvig.rapio.quotes.web.dto.DanishAccidentQuoteRequestData
import com.hedvig.rapio.quotes.web.dto.DanishHomeContentQuoteRequestData
import com.hedvig.rapio.quotes.web.dto.DanishTravelQuoteRequestData
import com.hedvig.rapio.quotes.web.dto.HouseQuoteRequestData
import com.hedvig.rapio.quotes.web.dto.NorwegianHomeContentQuoteRequestData
import com.hedvig.rapio.quotes.web.dto.NorwegianTravelQuoteRequestData
import com.hedvig.rapio.quotes.web.dto.ProductSubType
import com.hedvig.rapio.quotes.web.dto.QuoteRequestDTO
import com.hedvig.rapio.quotes.web.dto.QuoteResponseDTO
import com.hedvig.rapio.quotes.web.dto.SignBundleRequestDTO
import com.hedvig.rapio.quotes.web.dto.SignBundleResponseDTO
import com.hedvig.rapio.quotes.web.dto.SignRequestDTO
import com.hedvig.rapio.quotes.web.dto.SignResponseDTO
import com.hedvig.rapio.util.getCurrentlyAuthenticatedPartner
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class QuoteServiceImpl(
    val underwriter: Underwriter,
    val externalMemberRepository: ExternalMemberRepository,
    val apiGateway: ApiGateway
) : QuoteService {

    override fun createQuote(requestDTO: QuoteRequestDTO, partner: Partner): QuoteResponseDTO {
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
                    apartment = quoteData.apartment,
                    floor = quoteData.floor,
                    zipCode = quoteData.zipCode,
                    city = quoteData.city,
                    bbrId = quoteData.bbrId,
                    livingSpace = quoteData.livingSpace,
                    coInsured = quoteData.coInsured,
                    student = quoteData.student,
                    subType = quoteData.productSubType
                )
            is DanishTravelQuoteRequestData ->
                IncompleteDanishTravelQuoteDataDto(
                    street = quoteData.street,
                    apartment = quoteData.apartment,
                    floor = quoteData.floor,
                    zipCode = quoteData.zipCode,
                    city = quoteData.city,
                    bbrId = quoteData.bbrId,
                    coInsured = quoteData.coInsured,
                    student = quoteData.student,
                    travelArea = quoteData.travelArea
                )
            is DanishAccidentQuoteRequestData ->
                IncompleteDanishAccidentQuoteDataDto(
                    street = quoteData.street,
                    apartment = quoteData.apartment,
                    floor = quoteData.floor,
                    zipCode = quoteData.zipCode,
                    city = quoteData.city,
                    bbrId = quoteData.bbrId,
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
                is DanishHomeContentQuoteRequestData -> LocalDate.parse(quoteData.birthDate)
                is DanishTravelQuoteRequestData -> LocalDate.parse(quoteData.birthDate)
                is DanishAccidentQuoteRequestData -> LocalDate.parse(quoteData.birthDate)
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

        return QuoteResponseDTO(
            requestDTO.requestId,
            completeQuote.id,
            completeQuote.validTo.epochSecond,
            completeQuote.price
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
        val member = request.externalMemberId?.let {
            externalMemberRepository.findByIdOrNull(it) ?: return Left("No such member: $it")
        }
        val response = this.underwriter.signQuote(
            id = quoteId.toString(),
            email = request.email,
            startsAt = request.startsAt?.date,
            insuranceCompany = request.currentInsuranceCompanyId,
            firstName = request.firstName,
            lastName = request.lastName,
            ssn = request.personalNumber,
            memberId = member?.memberId
        )

        return response.bimap(
            { error ->
                logger.info("Failed to sign bundle: ${error.errorCode} - ${error.errorMessage}")
                when (error.errorCode) {
                    ErrorCodes.MEMBER_BREACHES_UW_GUIDELINES -> "Cannot sign quote, breaches underwriting guidelines"
                    ErrorCodes.MEMBER_QUOTE_HAS_EXPIRED -> "Cannot sign quote, quote has expired"
                    ErrorCodes.MEMBER_HAS_EXISTING_INSURANCE -> "Cannot sign quote, unable to sign member"
                    ErrorCodes.NO_SUCH_QUOTE -> "Quote not found.."
                    ErrorCodes.INVALID_BUNDLING -> "Bundling not supported.."
                    ErrorCodes.UNKNOWN_ERROR_CODE -> "Something went wrong.."
                }
            },
            { response ->
                val completionUrlMaybe: String? = apiGateway.setupPaymentLink(response.memberId, response.market)
                val partner = getCurrentlyAuthenticatedPartner()
                val externalMember =
                    member ?: externalMemberRepository.save(
                        ExternalMember(
                            UUID.randomUUID(),
                            response.memberId,
                            partner
                        )
                    )

                SignResponseDTO(
                    requestId = request.requestId,
                    quoteId = response.id,
                    productId = response.id,
                    externalMemberId = if (partner.role == Role.DISTRIBUTION) externalMember.id else null,
                    signedAt = response.signedAt.epochSecond,
                    completionUrl = completionUrlMaybe
                )
            }
        )
    }

    override fun signBundle(request: SignBundleRequestDTO): Either<String, SignBundleResponseDTO> {
        val member = request.externalMemberId?.let {
            externalMemberRepository.findByIdOrNull(it) ?: return Left("No such member: $it")
        }
        val response = underwriter.signBundle(
            request.quoteIds,
            request.email,
            request.startsAt.date,
            request.firstName,
            request.lastName,
            request.personalNumber,
            request.monthlyPremium.amount,
            request.monthlyPremium.currency,
            member?.memberId
        )

        return response.bimap(
            { error ->
                logger.info("Failed to sign bundle: ${error.errorCode} - ${error.errorMessage}")
                when (error.errorCode) {
                    ErrorCodes.MEMBER_BREACHES_UW_GUIDELINES -> "Cannot sign quote, breaches underwriting guidelines"
                    ErrorCodes.MEMBER_QUOTE_HAS_EXPIRED -> "Cannot sign quote, quote has expired"
                    ErrorCodes.MEMBER_HAS_EXISTING_INSURANCE -> "Cannot sign quote, unable to sign member"
                    ErrorCodes.NO_SUCH_QUOTE -> "Quote not found.."
                    ErrorCodes.INVALID_BUNDLING -> "Bundling not supported.."
                    ErrorCodes.UNKNOWN_ERROR_CODE -> "Something went wrong.."
                }
            },
            { response ->
                val completionUrlMaybe: String? = apiGateway.setupPaymentLink(response.memberId, response.market)
                val partner = getCurrentlyAuthenticatedPartner()
                val externalMember =
                    member ?: externalMemberRepository.save(
                        ExternalMember(
                            UUID.randomUUID(),
                            response.memberId,
                            partner
                        )
                    )

                SignBundleResponseDTO(
                    requestId = request.requestId,
                    productIds = response.contracts.map { contract -> contract.id.toString() }.toList(),
                    externalMemberId = if (partner.role == Role.DISTRIBUTION) externalMember.id else null,
                    signedAt = response.contracts.first().signedAt.epochSecond,
                    completionUrl = completionUrlMaybe
                )
            }
        )
    }

    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)!!
    }
}
