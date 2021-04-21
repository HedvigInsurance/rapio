package com.hedvig.rapio.insuranceinfo

import com.hedvig.libs.logging.calls.LogCall
import com.hedvig.rapio.external.ExternalMemberService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.validation.Valid

@RestController
@RequestMapping("v1/members")
class InsuranceInfoController(
    val insuranceInfoService: InsuranceInfoService,
    val externalMemberService: ExternalMemberService
) {
    @GetMapping("/{memberId}")
    @Secured("ROLE_INSURANCE_INFO")
    @LogCall
    fun getInsuranceInfo(
        @Valid @PathVariable memberId: String
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
        @Valid @PathVariable externalMemberId: UUID
    ): ResponseEntity<InsuranceInfo> {
        val memberId = externalMemberService.getMemberIdByExternalMemberId(externalMemberId)
            ?: return ResponseEntity.notFound().build()
        return when (val insuranceInfo = insuranceInfoService.getInsuranceInfo(memberId)) {
            null -> ResponseEntity.notFound().build()
            else -> ResponseEntity.ok(insuranceInfo)
        }
    }
}
