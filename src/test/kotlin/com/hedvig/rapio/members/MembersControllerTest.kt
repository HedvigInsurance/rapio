package com.hedvig.rapio.members

import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.apikeys.Role
import com.hedvig.rapio.external.ExternalMember
import com.hedvig.rapio.externalservices.memberService.dto.CreateMemberResponse
import com.hedvig.rapio.externalservices.memberService.dto.CreateTrialResponse
import com.hedvig.rapio.externalservices.memberService.dto.CreateUserResponse
import com.hedvig.rapio.externalservices.memberService.dto.IsMemberRequest
import com.hedvig.rapio.helpers.IntegrationTest
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDate
import java.util.UUID

@ContextConfiguration(classes = [MemberControllerTestUserConfiguration::class])
class MembersControllerTest : IntegrationTest() {

    @AfterEach
    fun teardown() {
        reset {
            entity<ExternalMember>()
        }
    }

    @Test
    fun `Can create trial insurance`() {
        val memberId = 1337L

        every { memberServiceClient.getIsMember(IsMemberRequest(ssn = "191212121212")) } returns ResponseEntity.ok(false)
        every { memberServiceClient.createMember(any()) } returns ResponseEntity.ok(
            CreateMemberResponse(memberId = memberId)
        )

        every { memberServiceClient.updateMember(memberId, any()) } returns ResponseEntity.ok(Unit)

        every { memberServiceClient.createUser(any()) } returns ResponseEntity.ok(
            CreateUserResponse((UUID.randomUUID()))
        )

        every { productPricingClient.createTrial(any()) } returns ResponseEntity.ok(CreateTrialResponse(UUID.randomUUID()))

        val result = client.post(
            uri = "/v1/members/trial",
            body = mapOf(
                "personalNumber" to "191212121212",
                "firstName" to "Testy",
                "lastName" to "Tester",
                "email" to "test@example.com",
                "countryCode" to "SE",
                "phoneNumber" to "08-8888",
                "address" to mapOf(
                    "street" to "testgatan",
                    "zipCode" to "12345",
                    "city" to "Stockholm",
                    "livingSpace" to 40,
                    "apartmentNo" to "1",
                    "floor" to 2
                ),
                "fromDate" to LocalDate.now(),
                "type" to "SE_APARTMENT_BRF",
                "birthDate" to "1900-01-01"
            ),
            headers = mapOf(
                "Accept-Language" to "en"
            )
        ).assert2xx()

        assertThat(result.body<Map<String, Any>>()["memberId"]).isNotNull
    }

    @Test
    fun `Cannot create trial insurance if member service says the SSN is taken`() {
        every { memberServiceClient.getIsMember(IsMemberRequest(ssn = "191212121212")) } returns ResponseEntity.ok(true)

        client.post(
            uri = "/v1/members/trial",
            body = mapOf(
                "personalNumber" to "191212121212",
                "firstName" to "Testy",
                "lastName" to "Tester",
                "email" to "test@example.com",
                "countryCode" to "SE",
                "phoneNumber" to "08-8888",
                "address" to mapOf(
                    "street" to "testgatan",
                    "zipCode" to "12345",
                    "city" to "Stockholm",
                    "livingSpace" to 40,
                    "apartmentNo" to "1",
                    "floor" to 2
                ),
                "fromDate" to LocalDate.now(),
                "type" to "SE_APARTMENT_BRF",
                "birthDate" to "1900-01-01"
            ),
            headers = mapOf(
                "Accept-Language" to "en"
            )
        ).assertStatus(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `Cannot create trial insurance if from date is in the past`() {
        every { memberServiceClient.getIsMember(IsMemberRequest(ssn = "191212121212")) } returns ResponseEntity.ok(false)

        client.post(
            uri = "/v1/members/trial",
            body = mapOf(
                "personalNumber" to "191212121212",
                "firstName" to "Testy",
                "lastName" to "Tester",
                "email" to "test@example.com",
                "countryCode" to "SE",
                "phoneNumber" to "08-8888",
                "address" to mapOf(
                    "street" to "testgatan",
                    "zipCode" to "12345",
                    "city" to "Stockholm",
                    "livingSpace" to 40,
                    "apartmentNo" to "1",
                    "floor" to 2
                ),
                "fromDate" to LocalDate.now().minusYears(1),
                "type" to "SE_APARTMENT_BRF",
                "birthDate" to "1900-01-01"
            ),
            headers = mapOf(
                "Accept-Language" to "en"
            )
        ).assertStatus(HttpStatus.BAD_REQUEST)
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
class MemberControllerTestUserConfiguration {

    @Bean("insecureUserName")
    fun userName(): Partner = Partner.AVY

    @Bean("insecureUserRole")
    fun userRole(): Role = Role.DISTRIBUTION
}
