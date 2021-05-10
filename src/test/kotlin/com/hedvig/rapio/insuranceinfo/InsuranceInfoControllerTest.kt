package com.hedvig.rapio.insuranceinfo

import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.external.ExternalMember
import com.hedvig.rapio.external.ExternalMemberService
import com.hedvig.rapio.externalservices.productPricing.InsuranceStatus
import com.hedvig.rapio.insuranceinfo.dto.InsuranceInfo
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.javamoney.moneta.Money
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@WebMvcTest(controllers = [InsuranceInfoController::class], secure = false)
internal class InsuranceInfoControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var insuranceInfoService: InsuranceInfoService

    @MockkBean
    lateinit var externalMemberService: ExternalMemberService

    @Test
    @WithMockUser("AVY")
    fun `a Partner with Role of Distribution can successfully access conversion endpoint`() {
        val EXTERNAL_MEMBER_ID = UUID.randomUUID()
        every { externalMemberService.createExternalMember("123456", Partner.AVY) } returns ExternalMember(
            id = EXTERNAL_MEMBER_ID,
            memberId = "123456",
            partner = Partner.AVY
        )
        every { insuranceInfoService.getInsuranceInfo(memberId = "123456") } returns InsuranceInfo(
            memberId = "123456",
            insuranceStatus = InsuranceStatus.ACTIVE,
            insurancePremium = Money.of(BigDecimal.TEN, "SEK"),
            inceptionDate = LocalDate.now(),
            paymentConnected = true
        )
        val request = post("/v1/members/123456/to-external-member-id")
            .with(user("AVY"))

        val result = mockMvc.perform(request)

        result.andExpect(status().is2xxSuccessful)

        val content = result.andReturn().response.contentAsString

        val trimmedContent = content.substring(1, content.length - 1)

        assertThat(trimmedContent).isEqualTo(EXTERNAL_MEMBER_ID.toString())
    }

    @Test
    @WithMockUser("AVY")
    fun `member id that is not connected to a contract should not be converted`() {
        every { insuranceInfoService.getInsuranceInfo(any()) } returns null

        val request = post("/v1/members/123456/to-external-member-id")
            .with(user("AVY"))

        val result = mockMvc.perform(request)

        result.andExpect(status().isNotFound)
    }
}
