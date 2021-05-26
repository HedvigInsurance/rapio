package com.hedvig.rapio.qa

import com.hedvig.rapio.externalservices.memberService.dto.IsMemberRequest
import com.hedvig.rapio.qa.dto.MemberServiceUnsignMemberRequest
import com.hedvig.rapio.qa.dto.UnsignMemberRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "member-service",
    url = "\${hedvig.member-service.url:member-service}"
)
interface QaMemberServiceClient {

    @PostMapping("/_/staging/unsignMember")
    fun unsignMember(@RequestBody request: MemberServiceUnsignMemberRequest): ResponseEntity<Boolean>
}

