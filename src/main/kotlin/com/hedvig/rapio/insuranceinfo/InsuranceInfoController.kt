package com.hedvig.rapio.insuranceinfo

import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("v1/members")
class InsuranceInfoController(
    val insuranceInfoService: InsuranceInfoService
) {

    @GetMapping("/{memberId}")
    @Secured("ROLE_INSURANCE_INFO")
    fun getInsuranceInfo(
        @Valid @PathVariable memberId: String
    ): ResponseEntity<InsuranceInfo> {
        return when (val insuranceInfo = insuranceInfoService.getInsuranceInfo(memberId)) {
            null -> ResponseEntity.notFound().build()
            else -> ResponseEntity.ok(insuranceInfo)
        }
    }
}