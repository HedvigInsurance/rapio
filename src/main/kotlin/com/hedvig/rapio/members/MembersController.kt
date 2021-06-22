package com.hedvig.rapio.members

import com.hedvig.libs.logging.calls.LogCall
import com.hedvig.rapio.apikeys.Partner
import com.hedvig.rapio.external.ExternalMemberService
import com.hedvig.rapio.externalservices.memberService.MemberService
import com.hedvig.rapio.externalservices.memberService.model.NewMemberInfo
import com.hedvig.rapio.insuranceinfo.dto.IsMemberRequest
import com.hedvig.rapio.insuranceinfo.dto.IsMemberResponse
import com.hedvig.rapio.members.dto.CreateTrialMemberRequest
import com.hedvig.rapio.members.dto.CreateTrialMemberResponse
import com.hedvig.rapio.util.getCurrentlyAuthenticatedPartner
import com.hedvig.rapio.util.unauthorized
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/members")
@ConditionalOnProperty("hedvig.new-avy-api.enabled", havingValue = "true")
class MembersController(
    val externalMemberService: ExternalMemberService,
    val memberService: MemberService
) {

    @PostMapping("/trial")
    @Secured("ROLE_DISTRIBUTION")
    @LogCall
    fun createMemberTrial(
        @RequestHeader(value = "Accept-Language", required = true) acceptLanguage: String,
        @RequestBody body: CreateTrialMemberRequest
    ): ResponseEntity<CreateTrialMemberResponse> {
        val partner = getCurrentlyAuthenticatedPartner()

        if (partner != Partner.AVY) {
            throw unauthorized("This endpoint is restricted.")
        }

        val memberId = memberService.createMemberWithTrialInsurance(
            language = acceptLanguage,
            countryCode = body.countryCode,
            partner = partner,
            fromDate = body.fromDate,
            newMemberInfo = body.toNewMemberInfo()
        )

        val externalMember = externalMemberService.createExternalMember(memberId.toString(), partner)
        return ResponseEntity.ok(CreateTrialMemberResponse(externalMember.id))
    }

    @GetMapping("/is-member")
    @Secured("ROLE_DISTRIBUTION")
    @LogCall
    fun getIsMember(
        @RequestBody request: IsMemberRequest?
    ): ResponseEntity<IsMemberResponse> {
        val isMember = memberService.isMember(null, request!!.personalNumber, null)
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
        type = type
    )

private fun CreateTrialMemberRequest.Address.toAddress(): NewMemberInfo.Address =
    NewMemberInfo.Address(
        street = street,
        city = city,
        zipCode = zipCode,
        apartmentNo = apartmentNo,
        livingSpace = livingSpace,
        floor = floor
    )
