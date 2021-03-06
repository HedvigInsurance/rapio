package com.hedvig.rapio.quotes.web

import com.hedvig.rapio.externalservices.apigateway.transport.ApiGatewayClient
import com.hedvig.rapio.externalservices.apigateway.transport.CreateSetupPaymentLinkRequestDto
import com.hedvig.rapio.externalservices.apigateway.transport.CreateSetupPaymentLinkResponseDto
import com.hedvig.rapio.externalservices.memberService.MemberServiceClient
import com.hedvig.rapio.externalservices.paymentService.transport.PaymentServiceClient
import com.hedvig.rapio.externalservices.productPricing.transport.ProductPricingClient
import com.hedvig.rapio.externalservices.underwriter.transport.CompleteQuoteResponse
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteNorwegianHomeContentQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteNorwegianTravelQuoteDataDto
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
class NorwayIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @MockkBean(relaxed = true)
    lateinit var apiGatewayClient: ApiGatewayClient

    @MockkBean(relaxed = true)
    lateinit var paymentServiceClient: PaymentServiceClient

    @MockkBean(relaxed = true)
    lateinit var productPricingClient: ProductPricingClient

    @MockkBean(relaxed = true)
    lateinit var underwriterClient: UnderwriterClient

    @MockkBean(relaxed = true)
    lateinit var memberServiceClient: MemberServiceClient

    @Test
    @WithMockUser("COMPRICER", roles = ["COMPARISON"])
    fun create_travel_quote() {

        val uwQuoteId = UUID.randomUUID().toString()
        val uwQuoteRequest = slot<IncompleteQuoteDTO>()
        val uwQuoteResponse =
            CompleteQuoteResponse(
                id = uwQuoteId,
                price = BigDecimal(10.0),
                currency = "NOK",
                validTo = Instant.now().atZone(ZoneId.of("Europe/Stockholm")).plusMonths(1).toInstant()
            )

        every { underwriterClient.createQuote(capture(uwQuoteRequest)) } returns ResponseEntity.ok(uwQuoteResponse)

        val requestData = """
            {
              "requestId": "apa",
              "quoteData": {
                "birthDate": "1948-12-20",
                "coInsured": 0,
                "youth": false
              },
              "productType": "NORWEGIAN_TRAVEL"
            }
        """.trimIndent()

        val response = postJson<QuoteResponseDTO>("/v1/quotes", requestData)

        assertThat(response.statusCode.value()).isEqualTo(200)

        with(response.body!!) {
            assertThat(requestId).isEqualTo("apa")
            assertThat(quoteId).isEqualTo(uwQuoteId)
            assertThat(validUntil).isGreaterThan(Instant.now().epochSecond)
            assertThat(monthlyPremium.toString()).isEqualTo("NOK 10")
        }

        assertThat(uwQuoteRequest.captured.birthDate).isEqualTo("1948-12-20")
        assertThat(uwQuoteRequest.captured.currentInsurer).isNull()
        assertThat(uwQuoteRequest.captured.firstName).isNull()
        assertThat(uwQuoteRequest.captured.lastName).isNull()
        assertThat(uwQuoteRequest.captured.ssn).isNull()
        assertThat(uwQuoteRequest.captured.productType.name).isEqualTo("TRAVEL")
        assertThat(uwQuoteRequest.captured.shouldComplete).isTrue()

        val incompleteQuoteData = uwQuoteRequest.captured.incompleteQuoteData as IncompleteNorwegianTravelQuoteDataDto

        assertThat(incompleteQuoteData.coInsured).isEqualTo(0)
        assertThat(incompleteQuoteData.youth).isFalse()
    }

    @Test
    @WithMockUser("COMPRICER", roles = ["COMPARISON"])
    fun create_home_content_quote() {

        val uwQuoteId = UUID.randomUUID().toString()
        val uwQuoteRequest = slot<IncompleteQuoteDTO>()
        val uwQuoteResponse =
            CompleteQuoteResponse(
                id = uwQuoteId,
                price = BigDecimal(10.0),
                currency = "NOK",
                validTo = Instant.now().atZone(ZoneId.of("Europe/Stockholm")).plusMonths(1).toInstant()
            )

        every { underwriterClient.createQuote(capture(uwQuoteRequest)) } returns ResponseEntity.ok(uwQuoteResponse)

        val requestData = """
            {"requestId": "apa", 
             "productType": "NORWEGIAN_HOME_CONTENT", 
             "quoteData":{
                "street": "ApGatan", 
                "zipCode": "1234", 
                "city": "ApCity", 
                "birthDate": "1988-01-01", 
                "livingSpace": 122, 
                "coInsured":0, 
                "youth": false, 
                "productSubType": "OWN" 
                }
             }
        """.trimIndent()

        val response = postJson<QuoteResponseDTO>("/v1/quotes", requestData)

        assertThat(response.statusCode.value()).isEqualTo(200)

        with(response.body!!) {
            assertThat(requestId).isEqualTo("apa")
            assertThat(quoteId).isEqualTo(uwQuoteId)
            assertThat(validUntil).isGreaterThan(Instant.now().epochSecond)
            assertThat(monthlyPremium.toString()).isEqualTo("NOK 10")
        }

        assertThat(uwQuoteRequest.captured.birthDate).isEqualTo("1988-01-01")
        assertThat(uwQuoteRequest.captured.currentInsurer).isNull()
        assertThat(uwQuoteRequest.captured.firstName).isNull()
        assertThat(uwQuoteRequest.captured.lastName).isNull()
        assertThat(uwQuoteRequest.captured.ssn).isNull()
        assertThat(uwQuoteRequest.captured.productType.name).isEqualTo("HOME_CONTENT")
        assertThat(uwQuoteRequest.captured.shouldComplete).isTrue()

        val incompleteQuoteData =
            uwQuoteRequest.captured.incompleteQuoteData as IncompleteNorwegianHomeContentQuoteDataDto

        assertThat(incompleteQuoteData.street).isEqualTo("ApGatan")
        assertThat(incompleteQuoteData.zipCode).isEqualTo("1234")
        assertThat(incompleteQuoteData.city).isEqualTo("ApCity")
        assertThat(incompleteQuoteData.livingSpace).isEqualTo(122)
        assertThat(incompleteQuoteData.coInsured).isEqualTo(0)
        assertThat(incompleteQuoteData.youth).isFalse()
    }

    @Test
    @WithMockUser("COMPRICER", roles = ["COMPARISON"])
    fun sign_quote() {

        val uwSignedAt = Instant.now()
        val uwQuoteId = UUID.randomUUID().toString()
        val uwProductId = UUID.randomUUID().toString()
        val uwSignQuoteRequest1 = slot<String>()
        val uwSignQuoteRequest2 = slot<SignQuoteRequest>()
        val uwSignQuoteResponse =
            SignedQuoteResponseDto(
                id = uwProductId,
                memberId = "12345",
                signedAt = uwSignedAt,
                market = "NORWAY"
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
                "personalNumber": "121212012345"
            }
        """.trimIndent()

        val response = postJson<SignResponseDTO>("/v1/quotes/$uwQuoteId/sign", requestData)

        assertThat(response.statusCode.value()).isEqualTo(200)

        with(response.body!!) {
            assertThat(requestId).isEqualTo("apa")
            assertThat(productId).isEqualTo(uwProductId)
            assertThat(signedAt).isEqualTo(uwSignedAt.epochSecond)
            assertThat(completionUrl).isEqualTo("payment-link")
        }

        assertThat(uwSignQuoteRequest1.captured).isEqualTo(uwQuoteId)
        assertThat(uwSignQuoteRequest2.captured.name!!.firstName).isEqualTo("Apan")
        assertThat(uwSignQuoteRequest2.captured.name!!.lastName).isEqualTo("Apansson")
        assertThat(uwSignQuoteRequest2.captured.email).isEqualTo("apan@apansson.se")
        assertThat(uwSignQuoteRequest2.captured.ssn).isEqualTo("121212012345")
        assertThat(uwSignQuoteRequest2.captured.startDate).isEqualTo("${LocalDate.now()}")

        assertThat(agSetupPaymentLinkRequest1.captured).isEqualTo("test")
        assertThat(agSetupPaymentLinkRequest2.captured.countryCode.name).isEqualTo("NO")
        assertThat(agSetupPaymentLinkRequest2.captured.memberId).isEqualTo("12345")
    }

    private inline fun <reified T : Any> postJson(url: String, data: String): ResponseEntity<T> {

        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON

        return restTemplate.exchange(url, HttpMethod.POST, HttpEntity(data, headers), T::class.java)
    }
}
