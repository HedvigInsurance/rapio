package com.hedvig.rapio.members

import com.hedvig.memberservice.helpers.IntegrationTest
import com.hedvig.productPricing.testHelpers.TestHttpClient
import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.apikeys.Role
import com.hedvig.rapio.external.ExternalMember
import com.hedvig.rapio.externalservices.memberService.dto.CreateMemberResponse
import com.hedvig.rapio.externalservices.memberService.dto.IsMemberRequest
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [MemberControllerTestUserConfiguration::class])
class MembersControllerTest : IntegrationTest() {

    @Autowired
    private lateinit var client: TestHttpClient

    @AfterEach
    fun teardown() {
        reset {
            entity<ExternalMember>()
        }
    }

    @Test
    fun `Can create member with trial insurance`() {
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
    fun `isMember returns true when memberService returns true`() {
        every { memberServiceClient.getIsMember(IsMemberRequest(null, "123", null)) } returns ResponseEntity.ok(true)

        val response = client.get(
            uri = "/v1/members/is-member",
            headers = mapOf("Content-Type" to "Application/json"),
            body = mapOf("personalNumber" to "123")
        )
        assertThat(response.body<Map<String, Any>>()["isMember"]).isEqualTo(true)
    }

    @Test
    fun `isMember returns false when memberService returns false`() {
        every { memberServiceClient.getIsMember(IsMemberRequest(null, "123", null)) } returns ResponseEntity.ok(false)

        val response = client.get(
            uri = "/v1/members/is-member",
            headers = mapOf("Content-Type" to "Application/json"),
            body = mapOf("personalNumber" to "123")
        ).assert2xx()
        assertThat(response.body<Map<String, Any>>()["isMember"]).isEqualTo(false)
    }
}

@TestConfiguration
private class MemberControllerTestUserConfiguration {

    @Bean("insecureUserName")
    fun userName(): Partner = Partner.AVY

    @Bean("insecureUserRole")
    fun userRole(): Role = Role.DISTRIBUTION
}