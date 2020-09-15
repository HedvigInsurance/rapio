package com.hedvig.rapio.insuranceinfo

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("v1/members")
class InsuranceInfoController(
    val insuranceInfoService: InsuranceInfoService
) {

    @GetMapping("/{memberId}")
    fun getInsuranceInfo(
        @Valid @RequestParam memberId: String
    ): ResponseEntity<InsuranceInfo> {
        val insuranceInfo = insuranceInfoService.getInsuranceInfo(memberId)

        return ResponseEntity.ok(insuranceInfo)
    }
}