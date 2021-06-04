package com.hedvig.rapio.insuranceinfo

import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.external.ExternalMember
import com.hedvig.rapio.external.ExternalMemberService
import com.hedvig.rapio.externalservices.memberService.MemberService
import com.hedvig.rapio.externalservices.productPricing.InsuranceStatus
import com.hedvig.rapio.insuranceinfo.dto.InsuranceInfo
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import org.hamcrest.Matchers
import org.javamoney.moneta.Money
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [InsuranceInfoController::class], secure = false)
internal class InsuranceInfoControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var insuranceInfoService: InsuranceInfoService

    @MockkBean
    lateinit var externalMemberService: ExternalMemberService

    @MockkBean
    lateinit var memberService: MemberService

    @Test
    @WithMockUser("AVY")
    fun `retrieving member info returns not found if no insurance info`() {
        val MEMBER_ID = "123456"
        every { insuranceInfoService.getInsuranceInfo(MEMBER_ID) } returns null
        val result = mockMvc.perform(get("/v1/members/$MEMBER_ID"))
        result.andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser("AVY")
    fun `can use regular insurance endpoint with member id`() {
        val MEMBER_ID = "123456"
        every { insuranceInfoService.getInsuranceInfo(MEMBER_ID) } returns InsuranceInfo(
            memberId = MEMBER_ID,
            insuranceStatus = InsuranceStatus.ACTIVE,
            insurancePremium = Money.of(BigDecimal.TEN, "SEK"),
            inceptionDate = LocalDate.now(),
            paymentConnected = true
        )
        val result = mockMvc.perform(get("/v1/members/$MEMBER_ID"))
        result
            .andExpect(status().is2xxSuccessful)
            .andExpect(jsonPath("$.memberId", Matchers.`is`(MEMBER_ID)))
    }

    @Test
    @WithMockUser("AVY")
    fun `can use regular insurance endpoint with external member id`() {
        val EXTERNAL_MEMBER_ID = UUID.randomUUID()
        val MEMBER_ID = "123456"
        every { externalMemberService.getMemberIdByExternalMemberId(EXTERNAL_MEMBER_ID) } returns MEMBER_ID
        every { insuranceInfoService.getInsuranceInfo(MEMBER_ID) } returns InsuranceInfo(
            memberId = MEMBER_ID,
            insuranceStatus = InsuranceStatus.ACTIVE,
            insurancePremium = Money.of(BigDecimal.TEN, "SEK"),
            inceptionDate = LocalDate.now(),
            paymentConnected = true
        )
        val result = mockMvc.perform(get("/v1/members/$EXTERNAL_MEMBER_ID"))
        result
            .andExpect(status().is2xxSuccessful)
            .andExpect(jsonPath("$.memberId", Matchers.`is`(MEMBER_ID)))
    }

    @Test
    @WithMockUser("AVY")
    fun `retrieving member info returns not found if no member for external member id`() {
        val EXTERNAL_MEMBER_ID = UUID.randomUUID()
        every { externalMemberService.getMemberIdByExternalMemberId(EXTERNAL_MEMBER_ID) } returns null
        val result = mockMvc.perform(get("/v1/members/$EXTERNAL_MEMBER_ID"))
        result.andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser("AVY")
    fun `can successfully convert memberId to externalMemberId`() {
        val MEMBER_ID = "123456"
        val EXTERNAL_MEMBER_ID = UUID.randomUUID()
        every { externalMemberService.createExternalMember(MEMBER_ID, Partner.AVY) } returns ExternalMember(
            id = EXTERNAL_MEMBER_ID,
            memberId = "123456",
            partner = Partner.AVY
        )
        every { insuranceInfoService.getInsuranceInfo(memberId = MEMBER_ID) } returns InsuranceInfo(
            memberId = MEMBER_ID,
            insuranceStatus = InsuranceStatus.ACTIVE,
            insurancePremium = Money.of(BigDecimal.TEN, "SEK"),
            inceptionDate = LocalDate.now(),
            paymentConnected = true
        )
        every { externalMemberService.getExternalMemberByMemberId(MEMBER_ID) } returns null

        val request = post("/v1/members/$MEMBER_ID/to-external-member-id")
            .with(user("AVY"))

        val result = mockMvc.perform(request)

        result.andExpect(status().is2xxSuccessful)
            .andExpect(jsonPath("$.id", Matchers.`is`(EXTERNAL_MEMBER_ID.toString())))
    }

    @Test
    @WithMockUser("AVY")
    fun `member id that is not connected to a contract should not be converted`() {
        every { insuranceInfoService.getInsuranceInfo(any()) } returns null
        every { externalMemberService.getExternalMemberByMemberId(any()) } returns null

        val request = post("/v1/members/123456/to-external-member-id")
            .with(user("AVY"))

        val result = mockMvc.perform(request)

        result.andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser("AVY")
    fun `existing external member should be returned when attempting to create new external user`() {
        val MEMBER_ID = "123456"
        val EXTERNAL_MEMBER_ID = UUID.randomUUID()

        every { insuranceInfoService.getInsuranceInfo(memberId = MEMBER_ID) } returns InsuranceInfo(
            memberId = MEMBER_ID,
            insuranceStatus = InsuranceStatus.ACTIVE,
            insurancePremium = Money.of(BigDecimal.TEN, "SEK"),
            inceptionDate = LocalDate.now(),
            paymentConnected = true
        )
        every { externalMemberService.getExternalMemberByMemberId(MEMBER_ID) } returns ExternalMember(
            id = EXTERNAL_MEMBER_ID,
            memberId = "123456",
            partner = Partner.AVY
        )

        val request = post("/v1/members/$MEMBER_ID/to-external-member-id")
            .with(user("AVY"))

        val result = mockMvc.perform(request)

        result.andExpect(status().is2xxSuccessful)
            .andExpect(jsonPath("$.id", Matchers.`is`(EXTERNAL_MEMBER_ID.toString())))
    }

    @Test
    @WithMockUser("AVY")
    fun `isMember returns true when memberService returns true`() {
        val SSN = "123"

        every { memberService.isMember(null, SSN, null) } returns true

        val request = get("/v1/members/is-member")
            .with(user("AVY"))
            .content("{\"personalNumber\":\"$SSN\"}")
            .contentType(MediaType.APPLICATION_JSON)

        val result = mockMvc.perform(request)
        result.andExpect(status().is2xxSuccessful)
            .andExpect(jsonPath("$.isMember", Matchers.`is`(true)))
    }

    @Test
    @WithMockUser("AVY")
    fun `isMember returns false when memberService returns false`() {
        val SSN = "123"

        every { memberService.isMember(null, SSN, null) } returns false

        val request = get("/v1/members/is-member")
            .with(user("AVY"))
            .content("{\"personalNumber\":\"$SSN\"}")
            .contentType(MediaType.APPLICATION_JSON)


        val result = mockMvc.perform(request)
        result.andExpect(status().is2xxSuccessful)
            .andExpect(jsonPath("$.isMember", Matchers.`is`(false)))
    }
}
