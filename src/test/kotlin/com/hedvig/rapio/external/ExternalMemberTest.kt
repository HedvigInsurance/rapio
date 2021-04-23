package com.hedvig.rapio.external

import com.hedvig.rapio.apikeys.Partner
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.Instant
import java.util.UUID

@DataJpaTest
internal class ExternalMemberTest {
    @Autowired
    private lateinit var repository: ExternalMemberRepository

    private val EXTERNAL_MEMBER_ID = UUID.randomUUID()
    private val MEMBER_ID = "12345"

    @Test
    fun `can create and find an external member`() {
        val externalMember = ExternalMember(EXTERNAL_MEMBER_ID, MEMBER_ID, Partner.AVY)
        val before = Instant.now()
        repository.saveAndFlush(externalMember)
        val after = Instant.now()

        val result = repository.getOne(EXTERNAL_MEMBER_ID)
        assertThat(result).isNotNull()
        assertThat(result.id).isEqualTo(EXTERNAL_MEMBER_ID)
        assertThat(result.memberId).isEqualTo(MEMBER_ID)
        assertThat(result.partner).isEqualTo(Partner.AVY)
        assertThat(before).isBefore(result.createdAt)
        assertThat(after).isAfter(result.createdAt)
    }

    @Test
    fun `cannot create multiple external members with the same member id`() {
        val externalMember = ExternalMember(EXTERNAL_MEMBER_ID, MEMBER_ID, Partner.AVY)
        repository.saveAndFlush(externalMember)

        val externalMemberWithSameMemberId = ExternalMember(UUID.randomUUID(), MEMBER_ID, Partner.AVY)
        repository.save(externalMemberWithSameMemberId)
    }
}
