package com.hedvig.rapio.members

import com.hedvig.libs.logging.calls.LogCall
import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.external.ExternalMember
import com.hedvig.rapio.external.ExternalMemberService
import com.hedvig.rapio.externalservices.memberService.*
import com.hedvig.rapio.externalservices.memberService.dto.Address
import com.hedvig.rapio.externalservices.memberService.dto.StartOnboardingWithSSNRequest
import com.hedvig.rapio.externalservices.memberService.dto.UpdateContactInformationRequest
import com.hedvig.rapio.insuranceinfo.dto.IsMemberRequest
import com.hedvig.rapio.insuranceinfo.dto.IsMemberResponse
import com.hedvig.rapio.members.dto.CreateMemberRequest
import com.hedvig.rapio.members.dto.CreateMemberResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("v1/members")
class MembersController(
    val memberServiceClient: MemberServiceClient,
    val externalMemberService: ExternalMemberService
) {

    @PostMapping("/trial-insurance")
    @Secured("ROLE_DISTRIBUTION")
    @LogCall
    fun createMember(
        @RequestHeader(value = "Accept-Language", required = true) acceptLanguage: String,
        @RequestBody body: CreateMemberRequest
    ): ResponseEntity<CreateMemberResponse> {
        val currentUserName = SecurityContextHolder.getContext().authentication.name
        val partner = Partner.valueOf(currentUserName)
        val response = memberServiceClient.createMember(
            CreateMemberRequest(
                acceptLanguage, partner.toString()
            )
        )
        if (response.statusCode.is2xxSuccessful && response.body != null) {
            val memberId = response.body!!.memberId
            memberServiceClient.startOnboardingWithSSN(memberId, StartOnboardingWithSSNRequest(body.personalNumber))
            memberServiceClient.finalizeOnboarding(
                memberId, UpdateContactInformationRequest(
                    memberId = memberId.toString(),
                    firstName = body.firstName,
                    lastName = body.lastName,
                    email = body.email,
                    phoneNumber = body.phoneNumber,
                    address = addressFromDTO(body.address),
                    birthDate = body.birthDate
                )
            )

            val externalMember = externalMemberService.createExternalMember(memberId.toString(), partner)
            return ResponseEntity.ok(CreateMemberResponse(externalMember.id))
        } else {
            return ResponseEntity.badRequest().build()
        }
    }

    private fun addressFromDTO(address: com.hedvig.rapio.members.dto.Address): Address {
        return Address(
            street = address.street,
            city = address.city,
            zipCode = address.zipCode,
            apartmentNo = address.apartmentNo,
            floor = address.floor
        )
    }

    @GetMapping("/is-member")
    @Secured("ROLE_DISTRIBUTION")
    @LogCall
    fun getIsMember(
        @RequestBody request: IsMemberRequest
    ): ResponseEntity<IsMemberResponse> {
        val isMember = memberService.isMember(null, request.personalNumber, null)
        return ResponseEntity.ok(IsMemberResponse(isMember))
    }
}
