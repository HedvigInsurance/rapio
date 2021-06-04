package com.hedvig.rapio.members

import com.hedvig.rapio.externalservices.memberService.CreateMemberResponse
import com.hedvig.rapio.externalservices.memberService.MemberServiceClient
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
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

    @Test
    @WithMockUser("AVY")
    fun create_member() {
        val memberId = 1337L

        every { memberServiceClient.createMember(any()) } returns
            (ResponseEntity.ok(CreateMemberResponse(memberId = memberId)))

        val request = post("/v1/members")
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
                 "birthDate": "1900-01-01"
                }
            """.trimIndent()
            )

        val result = mockMvc.perform(request)

        result
            .andExpect(status().is2xxSuccessful)
            .andExpect(jsonPath("$.memberId", Matchers.any(String::class.java)))
    }
}