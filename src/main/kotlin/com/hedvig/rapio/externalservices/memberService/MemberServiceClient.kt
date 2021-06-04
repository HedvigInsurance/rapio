package com.hedvig.rapio.externalservices.memberService

import com.hedvig.rapio.externalservices.memberService.dto.IsMemberRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "member-service",
    url = "\${hedvig.member-service.url:member-service}"
)
interface MemberServiceClient {

    @PostMapping("/_/person/has/signed")
    fun getIsMember(@RequestBody request: IsMemberRequest): ResponseEntity<Boolean>

    @PostMapping("/_/member")
    fun createMember(@RequestBody member: CreateMemberRequest): ResponseEntity<CreateMemberResponse>

    @PostMapping("/_/member/{memberId}/startOnboardingWithSSN")
    fun startOnboardingWithSSN(
        @PathVariable memberId: Long,
        @RequestBody body: StartOnboardingWithSSNRequest
    ): ResponseEntity<Unit>

    @PostMapping("/_/member/{memberId}/finalizeOnboarding")
    fun finalizeOnboarding(
        @PathVariable memberId: Long,
        @RequestBody body: UpdateContactInformationRequest
    ): ResponseEntity<Unit>

}
