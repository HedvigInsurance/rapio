package com.hedvig.rapio.external

import com.hedvig.rapio.apikeys.Partner
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.UUID

@RunWith(SpringRunner::class)
@DataJpaTest
internal class ExternalMemberServiceTest {
    @Autowired
    private lateinit var repository: ExternalMemberRepository

    private lateinit var externalMemberServiceToTest: ExternalMemberService

    private val EXTERNAL_MEMBER_ID = UUID.randomUUID()
    private val MEMBER_ID = "12345"

    @Before
    fun setup() {
        externalMemberServiceToTest = ExternalMemberService(repository)
    }

    @Test
    fun `get memberId by externalMemberId if exists`() {
        val externalMember = ExternalMember(EXTERNAL_MEMBER_ID, MEMBER_ID, Partner.AVY_DISTRIBUTOR)
        repository.saveAndFlush(externalMember)

        val memberId = externalMemberServiceToTest.getMemberIdByExternalMemberId(EXTERNAL_MEMBER_ID)
        assertThat(memberId).isEqualTo(MEMBER_ID)
    }

    @Test
    fun `gets null if member does not exist by externalMemberId`() {
        val memberId = externalMemberServiceToTest.getMemberIdByExternalMemberId(UUID.randomUUID())
        assertThat(memberId).isNull()
    }
}