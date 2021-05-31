package com.hedvig.rapio.qa

import com.hedvig.rapio.qa.dto.UnsignMemberRequest
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Profile("staging", "development")
@RequestMapping("v1/staging/")
class QualityAssuranceController(
    private val qualityAssuranceService: QualityAssuranceService
) {
    @PostMapping("unsign-member")
    fun unsignMember(
        @RequestBody request: UnsignMemberRequest
    ): ResponseEntity<Void> {
        val success = qualityAssuranceService.unsignMember(personalNumber = request.personalNumber)
        if (success) {
          return ResponseEntity.noContent().build()
        } else {
          return ResponseEntity.notFound().build()
        }
    }
}
