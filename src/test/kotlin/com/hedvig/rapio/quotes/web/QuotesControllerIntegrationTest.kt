package com.hedvig.rapio.quotes.web

import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.external.ExternalMember
import com.hedvig.rapio.external.ExternalMemberRepository
import com.hedvig.rapio.externalservices.underwriter.transport.SignQuoteRequest
import com.hedvig.rapio.externalservices.underwriter.transport.SignedQuoteResponseDto
import com.hedvig.rapio.helpers.IntegrationTest
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.Instant
import java.util.UUID

class QuotesControllerIntegrationTest : IntegrationTest() {

    @Autowired
    private lateinit var externalMemberRepository: ExternalMemberRepository

    @AfterEach
    fun tearDown() {
        reset {
            entity<ExternalMember>()
        }
    }

    @Test
    fun `can sign quote`() {
        val quoteId = UUID.randomUUID()
        mockSigning(quoteId)

        val body = signQuoteBody()
        client.post("/v1/quotes/$quoteId/sign", body).assert2xx()
    }

    @Test
    fun `signing quote without external member creates one upon success`() {
        val quoteId = UUID.randomUUID()
        mockSigning(quoteId, memberId = "mid1")

        val body = signQuoteBody(externalMemberId = null)
        client.post("/v1/quotes/$quoteId/sign", body)

        assertThat(
            externalMemberRepository.findByMemberId("mid1")
        ).isNotNull
    }

    @Test
    fun `signing quote with external member uses it`() {
        val externalMemberId = UUID.randomUUID()
        externalMemberRepository.save(
            ExternalMember(externalMemberId, "mid2", Partner.AVY)
        )
        val quoteId = UUID.randomUUID()
        val slot = mockSigning(quoteId)

        val body = signQuoteBody(externalMemberId = externalMemberId)
        client.post("/v1/quotes/$quoteId/sign", body)

        assertThat(
            externalMemberRepository.findByMemberId("mid2")?.id
        ).isEqualTo(externalMemberId)
        assertThat(slot.captured.memberId).isEqualTo("mid2")
    }

    @Test
    fun `signing quote with non-existent external member is an error`() {
        val quoteId = UUID.randomUUID()
        val body = signQuoteBody(externalMemberId = UUID.randomUUID())
        client.post("/v1/quotes/$quoteId/sign", body).assertStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun mockSigning(quoteId: UUID, memberId: String = "mid"): CapturingSlot<SignQuoteRequest> {
        val slot = slot<SignQuoteRequest>()
        every {
            underwriterClient.signQuote(quoteId.toString(), capture(slot))
        } returns ResponseEntity.ok(
            SignedQuoteResponseDto(
                id = "contractId",
                memberId = memberId,
                signedAt = Instant.now(),
                market = "SE"
            )
        )
        return slot
    }

    private fun signQuoteBody(externalMemberId: UUID? = null) = mapOf(
        "requestId" to "rid",
        "email" to "example@example.com",
        "firstName" to "Testing",
        "lastName" to "Tester",
        "startsAt" to mapOf(
            "date" to "2021-04-01",
            "timezone" to "UTC"
        ),
        "externalMemberId" to externalMemberId
    )
}
