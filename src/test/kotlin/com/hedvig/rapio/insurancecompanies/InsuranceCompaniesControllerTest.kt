package com.hedvig.rapio.insurancecompanies

import com.hedvig.rapio.externalservices.underwriter.Underwriter
import com.hedvig.rapio.externalservices.underwriter.transport.InsuranceCompanyDto
import com.neovisionaries.i18n.CountryCode
import com.ninjasquad.springmockk.MockkBean
import feign.FeignException
import feign.Request
import feign.Response
import io.mockk.every
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(controllers = [InsuranceCompaniesController::class], secure = false)
internal class InsuranceCompaniesControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var underwriter: Underwriter

    @Test
    @WithMockUser("AVY")
    fun `returns list of companies when ok upstream response`() {
        val countryCode = CountryCode.SE
        val insuranceCompanies = listOf(
            InsuranceCompanyDto("if", "If", false),
            InsuranceCompanyDto("trygg-hansa", "Trygg Hansa", true)
        )

        every { underwriter.getInsuranceCompanies(countryCode) } returns insuranceCompanies

        val result = mockMvc.perform(
            get("/v1/insurance-companies")
                .with(user("AVY"))
                .param("countryCode", "SE")
                .accept(MediaType.APPLICATION_JSON)
        )

        result.andExpect(status().is2xxSuccessful)
            .andExpect(jsonPath("$[0].id", Matchers.`is`("if")))
    }

    @Test
    @WithMockUser("AVY")
    fun `returns 502 when error from underwriter`() {
        val countryCode = CountryCode.SE

        every { underwriter.getInsuranceCompanies(countryCode) } throws FeignException.errorStatus(
            "bla",
            Response.builder().status(500).headers(emptyMap()).reason("Error")
                .request(Request.create(Request.HttpMethod.GET, "url", emptyMap(), null)).build()
        )

        val result = mockMvc.perform(
            get("/v1/insurance-companies")
                .with(user("AVY"))
                .param("countryCode", "SE")
        )
        result.andExpect(status().`is`(502))
    }

    @Test
    @WithMockUser("AVY")
    fun `invalid countrycode returns 400`() {
        val countryCode = CountryCode.SE

        every { underwriter.getInsuranceCompanies(countryCode) } throws FeignException.errorStatus(
            "bla",
            Response.builder().status(500).headers(emptyMap()).reason("Error")
                .request(Request.create(Request.HttpMethod.GET, "url", emptyMap(), null)).build()
        )

        val result = mockMvc.perform(
            get("/v1/insurance-companies")
                .with(user("AVY"))
                .param("countryCode", "Sweden")
        )
        result.andExpect(status().`is`(400))
    }
}