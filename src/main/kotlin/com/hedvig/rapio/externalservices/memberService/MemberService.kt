package com.hedvig.rapio.externalservices.memberService

import com.hedvig.rapio.externalservices.memberService.dto.Address
import com.hedvig.rapio.externalservices.memberService.dto.IsMemberRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MemberService(
    val memberServiceClient: MemberServiceClient
) {

    fun isMember(memberId: String?, ssn: String?, email: String?): Boolean {
        try {
            val response = memberServiceClient.getIsMember(IsMemberRequest(memberId, ssn, email))
            if (response.statusCode.is2xxSuccessful) {
                return response.body!!
            }
        } catch (ex: Exception) {
            logger.error("Error from member service when checking member status for ${IsMemberRequest(memberId, ssn, email)}.", ex)
        }
        return false
    }

    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)!!
    }
}