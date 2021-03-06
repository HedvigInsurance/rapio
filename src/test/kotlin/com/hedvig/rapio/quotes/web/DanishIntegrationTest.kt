package com.hedvig.rapio.quotes.web

import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.external.ExternalMember
import com.hedvig.rapio.external.ExternalMemberRepository
import com.hedvig.rapio.externalservices.apigateway.transport.ApiGatewayClient
import com.hedvig.rapio.externalservices.apigateway.transport.CreateSetupPaymentLinkRequestDto
import com.hedvig.rapio.externalservices.apigateway.transport.CreateSetupPaymentLinkResponseDto
import com.hedvig.rapio.externalservices.memberService.MemberServiceClient
import com.hedvig.rapio.externalservices.paymentService.transport.PaymentServiceClient
import com.hedvig.rapio.externalservices.productPricing.transport.ProductPricingClient
import com.hedvig.rapio.externalservices.underwriter.transport.CompleteQuoteResponse
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteDanishAccidentQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteDanishHomeContentQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteDanishTravelQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteQuoteDTO
import com.hedvig.rapio.externalservices.underwriter.transport.SignQuoteRequest
import com.hedvig.rapio.externalservices.underwriter.transport.SignedQuoteResponseDto
import com.hedvig.rapio.externalservices.underwriter.transport.UnderwriterClient
import com.hedvig.rapio.quotes.web.dto.QuoteResponseDTO
import com.hedvig.rapio.quotes.web.dto.SignResponseDTO
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(secure = false)
@ActiveProfiles(profiles = ["noauth"])
class DanishIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @MockkBean(relaxed = true)
    lateinit var apiGatewayClient: ApiGatewayClient

    @MockkBean(relaxed = true)
    lateinit var paymentServiceClient: PaymentServiceClient

    @MockkBean(relaxed = true)
    lateinit var productPricingServiceClient: ProductPricingClient

    @MockkBean(relaxed = true)
    lateinit var underwriterClient: UnderwriterClient

    @MockkBean(relaxed = true)
    lateinit var memberServiceClient: MemberServiceClient

    @MockkBean(relaxed = true)
    lateinit var externalMemberRepository: ExternalMemberRepository

    /*
        Danish test cpr numbers (ssn):
            0505059996
            0505109990
            0505159995
            0505209996
            0506889996
            1007059995
            1110109996
            1310169995
        Format: DDMMYYSSSS
     */

    @Test
    @WithMockUser("COMPRICER", roles = ["COMPARISON"])
    fun create_home_content_quote() {

        val uwQuoteId = UUID.randomUUID().toString()
        val uwQuoteRequest = slot<IncompleteQuoteDTO>()
        val uwQuoteResponse =
            CompleteQuoteResponse(
                id = uwQuoteId,
                price = BigDecimal(250.0),
                currency = "DKK",
                validTo = Instant.now().atZone(ZoneId.of("Europe/Stockholm")).plusMonths(1).toInstant()
            )

        every { underwriterClient.createQuote(capture(uwQuoteRequest)) } returns ResponseEntity.ok(uwQuoteResponse)

        val requestData = """
            {
              "requestId": "apa",
              "quoteData": {
                "street": "ApStreet 1234",
                "apartment": "10",
                "floor": "th",
                "zipCode": "1234",
                "city": "ApCity",
                "bbrId": "12345",
                "livingSpace": 60,
                "coInsured": 0,
                "birthDate": "1971-01-20",
                "student": true,
                "productSubType": "OWN"
              },
              "productType": "DANISH_HOME_CONTENT"
            }
        """.trimIndent()

        val response = postJson<QuoteResponseDTO>("/v1/quotes", requestData)

        assertThat(response.statusCode.value()).isEqualTo(200)

        with(response.body!!) {
            assertThat(requestId).isEqualTo("apa")
            assertThat(quoteId).isEqualTo(uwQuoteId)
            assertThat(validUntil).isGreaterThan(Instant.now().epochSecond)
            assertThat(monthlyPremium.toString()).isEqualTo("DKK 250")
        }

        assertThat(uwQuoteRequest.captured.birthDate).isEqualTo("1971-01-20")
        assertThat(uwQuoteRequest.captured.currentInsurer).isNull()
        assertThat(uwQuoteRequest.captured.firstName).isNull()
        assertThat(uwQuoteRequest.captured.lastName).isNull()
        assertThat(uwQuoteRequest.captured.ssn).isNull()
        assertThat(uwQuoteRequest.captured.productType.name).isEqualTo("HOME_CONTENT")
        assertThat(uwQuoteRequest.captured.shouldComplete).isTrue()

        val incompleteQuoteData = uwQuoteRequest.captured.incompleteQuoteData as IncompleteDanishHomeContentQuoteDataDto

        assertThat(incompleteQuoteData.street).isEqualTo("ApStreet 1234")
        assertThat(incompleteQuoteData.apartment).isEqualTo("10")
        assertThat(incompleteQuoteData.floor).isEqualTo("th")
        assertThat(incompleteQuoteData.zipCode).isEqualTo("1234")
        assertThat(incompleteQuoteData.city).isEqualTo("ApCity")
        assertThat(incompleteQuoteData.bbrId).isEqualTo("12345")
        assertThat(incompleteQuoteData.livingSpace).isEqualTo(60)
        assertThat(incompleteQuoteData.coInsured).isEqualTo(0)
        assertThat(incompleteQuoteData.student).isTrue()
        assertThat(incompleteQuoteData.subType).isEqualTo("OWN")
    }

    @Test
    @WithMockUser("COMPRICER", roles = ["COMPARISON"])
    fun create_travel_quote() {

        val uwQuoteId = UUID.randomUUID().toString()
        val uwQuoteRequest = slot<IncompleteQuoteDTO>()
        val uwQuoteResponse =
            CompleteQuoteResponse(
                id = uwQuoteId,
                price = BigDecimal(250.0),
                currency = "DKK",
                validTo = Instant.now().atZone(ZoneId.of("Europe/Stockholm")).plusMonths(1).toInstant()
            )

        every { underwriterClient.createQuote(capture(uwQuoteRequest)) } returns ResponseEntity.ok(uwQuoteResponse)

        val requestData = """
            {
              "requestId": "apa",
              "quoteData": {
                "street": "ApStreet 1234",
                "apartment": "10",
                "floor": "th",
                "bbrId": "12345",
                "zipCode": "1234",
                "city": "ApCity",
                "coInsured": 0,
                "birthDate": "1970-01-20",
                "student": true,
                "travelArea": "WHOLE_WORLD"
              },
              "productType": "DANISH_TRAVEL"
            }
        """.trimIndent()

        val response = postJson<QuoteResponseDTO>("/v1/quotes", requestData)

        assertThat(response.statusCode.value()).isEqualTo(200)

        with(response.body!!) {
            assertThat(requestId).isEqualTo("apa")
            assertThat(quoteId).isEqualTo(uwQuoteId)
            assertThat(validUntil).isGreaterThan(Instant.now().epochSecond)
            assertThat(monthlyPremium.toString()).isEqualTo("DKK 250")
        }

        assertThat(uwQuoteRequest.captured.birthDate).isEqualTo("1970-01-20")
        assertThat(uwQuoteRequest.captured.currentInsurer).isNull()
        assertThat(uwQuoteRequest.captured.firstName).isNull()
        assertThat(uwQuoteRequest.captured.lastName).isNull()
        assertThat(uwQuoteRequest.captured.ssn).isNull()
        assertThat(uwQuoteRequest.captured.productType.name).isEqualTo("TRAVEL")
        assertThat(uwQuoteRequest.captured.shouldComplete).isTrue()

        val incompleteQuoteData = uwQuoteRequest.captured.incompleteQuoteData as IncompleteDanishTravelQuoteDataDto

        assertThat(incompleteQuoteData.street).isEqualTo("ApStreet 1234")
        assertThat(incompleteQuoteData.apartment).isEqualTo("10")
        assertThat(incompleteQuoteData.floor).isEqualTo("th")
        assertThat(incompleteQuoteData.zipCode).isEqualTo("1234")
        assertThat(incompleteQuoteData.city).isEqualTo("ApCity")
        assertThat(incompleteQuoteData.bbrId).isEqualTo("12345")
        assertThat(incompleteQuoteData.coInsured).isEqualTo(0)
        assertThat(incompleteQuoteData.student).isTrue()
        assertThat(incompleteQuoteData.travelArea).isEqualTo("WHOLE_WORLD")
    }

    @Test
    @WithMockUser("COMPRICER", roles = ["COMPARISON"])
    fun create_accident_quote() {

        val uwQuoteId = UUID.randomUUID().toString()
        val uwQuoteRequest = slot<IncompleteQuoteDTO>()
        val uwQuoteResponse =
            CompleteQuoteResponse(
                id = uwQuoteId,
                price = BigDecimal(250.0),
                currency = "DKK",
                validTo = Instant.now().atZone(ZoneId.of("Europe/Stockholm")).plusMonths(1).toInstant()
            )

        every { underwriterClient.createQuote(capture(uwQuoteRequest)) } returns ResponseEntity.ok(uwQuoteResponse)

        val requestData = """
            {
              "requestId": "apa",
              "quoteData": {
                "street": "ApStreet 1234",
                "apartment": "10A",
                "floor": "1",
                "zipCode": "1234",
                "city": "ApCity",
                "bbrId": "12345",
                "coInsured": 0,
                "birthDate": "1970-01-20",
                "student": true
              },
              "productType": "DANISH_ACCIDENT"
            }
        """.trimIndent()

        val response = postJson<QuoteResponseDTO>("/v1/quotes", requestData)

        assertThat(response.statusCode.value()).isEqualTo(200)

        with(response.body!!) {
            assertThat(requestId).isEqualTo("apa")
            assertThat(quoteId).isEqualTo(uwQuoteId)
            assertThat(validUntil).isGreaterThan(Instant.now().epochSecond)
            assertThat(monthlyPremium.toString()).isEqualTo("DKK 250")
        }

        assertThat(uwQuoteRequest.captured.birthDate).isEqualTo("1970-01-20")
        assertThat(uwQuoteRequest.captured.currentInsurer).isNull()
        assertThat(uwQuoteRequest.captured.firstName).isNull()
        assertThat(uwQuoteRequest.captured.lastName).isNull()
        assertThat(uwQuoteRequest.captured.ssn).isNull()
        assertThat(uwQuoteRequest.captured.productType.name).isEqualTo("ACCIDENT")
        assertThat(uwQuoteRequest.captured.shouldComplete).isTrue()

        val incompleteQuoteData = uwQuoteRequest.captured.incompleteQuoteData as IncompleteDanishAccidentQuoteDataDto

        assertThat(incompleteQuoteData.street).isEqualTo("ApStreet 1234")
        assertThat(incompleteQuoteData.apartment).isEqualTo("10A")
        assertThat(incompleteQuoteData.floor).isEqualTo("1")
        assertThat(incompleteQuoteData.zipCode).isEqualTo("1234")
        assertThat(incompleteQuoteData.city).isEqualTo("ApCity")
        assertThat(incompleteQuoteData.bbrId).isEqualTo("12345")
        assertThat(incompleteQuoteData.coInsured).isEqualTo(0)
        assertThat(incompleteQuoteData.student).isTrue()
    }

    @Test
    @WithMockUser("COMPRICER", roles = ["COMPARISON"])
    fun sign_quote() {

        val now = Instant.now()
        val uwQuoteId = UUID.randomUUID().toString()
        val uwProductId = UUID.randomUUID().toString()
        val uwSignQuoteRequest1 = slot<String>()
        val uwSignQuoteRequest2 = slot<SignQuoteRequest>()
        val uwSignQuoteResponse =
            SignedQuoteResponseDto(
                id = uwProductId,
                memberId = "12345",
                signedAt = now,
                market = "DENMARK"
            )

        val agSetupPaymentLinkRequest1 = slot<String>()
        val agSetupPaymentLinkRequest2 = slot<CreateSetupPaymentLinkRequestDto>()

        every {
            underwriterClient.signQuote(
                capture(uwSignQuoteRequest1),
                capture(uwSignQuoteRequest2)
            )
        } returns ResponseEntity.ok(uwSignQuoteResponse)

        every {
            apiGatewayClient.setupPaymentLink(
                capture(agSetupPaymentLinkRequest1),
                capture(agSetupPaymentLinkRequest2),
                null
            )
        } returns ResponseEntity.ok(CreateSetupPaymentLinkResponseDto("payment-link"))

        every {
            externalMemberRepository.save<ExternalMember>(any())
        } returns ExternalMember(UUID.randomUUID(), "12345", Partner.COMPRICER)

        val requestData = """
            {
                "requestId": "apa",
                "startsAt": {
                    "date": "${LocalDate.now()}",
                    "timezone": "Europe/Stockholm"
                },
                "email": "apan@apansson.se",
                "firstName": "Apan",
                "lastName": "Apansson",
                "personalNumber": "0505059996"
            }
        """.trimIndent()

        val response = postJson<SignResponseDTO>("/v1/quotes/$uwQuoteId/sign", requestData)

        assertThat(response.statusCode.value()).isEqualTo(200)

        with(response.body!!) {
            assertThat(requestId).isEqualTo("apa")
            assertThat(productId).isEqualTo(uwProductId)
            assertThat(signedAt).isEqualTo(now.epochSecond)
            assertThat(completionUrl).isEqualTo("payment-link")
        }

        assertThat(uwSignQuoteRequest1.captured).isEqualTo(uwQuoteId)
        assertThat(uwSignQuoteRequest2.captured.name!!.firstName).isEqualTo("Apan")
        assertThat(uwSignQuoteRequest2.captured.name!!.lastName).isEqualTo("Apansson")
        assertThat(uwSignQuoteRequest2.captured.email).isEqualTo("apan@apansson.se")
        assertThat(uwSignQuoteRequest2.captured.ssn).isEqualTo("0505059996")
        assertThat(uwSignQuoteRequest2.captured.startDate).isEqualTo("${LocalDate.now()}")

        assertThat(agSetupPaymentLinkRequest1.captured).isEqualTo("test")
        assertThat(agSetupPaymentLinkRequest2.captured.countryCode.name).isEqualTo("DK")
        assertThat(agSetupPaymentLinkRequest2.captured.memberId).isEqualTo("12345")
    }

    private inline fun <reified T : Any> postJson(url: String, data: String): ResponseEntity<T> {

        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON

        return restTemplate.exchange(url, HttpMethod.POST, HttpEntity(data, headers), T::class.java)
    }
}
