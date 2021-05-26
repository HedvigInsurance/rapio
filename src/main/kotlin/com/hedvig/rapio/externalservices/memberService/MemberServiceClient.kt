package com.hedvig.rapio.externalservices.memberService

import com.hedvig.rapio.externalservices.memberService.dto.IsMemberRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "member-service",
    url = "\${hedvig.member-service.url:member-service}"
)
interface MemberServiceClient {

    @PostMapping("/_/person/has/signed")
    fun getIsMember(@RequestBody request: IsMemberRequest) : ResponseEntity<Boolean>
}