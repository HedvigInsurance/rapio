package com.hedvig.rapio.quotes.web

import arrow.core.Either
import com.hedvig.rapio.externalservices.apigateway.ApiGateway
import com.hedvig.rapio.externalservices.paymentService.PaymentService
import com.hedvig.rapio.externalservices.productPricing.ProductPricingService
import com.hedvig.rapio.externalservices.underwriter.CompleteQuoteReference
import com.hedvig.rapio.externalservices.underwriter.ConcreteUnderwriter
import com.hedvig.rapio.externalservices.underwriter.transport.ErrorResponse
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteNorwegianHomeContentQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteNorwegianTravelQuoteDataDto
import com.hedvig.rapio.externalservices.underwriter.transport.IncompleteQuoteDTO
import com.hedvig.rapio.externalservices.underwriter.transport.SignedQuoteResponseDto
import com.ninjasquad.springmockk.MockkBean
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.equalTo
import org.javamoney.moneta.Money
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(secure = false)
@ActiveProfiles(profiles = ["noauth"])
class NorwayIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean(relaxed = true)
    lateinit var apiGateway: ApiGateway

    @MockkBean(relaxed = true)
    lateinit var paymentService: PaymentService

    @MockkBean(relaxed = true)
    lateinit var productPricingService: ProductPricingService

    @MockkBean(relaxed = true)
    lateinit var concreteUnderwriter: ConcreteUnderwriter


    @Test
    @WithMockUser("COMPRICER", roles = ["COMPARISON"])
    fun create_travel_quote() {

        val uwQuoteId = UUID.randomUUID().toString()
        val uwQuoteRequest = slot<IncompleteQuoteDTO>()
        val uwQuoteResponse: Either<ErrorResponse, CompleteQuoteReference> = Either.right(
            CompleteQuoteReference(
                id = uwQuoteId,
                price = Money.of(10, "NOK"),
                validTo = Instant.now().atZone(ZoneId.of("Europe/Stockholm")).plusMonths(1).toInstant()
            )
        )

        every { concreteUnderwriter.createQuote(capture(uwQuoteRequest)) } returns uwQuoteResponse

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

        val request = MockMvcRequestBuilders.post("/v1/quotes")
            .content(requestData)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)

        val response = mockMvc.perform(request)

        response
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andExpect(jsonPath("$.requestId", equalTo("apa")))
            .andExpect(jsonPath("$.quoteId", equalTo(uwQuoteId)))
            .andExpect(jsonPath("$.monthlyPremium.amount", equalTo("10.00")))
            .andExpect(jsonPath("$.monthlyPremium.currency", equalTo("NOK")))

        assertThat(uwQuoteRequest.captured.birthDate).isEqualTo("1948-12-20")
        assertThat(uwQuoteRequest.captured.currentInsurer).isNull()
        assertThat(uwQuoteRequest.captured.firstName).isNull()
        assertThat(uwQuoteRequest.captured.lastName).isNull()
        assertThat(uwQuoteRequest.captured.ssn).isNull()
        assertThat(uwQuoteRequest.captured.productType.name).isEqualTo("TRAVEL")
        assertThat(uwQuoteRequest.captured.quotingPartner).isEqualTo("HEDVIG")
        assertThat(uwQuoteRequest.captured.shouldComplete).isTrue()
        assertThat(uwQuoteRequest.captured.underwritingGuidelinesBypassedBy).isNull()

        val incompleteQuoteData = uwQuoteRequest.captured.incompleteQuoteData as IncompleteNorwegianTravelQuoteDataDto

        assertThat(incompleteQuoteData.coInsured).isEqualTo(0)
        assertThat(incompleteQuoteData.youth).isFalse()
    }

    @Test
    @WithMockUser("COMPRICER", roles = ["COMPARISON"])
    fun create_home_content_quote() {

        val uwQuoteId = UUID.randomUUID().toString()
        val uwQuoteRequest = slot<IncompleteQuoteDTO>()
        val uwQuoteResponse: Either<ErrorResponse, CompleteQuoteReference> = Either.right(
            CompleteQuoteReference(
                id = uwQuoteId,
                price = Money.of(10, "NOK"),
                validTo = Instant.now().atZone(ZoneId.of("Europe/Stockholm")).plusMonths(1).toInstant()
            )
        )

        every { concreteUnderwriter.createQuote(capture(uwQuoteRequest)) } returns uwQuoteResponse

        val quoteRequestData = """
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

        val request = MockMvcRequestBuilders.post("/v1/quotes")
            .content(quoteRequestData)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)

        val response = mockMvc.perform(request)

        response
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andExpect(jsonPath("$.requestId", equalTo("apa")))
            .andExpect(jsonPath("$.quoteId", equalTo(uwQuoteId)))
            .andExpect(jsonPath("$.monthlyPremium.amount", equalTo("10.00")))
            .andExpect(jsonPath("$.monthlyPremium.currency", equalTo("NOK")))

        assertThat(uwQuoteRequest.captured.birthDate).isEqualTo("1988-01-01")
        assertThat(uwQuoteRequest.captured.currentInsurer).isNull()
        assertThat(uwQuoteRequest.captured.firstName).isNull()
        assertThat(uwQuoteRequest.captured.lastName).isNull()
        assertThat(uwQuoteRequest.captured.ssn).isNull()
        assertThat(uwQuoteRequest.captured.productType.name).isEqualTo("HOME_CONTENT")
        assertThat(uwQuoteRequest.captured.quotingPartner).isEqualTo("HEDVIG")
        assertThat(uwQuoteRequest.captured.shouldComplete).isTrue()
        assertThat(uwQuoteRequest.captured.underwritingGuidelinesBypassedBy).isNull()

        val incompleteQuoteData = uwQuoteRequest.captured.incompleteQuoteData as IncompleteNorwegianHomeContentQuoteDataDto

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
        val uwSignRequestId = slot<String>()
        val uwSignRequestEmail = slot<String>()
        val uwSignRequestStartsAt = slot<LocalDate>()
        val uwSignRequestFirstName = slot<String>()
        val uwSignRequestLastName = slot<String>()
        val uwSignRequestSsn = slot<String>()
        val uwSignResponse: Either<ErrorResponse, SignedQuoteResponseDto> = Either.right(
            SignedQuoteResponseDto(
                id = uwProductId,
                memberId = "12345",
                signedAt = uwSignedAt
            )
        )

        every { concreteUnderwriter.signQuote(
            capture(uwSignRequestId),
            capture(uwSignRequestEmail),
            capture(uwSignRequestStartsAt),
            capture(uwSignRequestFirstName),
            capture(uwSignRequestLastName),
            capture(uwSignRequestSsn)
        ) } returns uwSignResponse

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

        val requestBuilder = MockMvcRequestBuilders.post("/v1/quotes/$uwQuoteId/sign")
            .content(requestData)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)

        val signResponse = mockMvc.perform(requestBuilder)

        signResponse
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andExpect(jsonPath("$.requestId", equalTo("apa")))
            .andExpect(jsonPath("$.quoteId", equalTo(uwProductId)))
            .andExpect(jsonPath("$.productId", equalTo(uwProductId)))
            .andExpect(jsonPath("$.signedAt", equalTo(uwSignedAt.epochSecond.toInt())))
            .andExpect(jsonPath("$.completionUrl", equalTo("")))

        assertThat(uwSignRequestId.captured).isEqualTo(uwQuoteId)
        assertThat(uwSignRequestFirstName.captured).isEqualTo("Apan")
        assertThat(uwSignRequestLastName.captured).isEqualTo("Apansson")
        assertThat(uwSignRequestEmail.captured).isEqualTo("apan@apansson.se")
        assertThat(uwSignRequestSsn.captured).isEqualTo("121212012345")
        assertThat(uwSignRequestStartsAt.captured).isEqualTo("${LocalDate.now()}")
    }

}