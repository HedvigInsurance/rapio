package com.hedvig.rapio.members

import arrow.core.Right
import com.hedvig.rapio.externalservices.memberService.CreateMemberResponse
import com.hedvig.rapio.externalservices.memberService.MemberServiceClient
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@WebMvcTest(controllers = [MembersController::class], secure = false)
class MembersControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var memberServiceClient: MemberServiceClient

    @Test
    @WithMockUser("AVY")
    fun create_member() {
        val memberId = 1337L

        every { memberServiceClient.createMember(any()) } returns
                (ResponseEntity.ok(CreateMemberResponse(memberId = memberId)))

        val request = MockMvcRequestBuilders.post("/v1/members")
            .with(SecurityMockMvcRequestPostProcessors.user("avy"))
            .content("""
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
                 "birthDate": "1900-01-01"
                }
            """.trimIndent())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)

        val result = mockMvc.perform(request)

        result
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andExpect(MockMvcResultMatchers.jsonPath("$.memberId", Matchers.any(String::class.java)))
    }
}