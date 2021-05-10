package com.hedvig.rapio.insuranceinfo

import com.hedvig.rapio.external.ExternalMemberService
import com.ninjasquad.springmockk.MockkBean
import org.hamcrest.Matchers
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

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
    fun create_external_member() {
        val request = post("/v1/members/123456/to-external-member-id")
            .with(user("AVY"))

        val result = mockMvc.perform(request)

        result.andExpect(status().is2xxSuccessful)
    }

}