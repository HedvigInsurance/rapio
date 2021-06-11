package com.hedvig.rapio.members

import com.hedvig.memberservice.helpers.IntegrationTest
import com.hedvig.productPricing.testHelpers.TestHttpClient
import com.hedvig.rapio.external.ExternalMember
import com.hedvig.rapio.externalservices.memberService.dto.CreateMemberResponse
import com.hedvig.rapio.externalservices.memberService.dto.IsMemberRequest
import io.mockk.every
import java.util.Base64
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

//@TestExecutionListeners(
//    listeners = [WithSecurityContextTestExecutionListener::class],
//    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
//)
//@ActiveProfiles(profiles = ["noauth"])
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

//@AutoConfigureTestDatabase
//@AutoConfigureMockMvc(secure = false)
@ExtendWith(SpringExtension::class)
@ActiveProfiles(profiles = ["noauth"])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc(secure = false)
class MembersControllerTest : IntegrationTest() {

    @Autowired
    private lateinit var client: TestHttpClient

    @AfterEach
    fun teardown() {
        reset {
            entity<ExternalMember>()
        }
    }

    @BeforeEach
    fun setup() {


//        SecurityContextHolder.setContext(
//            SecurityContextHolder.createEmptyContext()
//                .also {
//                    it.authentication = UsernamePasswordAuthenticationToken()
//                }
//        )
    }

    @Test
//    @WithMockUser("COMPRICER", roles = ["COMPARISON"])
    @WithMockUser("AVY", roles = ["DISTRIBUTION"])
    fun `Can create member with trial insurance`() {
        val auth = SecurityContextHolder.getContext().authentication

        val memberId = 1337L

        every { memberServiceClient.createMember(any()) } returns
            (ResponseEntity.ok(CreateMemberResponse(memberId = memberId)))

        every { memberServiceClient.startOnboardingWithSSN(memberId, any()) } returns ResponseEntity.ok(Unit)
        (ResponseEntity.ok(CreateMemberResponse(memberId = memberId)))

        every { memberServiceClient.finalizeOnboarding(memberId, any()) } returns ResponseEntity.ok(Unit)

        every { memberServiceClient.attachTemporaryInsurance(any()) } returns ResponseEntity.ok(Unit)

        val result = client.post(
            uri = "/v1/members/trial-insurance",
            body = """
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
             "ownership":"SE_BRF",
             "birthDate": "1900-01-01"
            }
        """.trimIndent(),
            headers = mapOf(
                "Content-Type" to "Application/json",
                "Accept-Language" to "en"
            )
        ).assert2xx()

        assertThat(result.body<Map<String, Any>>()["memberId"]).isNotNull
    }

    @Test
    @WithMockUser("AVY", roles = ["DISTRIBUTION"])
    fun `isMember returns true when memberService returns true`() {
        val SSN = "123"

        every { memberServiceClient.getIsMember(IsMemberRequest(null, SSN, null)) } returns ResponseEntity.ok(true)

        val request = MockMvcRequestBuilders.get("/v1/members/is-member")
            .with(user("AVY"))
            .content("{\"personalNumber\":\"$SSN\"}")
            .contentType(MediaType.APPLICATION_JSON)

        val result = mockMvc.perform(request)
        result.andExpect(status().is2xxSuccessful)
            .andExpect(jsonPath("$.isMember", Matchers.`is`(true)))
    }

    @Test
    @WithMockUser("AVY", roles = ["DISTRIBUTION"])
    fun `isMember returns false when memberService returns false`() {
        val SSN = "123"

        every { memberServiceClient.getIsMember(IsMemberRequest(null, SSN, null)) } returns ResponseEntity.ok(false)

        val request = MockMvcRequestBuilders.get("/v1/members/is-member")
            .with(user("AVY"))
            .content("{\"personalNumber\":\"$SSN\"}")
            .contentType(MediaType.APPLICATION_JSON)


//        val result = mockMvc.perform(request)
//        result.andExpect(status().is2xxSuccessful)
//            .andExpect(jsonPath("$.isMember", Matchers.`is`(false)))
    }
}
