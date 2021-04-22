package com.hedvig.rapio

import arrow.core.Right
import com.hedvig.rapio.externalservices.apigateway.ApiGateway
import com.hedvig.rapio.externalservices.paymentService.transport.PaymentServiceClient
import com.hedvig.rapio.externalservices.productPricing.transport.ProductPricingClient
import com.hedvig.rapio.externalservices.underwriter.transport.UnderwriterClient
import com.hedvig.rapio.insuranceinfo.InsuranceInfoService
import com.hedvig.rapio.quotes.QuoteService
import com.hedvig.rapio.quotes.util.QuoteData.createApartmentRequestJson
import com.hedvig.rapio.quotes.util.QuoteData.makeSignResponse
import com.hedvig.rapio.quotes.util.QuoteData.quoteResponse
import com.hedvig.rapio.quotes.util.QuoteData.signRequestJson
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("auth", "testing")
@TestPropertySource(
    properties = [
        "hedvig.rapio.apikeys.mrInsplanetUser=INSPLANET",
        "hedvig.rapio.apikeys.mrAvyUser=AVY",
        "hedvig.rapio.apikeys.mrAvyDistributorUser=AVY_DISTRIBUTOR"
    ]
)
class ApiKeysTest {
    @Autowired
    lateinit var mvc: MockMvc

    @MockkBean
    lateinit var quoteService: QuoteService

    @MockkBean
    lateinit var underwriterClient: UnderwriterClient

    @MockkBean
    lateinit var apiGateway: ApiGateway

    @MockkBean
    lateinit var paymentServiceClient: PaymentServiceClient

    @MockkBean
    lateinit var productPricingClient: ProductPricingClient

    @MockkBean
    lateinit var insuranceInfoService: InsuranceInfoService

    @Test
    fun apikey_with_basic_auth() {
        mvc
            .perform(
                get("/")
                    .with(httpBasic("mrInsplanetUser", ""))
            )
            .andExpect(status().isNotFound)
    }

    @Test
    fun apikey_not_found() {
        mvc
            .perform(
                get("/")
                    .with(httpBasic("tokenDoesNotExist", ""))
            )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `fail to access v1 quotes if the role is INSURANCE_INFO`() {
        every { quoteService.createQuote(any(), any()) } returns Right(quoteResponse)

        mvc
            .perform(
                post("/v1/quotes")
                    .with(httpBasic("mrAvyUser", ""))
                    .content(createApartmentRequestJson)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `succeed to access v1 quotes if the role is ROLE_COMPARISON`() {

        every { quoteService.createQuote(any(), any()) } returns Right(quoteResponse)

        mvc
            .perform(
                post("/v1/quotes")
                    .with(httpBasic("mrInsplanetUser", ""))
                    .content(createApartmentRequestJson)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `succeed to access v1 quotes if the role is ROLE_DISTRIBUTION`() {

        every { quoteService.createQuote(any(), any()) } returns Right(quoteResponse)

        mvc
            .perform(
                post("/v1/quotes")
                    .with(httpBasic("mrAvyDistributorUser", ""))
                    .content(createApartmentRequestJson)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `fail to access signing quotes if the role is ROLE_INSURANCE_INFO`() {
        every { quoteService.signQuote(any(), any()) } returns Right(makeSignResponse())

        mvc
            .perform(
                post("/v1/quotes/BA483B2A-2549-4C88-9311-F7394BB34D16/sign")
                    .with(httpBasic("mrAvyUser", ""))
                    .content(signRequestJson)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `succeed to access signing quotes if the role is ROLE_COMPARISON`() {
        every { quoteService.signQuote(any(), any()) } returns Right(makeSignResponse())

        mvc
            .perform(
                post("/v1/quotes/BA483B2A-2549-4C88-9311-F7394BB34D16/sign")
                    .with(httpBasic("mrInsplanetUser", ""))
                    .content(signRequestJson)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `succeed to access signing quotes if the role is ROLE_DISTRIBUTION`() {
        every { quoteService.signQuote(any(), any()) } returns Right(makeSignResponse())

        mvc
            .perform(
                post("/v1/quotes/BA483B2A-2549-4C88-9311-F7394BB34D16/sign")
                    .with(httpBasic("mrAvyDistributorUser", ""))
                    .content(signRequestJson)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `fail to access insurance info end-point with the role that is ROLE_COMPARISON`() {

        every { insuranceInfoService.getInsuranceInfo(any()) } returns null

        mvc
            .perform(
                get("/v1/members/12345")
                    .with(httpBasic("mrInsplanetUser", ""))
            )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `succeed to access insurance info end-point with the role ROLE_INSURANCE_INFO`() {

        every { insuranceInfoService.getInsuranceInfo(any()) } returns null

        mvc
            .perform(
                get("/v1/members/12345")
                    .with(httpBasic("mrAvyUser", ""))
            )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `succeed to access insurance info end-point with the role ROLE_DISTRIBUTION`() {

        every { insuranceInfoService.getInsuranceInfo(any()) } returns null

        mvc
            .perform(
                get("/v1/members/12345")
                    .with(httpBasic("mrAvyDistributorUser", ""))
            )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `succeed to access extended insurance info end-point with the role ROLE_INSURANCE_INFO`() {

        every { insuranceInfoService.getInsuranceInfo(any()) } returns null

        mvc
            .perform(
                get("/v1/members/996195e5-4330-4be9-87a9-a2ae8ce60311")
                    .with(httpBasic("mrAvyUser", ""))
            )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `succeed to access extended insurance info end-point with the role ROLE_DISTRIBUTION`() {

        every { insuranceInfoService.getInsuranceInfo(any()) } returns null

        mvc
            .perform(
                get("/v1/members/996195e5-4330-4be9-87a9-a2ae8ce60311")
                    .with(httpBasic("mrAvyDistributorUser", ""))
            )
            .andExpect(status().isNotFound)
    }
}
