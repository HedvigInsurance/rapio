package com.hedvig.rapio.insuranceinfo

import com.hedvig.libs.logging.calls.LogCall
import com.hedvig.rapio.external.ExternalMemberService
import com.hedvig.rapio.insuranceinfo.dto.DirectDebitLinkResponse
import com.hedvig.rapio.insuranceinfo.dto.ExtendedInsuranceInfo
import com.hedvig.rapio.insuranceinfo.dto.InsuranceInfo
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("v1/members")
class InsuranceInfoController(
    val insuranceInfoService: InsuranceInfoService,
    val externalMemberService: ExternalMemberService
) {
    @GetMapping("/{memberId}")
    @Secured("ROLE_DISTRIBUTION")
    @LogCall
    fun getInsuranceInfo(
        @PathVariable memberId: String
    ): ResponseEntity<InsuranceInfo> {
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
}
