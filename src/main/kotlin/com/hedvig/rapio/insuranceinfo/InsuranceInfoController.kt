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
    @GetMapping("/{externalMemberId}")
    @Secured("ROLE_INSURANCE_INFO", "ROLE_DISTRIBUTION")
    @LogCall
    fun getInsuranceInfo(
        @Valid @PathVariable externalMemberId: String
    ): ResponseEntity<InsuranceInfo> {
        try {
            val memberId = externalMemberService.getMemberIdByExternalMemberId(UUID.fromString(externalMemberId))
                ?: return ResponseEntity.notFound().build()
            return when (val insuranceInfo = insuranceInfoService.getExtendedInsuranceInfo(memberId)) {
                null -> ResponseEntity.notFound().build()
                else -> ResponseEntity.ok(insuranceInfo)
            }
        } catch (exception: IllegalArgumentException) {
            // FIXME: Do not allow this onward after talking to Avy
            return when (val insuranceInfo = insuranceInfoService.getInsuranceInfo(externalMemberId)) {
                null -> ResponseEntity.notFound().build()
                else -> ResponseEntity.ok(insuranceInfo)
            }
        }
    }
}
