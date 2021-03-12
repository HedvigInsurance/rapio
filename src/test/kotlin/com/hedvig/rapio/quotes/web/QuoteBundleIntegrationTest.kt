package com.hedvig.rapio.quotes.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.hedvig.rapio.externalservices.apigateway.ApiGateway
import com.hedvig.rapio.externalservices.paymentService.PaymentService
import com.hedvig.rapio.externalservices.productPricing.ProductPricingService
import com.hedvig.rapio.externalservices.underwriter.transport.QuoteBundleRequestDto
import com.hedvig.rapio.externalservices.underwriter.transport.QuoteBundleResponseDto
import com.hedvig.rapio.externalservices.underwriter.transport.UnderwriterClient
import com.hedvig.rapio.quotes.web.dto.BundleQuotesResponseDTO
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

    private inline fun <reified T : Any> postJsonToResponseEntity(url: String, data: String): ResponseEntity<T> {

        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON

        return restTemplate.exchange(url, HttpMethod.POST, HttpEntity(data, headers), T::class.java)
    }
}