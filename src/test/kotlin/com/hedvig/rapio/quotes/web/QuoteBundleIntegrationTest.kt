package com.hedvig.rapio.quotes.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.hedvig.rapio.externalservices.apigateway.ApiGateway
import com.hedvig.rapio.externalservices.paymentService.PaymentService
import com.hedvig.rapio.externalservices.productPricing.ProductPricingService
import com.hedvig.rapio.externalservices.underwriter.transport.QuoteBundleRequestDto
import com.hedvig.rapio.externalservices.underwriter.transport.QuoteBundleResponseDto
import com.hedvig.rapio.externalservices.underwriter.transport.SignQuoteBundleRequest
import com.hedvig.rapio.externalservices.underwriter.transport.SignedQuoteBundleResponseDto
import com.hedvig.rapio.externalservices.underwriter.transport.UnderwriterClient
import com.hedvig.rapio.quotes.web.dto.BundleQuotesResponseDTO
import com.hedvig.rapio.quotes.web.dto.SignBundleResponseDTO
import com.ninjasquad.springmockk.MockkBean
import feign.FeignException
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import java.time.Instant
import java.time.LocalDate
import java.util.UUID


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(secure = false)
@ActiveProfiles(profiles = ["noauth"])
class QuoteBundleIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @MockkBean(relaxed = true)
    lateinit var apiGateway: ApiGateway

    @MockkBean(relaxed = true)
    lateinit var paymentService: PaymentService

    @MockkBean(relaxed = true)
    lateinit var productPricingService: ProductPricingService

    @MockkBean
    lateinit var underwriterClient: UnderwriterClient

    @Test
    @WithMockUser("COMPRICER", roles = ["COMPARISON"])
    fun bundle_quotes() {

        val uwQuoteBundleRequest = slot<QuoteBundleRequestDto>()
        val uwQuoteBundleResponse = objectMapper.readValue("""
            {
                "bundleCost": {
                    "monthlyGross": {
                        "amount": "25.00",
                        "currency": "NOK"
                    },
                    "monthlyNet": {
                        "amount": "20.00",
                        "currency": "NOK"
                    },
                    "monthlyDiscount": {
                        "amount": "5.00",
                        "currency": "NOK"
                    }
                }
            }
        """, QuoteBundleResponseDto::class.java)

        every { underwriterClient.quoteBundle(
            capture(uwQuoteBundleRequest)
        ) } returns ResponseEntity.status(200).body(uwQuoteBundleResponse)

        val requestData = """
            {
                "requestId": "apa",
                "quoteIds": [
                    "3786e6df-2b52-4b07-8a1b-2be0ca1b0df7", "3786e6df-2b52-4b07-8a1b-2be0ca1b0df8"
                ]
            }
        """

        val response = postJsonToResponseEntity<BundleQuotesResponseDTO>("/v1/quotes/bundle", requestData)

        assertThat(response.statusCode.value()).isEqualTo(200)

        with(response.body!!) {
            assertThat(requestId).isEqualTo("apa")
            assertThat(monthlyPremium.amount).isEqualTo("20.00")
            assertThat(monthlyPremium.currency).isEqualTo("NOK")
        }
    }

    @Test
    @WithMockUser("COMPRICER", roles = ["COMPARISON"])
    fun bundle_quote_with_underwriter_error() {

        every { underwriterClient.quoteBundle(any()) } throws FeignException.InternalServerError("testing", "apa".toByteArray())

        val requestData = """
            {
                "requestId": "apa",
                "quoteIds": [
                    "3786e6df-2b52-4b07-8a1b-2be0ca1b0df7", "3786e6df-2b52-4b07-8a1b-2be0ca1b0df8"
                ]
            }
        """
        val response = postJsonToResponseEntity<String>("/v1/quotes/bundle", requestData)

        assertThat(response.statusCode.value()).isEqualTo(500)
        assertThat(response.body).isEqualTo("{\"errorMessage\":\"Underwriter error\"}")
    }

    @Test
    @WithMockUser("COMPRICER", roles = ["COMPARISON"])
    fun sign_bundle() {

        val ids = listOf(UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val signedAt = Instant.now()
        val startsAt = LocalDate.now()

        val uwQuoteBundleRequest = slot<SignQuoteBundleRequest>()
        val uwQuoteBundleResponse = objectMapper.readValue("""
            {
                "memberId": "1234",
                "market": "NORWAY",
                "contracts": [{
                    "id": "${ids[0]}",
                    "signedAt": "$signedAt"
                },
                {
                    "id": "${ids[1]}",
                    "signedAt": "$signedAt"
                }]
            }
        """, SignedQuoteBundleResponseDto::class.java)

        every { underwriterClient.signQuoteBundle(
            capture(uwQuoteBundleRequest)
        ) } returns ResponseEntity.status(200).body(uwQuoteBundleResponse)

        val agSetupPaymentLinkRequest1 = slot<String>()
        val agSetupPaymentLinkRequest2 = slot<String>()

        every { apiGateway.setupPaymentLink(
            capture(agSetupPaymentLinkRequest1),
            capture(agSetupPaymentLinkRequest2)
        ) } returns "payment-link"

        val requestData = """
            {
                "requestId": "apa",
                "quoteIds": [
                    "3786e6df-2b52-4b07-8a1b-2be0ca1b0df7", "3786e6df-2b52-4b07-8a1b-2be0ca1b0df8"
                ],
                "monthlyPremium": {
                    "amount": "25.00",
                    "currency": "NOK"
                },
                "startsAt": {
                    "date": "$startsAt",
                    "timezone": "Europe/Stockholm"
                },
                "email": "apan@apansson.se",
                "firstName": "Apan",
                "lastName": "Apansson",
                "personalNumber": "121212012345"
            }
        """.trimIndent()

        val response = postJsonToResponseEntity<SignBundleResponseDTO>("/v1/quotes/bundle/sign", requestData)

        assertThat(response.statusCode.value()).isEqualTo(200)

        with(response.body!!) {
            assertThat(requestId).isEqualTo("apa")
            assertThat(productIds.size).isEqualTo(2)
            assertThat(productIds[0]).isEqualTo(ids[0])
            assertThat(productIds[1]).isEqualTo(ids[1])
            assertThat(signedAt).isEqualTo(signedAt)
            assertThat(completionUrl).isEqualTo("payment-link")
        }

        with(uwQuoteBundleRequest.captured) {
            assertThat(quoteIds.size).isEqualTo(2)
            assertThat(quoteIds[0].toString()).isEqualTo("3786e6df-2b52-4b07-8a1b-2be0ca1b0df7")
            assertThat(quoteIds[1].toString()).isEqualTo("3786e6df-2b52-4b07-8a1b-2be0ca1b0df8")
            assertThat(name!!.firstName).isEqualTo("Apan")
            assertThat(name!!.lastName).isEqualTo("Apansson")
            assertThat(ssn).isEqualTo("121212012345")
            assertThat(startDate).isEqualTo(startsAt)
            assertThat(email).isEqualTo("apan@apansson.se")
            assertThat(price).isEqualTo("25.00")
            assertThat(currency).isEqualTo("NOK")
        }

        assertThat(agSetupPaymentLinkRequest1.captured).isEqualTo("1234")
        assertThat(agSetupPaymentLinkRequest2.captured).isEqualTo("NORWAY")
    }

    @Test
    @WithMockUser("COMPRICER", roles = ["COMPARISON"])
    fun sign_bundle_with_uw_failure() {

        val startsAt = LocalDate.now()

        val uwQuoteBundleRequest = slot<SignQuoteBundleRequest>()
        val uwQuoteBundleResponse = """
            {
                "errorCode": "MEMBER_HAS_EXISTING_INSURANCE",
                "errorMessage": "Testing"
            }
        """

        every { underwriterClient.signQuoteBundle(
            capture(uwQuoteBundleRequest)
        ) } throws FeignException.UnprocessableEntity("apa", uwQuoteBundleResponse.toByteArray())

        val requestData = """
            {
                "requestId": "apa",
                "quoteIds": [
                    "3786e6df-2b52-4b07-8a1b-2be0ca1b0df7", "3786e6df-2b52-4b07-8a1b-2be0ca1b0df8"
                ],
                "monthlyPremium": {
                    "amount": "25.00",
                    "currency": "NOK"
                },
                "startsAt": {
                    "date": "$startsAt",
                    "timezone": "Europe/Stockholm"
                },
                "email": "apan@apansson.se",
                "firstName": "Apan",
                "lastName": "Apansson",
                "personalNumber": "121212012345"
            }
        """.trimIndent()

        val response = postJsonToResponseEntity<String>("/v1/quotes/bundle/sign", requestData)

        assertThat(response.statusCode.value()).isEqualTo(500)
        assertThat(response.body!!).isEqualTo("{\"errorMessage\":\"Cannot sign quote, already a Hedvig member\"}")

    }

    private inline fun <reified T : Any> postJsonToResponseEntity(url: String, data: String): ResponseEntity<T> {

        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON

        return restTemplate.exchange(url, HttpMethod.POST, HttpEntity(data, headers), T::class.java)
    }
}