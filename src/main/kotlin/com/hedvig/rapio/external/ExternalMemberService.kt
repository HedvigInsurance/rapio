package com.hedvig.rapio.external

import com.hedvig.rapio.apikeys.Partner
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ExternalMemberService(
    private val repository: ExternalMemberRepository
) {
    fun getMemberIdByExternalMemberId(externalMemberId: UUID): String? {
        val memberId = repository.findByIdOrNull(externalMemberId)?.memberId
        if (memberId == null) {
            logger.info { "Unable to find memberId via externalMemberId (externalMemberId=$externalMemberId)" }
        }
        return memberId
    }

    fun createExternalMember(memberId: String, partner: Partner): ExternalMember {
        return repository.save(
            ExternalMember(
                id = UUID.randomUUID(),
                memberId = memberId,
                partner = partner
            )
        )
    }

    fun getExternalMemberByMemberId(memberId: String): ExternalMember? {
        return repository.findByMemberId(memberId)
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
