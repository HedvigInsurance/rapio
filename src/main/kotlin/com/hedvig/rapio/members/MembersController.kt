package com.hedvig.rapio.members

import com.hedvig.libs.logging.calls.LogCall
import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.external.ExternalMemberService
import com.hedvig.rapio.externalservices.memberService.MemberService
import com.hedvig.rapio.externalservices.memberService.MemberServiceClient
import com.hedvig.rapio.externalservices.memberService.model.Address
import com.hedvig.rapio.externalservices.memberService.model.NewMemberInfo
import com.hedvig.rapio.insuranceinfo.dto.IsMemberRequest
import com.hedvig.rapio.insuranceinfo.dto.IsMemberResponse
import com.hedvig.rapio.members.dto.CreateTrialMemberRequest
import com.hedvig.rapio.members.dto.CreateTrialMemberResponse
import com.hedvig.rapio.util.Forbidden
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.hedvig.rapio.members.dto.Address as DtoAddress

@RestController
@RequestMapping("v1/members")
class MembersController(
    val externalMemberService: ExternalMemberService,
    val memberService: MemberService
) {

    @PostMapping("/trial-insurance")
//    @Secured("ROLE_DISTRIBUTION")
    @Secured("ROLE_COMPARISON")
    @LogCall
    fun createMember(
        @RequestHeader(value = "Accept-Language", required = true) acceptLanguage: String,
        @RequestBody body: CreateTrialMemberRequest
    ): ResponseEntity<CreateTrialMemberResponse> {
        val currentUserName = SecurityContextHolder.getContext().authentication.name
        val partner = Partner.valueOf(currentUserName)

//        if (partner != Partner.AVY) {
//            throw Forbidden
//        }

        val memberId = memberService.createMemberWithTrialInsurance(
            language = acceptLanguage,
            partner = partner,
            fromDate = body.fromDate,
            newMemberInfo = body.toNewMemberInfo()
        )

        val externalMember = externalMemberService.createExternalMember(memberId.toString(), partner)
        return ResponseEntity.ok(CreateTrialMemberResponse(externalMember.id))
    }

    @GetMapping("/is-member")
//    @Secured("ROLE_DISTRIBUTION")
    @Secured("ROLE_COMPARISON")
    @LogCall
    fun getIsMember(
        @RequestBody request: IsMemberRequest
    ): ResponseEntity<IsMemberResponse> {
        val isMember = memberService.isMember(null, request.personalNumber, null)
        return ResponseEntity.ok(IsMemberResponse(isMember))
    }
}

private fun CreateTrialMemberRequest.toNewMemberInfo(): NewMemberInfo =
    NewMemberInfo(
        personalNumber = personalNumber,
        lastName = lastName,
        firstName = firstName,
        email = email,
        phoneNumber = phoneNumber,
        address = address.toAddress(),
        birthDate = birthDate,
        ownership = ownership
    )

private fun DtoAddress.toAddress(): Address = Address(street, city, zipCode, apartmentNo, floor)
