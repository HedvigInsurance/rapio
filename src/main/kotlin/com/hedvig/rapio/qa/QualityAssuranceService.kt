package com.hedvig.rapio.qa

import com.hedvig.rapio.qa.dto.MemberServiceUnsignMemberRequest
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("staging", "development")
class QualityAssuranceService(
    val qaMemberServiceClient: QualityAssuranceMemberServiceClient
) {

    fun unsignMember(personalNumber: String): Boolean = qaMemberServiceClient.unsignMember(
        MemberServiceUnsignMemberRequest(personalNumber)
    ).body!!
}
