package com.hedvig.rapio

import com.hedvig.rapio.comparison.QuoteService
import com.ninjasquad.springmockk.MockkBean
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
@ActiveProfiles("auth")
@TestPropertySource(properties = [
    "hedvig.rapio.apikeys.mrInsplanetUser=insplanet}",
    "hedvig.rapio.apikeys.mrInsplanetUser2=insplanet}"
])
class ApiKeysTest {
    @Autowired
    lateinit var mvc: MockMvc

    @MockkBean
    lateinit var quoteService: QuoteService

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
}