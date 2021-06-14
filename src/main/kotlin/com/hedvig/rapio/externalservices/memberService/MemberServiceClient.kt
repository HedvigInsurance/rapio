package com.hedvig.rapio.externalservices.memberService

import com.hedvig.rapio.externalservices.memberService.dto.CreateMemberRequest
import com.hedvig.rapio.externalservices.memberService.dto.CreateMemberResponse
import com.hedvig.rapio.externalservices.memberService.dto.CreateUserRequest
import com.hedvig.rapio.externalservices.memberService.dto.CreateUserResponse
import com.hedvig.rapio.externalservices.memberService.dto.IsMemberRequest
import com.hedvig.rapio.externalservices.memberService.dto.UpdateMemberRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
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

    @PatchMapping("/_/member/{memberId}")
    fun updateMember(@PathVariable memberId: Long, @RequestBody body: UpdateMemberRequest): ResponseEntity<Unit>

    @PostMapping("/_/user")
    fun createUser(@RequestBody user: CreateUserRequest): ResponseEntity<CreateUserResponse>

}
