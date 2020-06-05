package com.hedvig.rapio

import com.hedvig.rapio.externalservices.apigateway.ApiGateway
import com.hedvig.rapio.externalservices.underwriter.transport.UnderwriterClient
import com.hedvig.rapio.quotes.QuoteService
import com.ninjasquad.springmockk.MockkBean
import org.junit.Ignore
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("auth", "testing")
@TestPropertySource(
  properties = [
    "hedvig.rapio.apikeys.mrInsplanetUser=INSPLANET",
    "hedvig.rapio.apikeys.mrInsplanetUser2=INSPLANET"
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

  @Ignore
  @Test
  fun apikey_with_basic_auth() {
    mvc
      .perform(get("/").with(httpBasic("mrInsplanetUser", "")))
      .andExpect(status().isNotFound)
  }

  @Ignore
  @Test
  fun apikey_not_found() {
    mvc
      .perform(get("/").with(httpBasic("tokenDoesNotExist", "")))
      .andExpect(status().isUnauthorized)
  }
}