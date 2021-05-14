package com.hedvig.rapio.insuranceinfo

import com.hedvig.libs.logging.calls.LogCall
import com.hedvig.rapio.external.ExternalMemberService
import com.hedvig.rapio.externalservices.memberService.MemberService
import com.hedvig.rapio.insuranceinfo.dto.DirectDebitLinkResponse
import com.hedvig.rapio.insuranceinfo.dto.ExtendedInsuranceInfo
import com.hedvig.rapio.insuranceinfo.dto.ExternalMemberId
import com.hedvig.rapio.insuranceinfo.dto.InsuranceInfo
import com.hedvig.rapio.insuranceinfo.dto.IsMemberRequest
import com.hedvig.rapio.insuranceinfo.dto.IsMemberResponse
import com.hedvig.rapio.util.getCurrentlyAuthenticatedPartner
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import org.springframework.web.bind.annotation.RequestBody

@RestController
@RequestMapping("v1/members")
class InsuranceInfoController(
    val insuranceInfoService: InsuranceInfoService,
    val externalMemberService: ExternalMemberService,
    val memberService: MemberService
) {
    @GetMapping("/{externalMemberId}")
    @Secured("ROLE_DISTRIBUTION")
    @LogCall
    fun getInsuranceInfo(
        @PathVariable externalMemberId: String
    ): ResponseEntity<InsuranceInfo> {
        val memberId = try {
            externalMemberService.getMemberIdByExternalMemberId(UUID.fromString(externalMemberId))
                ?: return ResponseEntity.notFound().build()
        } catch (exception: IllegalArgumentException) {
            externalMemberId
        }
        return when (val insuranceInfo = insuranceInfoService.getInsuranceInfo(memberId)) {
            null -> ResponseEntity.notFound().build()
            else -> ResponseEntity.ok(insuranceInfo)
        }
    }

    @GetMapping("/{externalMemberId}/extended")
    @Secured("ROLE_DISTRIBUTION")
    @LogCall
    fun getExtendedInsuranceInfo(
        @PathVariable externalMemberId: UUID
    ): ResponseEntity<ExtendedInsuranceInfo> {
        val memberId = externalMemberService.getMemberIdByExternalMemberId(externalMemberId)
            ?: return ResponseEntity.notFound().build()
        return when (val insuranceInfo = insuranceInfoService.getExtendedInsuranceInfo(memberId)) {
            null -> ResponseEntity.notFound().build()
            else -> ResponseEntity.ok(insuranceInfo)
        }
    }

    @PostMapping("/{memberId}/to-external-member-id")
    @Secured("ROLE_DISTRIBUTION")
    @LogCall
    fun createExternalMember(
        @PathVariable memberId: String
    ): ResponseEntity<ExternalMemberId> {
        val partner = getCurrentlyAuthenticatedPartner()
        val externalMemberMaybe = externalMemberService.getExternalMemberByMemberId(memberId)
        val isValidMember = insuranceInfoService.getInsuranceInfo(memberId) != null
        return when {
            externalMemberMaybe != null -> ResponseEntity.ok(ExternalMemberId(externalMemberMaybe.id))
            isValidMember -> {
                val externalMember = externalMemberService.createExternalMember(memberId, partner)
                ResponseEntity.ok(ExternalMemberId(externalMember.id))
            }
            else -> ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/{externalMemberId}/direct-debit/url")
    @Secured("ROLE_DISTRIBUTION")
    @LogCall
    fun getConnectDirectDebitLink(
        @PathVariable externalMemberId: UUID
    ): ResponseEntity<DirectDebitLinkResponse> {
        val memberId = externalMemberService.getMemberIdByExternalMemberId(externalMemberId)
            ?: return ResponseEntity.notFound().build()
        return when (val directDebitUrl = insuranceInfoService.getConnectDirectDebitUrl(memberId)) {
            null -> ResponseEntity.notFound().build()
            else -> ResponseEntity.ok(DirectDebitLinkResponse(directDebitUrl))
        }
    }

    @GetMapping("/is-member")
    @Secured("ROLE_DISTRIBUTION")
    @LogCall
    fun getIsMember(
        @RequestBody request: IsMemberRequest
    ): ResponseEntity<IsMemberResponse> {
        val isMember = memberService.isMember(null, request.ssn, null)
        return ResponseEntity.ok(IsMemberResponse(isMember))
    }
}
