package com.hedvig.rapio.external

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ExternalMemberRepository : JpaRepository<ExternalMember, UUID> {

    fun findByMemberId(memberId: String): ExternalMember?
}
