package com.hedvig.rapio.quotes.web

import arrow.core.Right
import com.hedvig.rapio.quotes.QuoteService
import com.hedvig.rapio.quotes.QuotesController
import com.hedvig.rapio.quotes.web.dto.QuoteResponseDTO
import com.hedvig.rapio.quotes.web.dto.SignResponseDTO
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.hamcrest.Matchers
import org.javamoney.moneta.Money
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant
import java.util.*

@WebMvcTest(controllers = [QuotesController::class], secure = false)
internal class QuotesControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var quoteService: QuoteService

    val createApartmentRequestJson = """
        {"requestId":"adads",
         "productType": "HOME",
         "quoteData": { 
            "personalNumber": "191212121212",
            "street": "testgatan",
            "zipCode": "12345",
            "city": "Stockholm",
            "livingSpace": 42,
            "householdSize": 2,
            "productSubType": "RENT"
         }
        }
    """.trimIndent()

    val createHouseRequestJson = """
        {
            "requestId": "1231a",
            "productType": "HOUSE",
            "quoteData": {
                "street": "harry",
                "zipCode": "11216",
                "city": "stockholm",
                "livingSpace": "240",
                "personalNumber": "191212121212",
                "householdSize": "4",
                "ancilliaryArea": "123",
                "yearOfConstruction": "1976",
                "numberOfBathrooms": "2",
                "extraBuildings": [
                ],
                "isSubleted": "false",
                "floor": "2"
            }
        }
    """.trimIndent()

    @Test
    @WithMockUser("COMPRICER")
    fun create_apartment_quote(){

        val response = QuoteResponseDTO(
                requestId = "adads",
                monthlyPremium = Money.of(123,"SEK"),
                quoteId = UUID.randomUUID().toString(),
                validUntil = Instant.now().epochSecond
                )
        every { quoteService.createQuote(any(), any()) } returns(Right(response))

        val request = post("/v1/quotes")
                .with(user("compricer"))
                .content(createApartmentRequestJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)

        val result = mockMvc.perform(request)

        result
                .andExpect(status().is2xxSuccessful)
                .andExpect(jsonPath("$.quoteId", Matchers.any(String::class.java)))
    }

    @Test
    @WithMockUser("COMPRICER")
    fun create_house_quote(){

        val response = QuoteResponseDTO(
                requestId = "1231a",
                monthlyPremium = Money.of(123,"SEK"),
                quoteId = UUID.randomUUID().toString(),
                validUntil = Instant.now().epochSecond
        )
        every { quoteService.createQuote(any(), any()) } returns(Right(response))

        val request = post("/v1/quotes")
                .with(user("compricer"))
                .content(createHouseRequestJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)

        val result = mockMvc.perform(request)

        result
                .andExpect(status().is2xxSuccessful)
                .andExpect(jsonPath("$.quoteId", Matchers.any(String::class.java)))
    }

    val signRequestJson = """
        {
            "requestId": "jl",
            "startsAt": {
                "date": "2019-11-01",
                "timezone": "Europe/Stockholm"
            },
            "email": "some@test.com",
            "firstName": "test",
            "lastName": "Tolvansson"
        }
    """.trimIndent()

    @Test
    fun sign_quote(){

        val id = UUID.randomUUID()
        every { quoteService.signQuote(id, any()) } returns Right(SignResponseDTO("jl", id.toString(), Instant.now().epochSecond))

        val request = post("/v1/quotes/$id/sign")
                .with(user("compricer"))
                .content(signRequestJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)

        val result = mockMvc.perform(request)

        result
                .andExpect(status().is2xxSuccessful)
                .andExpect(jsonPath("$.quoteId", Matchers.equalTo(id.toString())))
    }
}