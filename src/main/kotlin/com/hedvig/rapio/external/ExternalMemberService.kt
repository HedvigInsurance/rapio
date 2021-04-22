package com.hedvig.rapio.external

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ExternalMemberService(
    private val repository: ExternalMemberRepository
) {
    fun getMemberIdByExternalMemberId(externalMemberId: UUID): String? =
        repository.findByIdOrNull(externalMemberId)?.memberId
}
