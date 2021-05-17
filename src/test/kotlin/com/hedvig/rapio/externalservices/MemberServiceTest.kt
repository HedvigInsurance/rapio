package com.hedvig.rapio.externalservices

import com.hedvig.rapio.externalservices.memberService.MemberService
import com.hedvig.rapio.externalservices.memberService.MemberServiceClient
import com.hedvig.rapio.externalservices.memberService.dto.IsMemberRequest
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import java.io.IOException
import org.junit.jupiter.api.BeforeEach
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.ResponseEntity

@ExtendWith(MockKExtension::class)
class MemberServiceTest {

    private lateinit var service: MemberService

    @MockK
    lateinit var client: MemberServiceClient

    @BeforeEach
    fun setup() {
        service = MemberService(client)
    }

    @Test
    fun `successful true response returns expected true`() {
        val SSN = "123"
        every { client.getIsMember(IsMemberRequest(null, SSN, null)) } returns ResponseEntity.ok(true)
        val result = service.isMember(null, SSN, null)
        assertThat(result).isTrue()
    }

    @Test
    fun `successful false response returns expected false`() {
        val SSN = "123"
        every { client.getIsMember(IsMemberRequest(null, SSN, null)) } returns ResponseEntity.ok(false)
        val result = service.isMember(null, SSN, null)
        assertThat(result).isFalse()
    }

    @Test
    fun `failed call returns false`() {
        val SSN = "123"
        every { client.getIsMember(IsMemberRequest(null, SSN, null)) } returns ResponseEntity.status(500).build()
        val result = service.isMember(null, SSN, null)
        assertThat(result).isFalse()
    }

    @Test
    fun `unreachable member service returns false`() {
        val SSN = "123"
        every { client.getIsMember(IsMemberRequest(null, SSN, null)) } throws IOException("MemberService is down!")
        val result = service.isMember(null, SSN, null)
        assertThat(result).isFalse()
    }
}