package com.hedvig.rapio.qa

import com.hedvig.rapio.qa.dto.MemberServiceUnsignMemberRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "member-service",
    url = "\${hedvig.member-service.url:member-service}"
)
interface QualityAssuranceMemberServiceClient {

    @PostMapping("/_/staging/unsignMember")
    fun unsignMember(@RequestBody request: MemberServiceUnsignMemberRequest): ResponseEntity<Boolean>
}
