package com.hedvig.rapio.quotes.web

import arrow.core.Right
import com.hedvig.rapio.quotes.QuoteService
import com.hedvig.rapio.quotes.QuotesController
import com.hedvig.rapio.quotes.util.QuoteData.createApartmentRequestJson
import com.hedvig.rapio.quotes.util.QuoteData.createHouseRequestJson
import com.hedvig.rapio.quotes.util.QuoteData.quoteResponse
import com.hedvig.rapio.quotes.web.dto.SignResponseDTO
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.hamcrest.Matchers
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
import java.util.UUID

@WebMvcTest(controllers = [QuotesController::class], secure = false)
internal class QuotesControllerTest {

  @Autowired
  lateinit var mockMvc: MockMvc

  @MockkBean
  lateinit var quoteService: QuoteService

  @Test
  @WithMockUser("COMPRICER")
  fun create_apartment_quote() {

    every { quoteService.createQuote(any(), any()) } returns (Right(quoteResponse))

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
  fun create_house_quote() {

    every { quoteService.createQuote(any(), any()) } returns (Right(quoteResponse))

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
  fun sign_quote() {

    val id = UUID.randomUUID()
    every { quoteService.signQuote(id, any()) } returns Right(
      SignResponseDTO(
        requestId = "jl",
        quoteId = id.toString(),
        productId = id.toString(),
        signedAt = Instant.now().epochSecond,
        completionUrl = "PAYMENT_REDIRECTION"
      )
    )

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