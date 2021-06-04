package com.hedvig.rapio.members

import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.external.ExternalMember
import com.hedvig.rapio.external.ExternalMemberService
import com.hedvig.rapio.externalservices.memberService.CreateMemberResponse
import com.hedvig.rapio.externalservices.memberService.MemberServiceClient
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import java.util.UUID
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest(controllers = [MembersController::class], secure = false)
class MembersControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var memberServiceClient: MemberServiceClient

    @MockkBean
    lateinit var externalMemberservice: ExternalMemberService

    @Test
    @WithMockUser("AVY")
    fun create_member() {
        val memberId = 1337L
        val externalMemberId = UUID.randomUUID()

        every { memberServiceClient.createMember(any()) } returns
            (ResponseEntity.ok(CreateMemberResponse(memberId = memberId)))

        every { memberServiceClient.startOnboardingWithSSN(memberId,any()) } returns ResponseEntity.ok(Unit)
            (ResponseEntity.ok(CreateMemberResponse(memberId = memberId)))

        every { memberServiceClient.finalizeOnboarding(memberId, any()) } returns ResponseEntity.ok(Unit)

        every { externalMemberservice.getExternalMemberByMemberId(memberId.toString()) } returns ExternalMember(
            externalMemberId,
            memberId.toString(),
            Partner.AVY
        )

        val request = post("/v1/members/trial-insurance")
            .with(user("AVY"))
            .content(
                """
                {"personalNumber": "191212121212",
                 "firstName": "Testy",
                 "lastName": "Tester",
                 "email": "test@example.com",
                 "phoneNumber": "08-8888",
                 "address": {
                    "street": "testgatan",
                    "zipCode": "12345",
                    "city": "Stockholm",
                    "apartmentNo": "1",
                    "floor": 2
                 },
                 "fromDate":"2021-04-04",
                 "ownership":"BRF",
                 "birthDate": "1900-01-01"
                }
            """.trimIndent()
            )
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Accept-Language", "en")

        val result = mockMvc.perform(request)

        result
            .andExpect(status().is2xxSuccessful)
            .andExpect(jsonPath("$.memberId", Matchers.any(String::class.java)))
    }
}