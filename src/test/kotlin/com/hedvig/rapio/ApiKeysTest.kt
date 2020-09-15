package com.hedvig.rapio

import arrow.core.Right
import com.hedvig.rapio.externalservices.apigateway.ApiGateway
import com.hedvig.rapio.externalservices.underwriter.transport.UnderwriterClient
import com.hedvig.rapio.quotes.QuoteService
import com.hedvig.rapio.quotes.util.QuoteData
import com.hedvig.rapio.quotes.util.QuoteData.quoteResponse
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
    "hedvig.rapio.apikeys.mrInsplanetUser2=INSPLANET",
    "hedvig.rapio.apikeys.mrAvyUser=AVY"
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

  @Test
  fun apikey_with_basic_auth() {
    mvc
      .perform(get("/").with(httpBasic("mrInsplanetUser", "")))
      .andExpect(status().isNotFound)
  }

  @Test
  fun apikey_not_found() {
    mvc
      .perform(get("/").with(httpBasic("tokenDoesNotExist", "")))
      .andExpect(status().isUnauthorized)
  }

  @Test
  fun `fail to access v1 quotes if the role is not ROLE_COMPARISON` () {

    every { quoteService.createQuote(any(), any()) } returns Right(quoteResponse)

    mvc
      .perform(post("/v1/quotes").with(httpBasic("mrAvyUser", ""))
        .content(QuoteData.createApartmentRequestJson)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden)
  }
}