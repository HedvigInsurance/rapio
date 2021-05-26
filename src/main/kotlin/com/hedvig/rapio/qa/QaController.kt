package com.hedvig.rapio.qa

import com.hedvig.rapio.qa.dto.UnsignMemberRequest
import com.hedvig.rapio.qa.dto.UnsignMemberResponse
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Profile("staging", "development")
@RequestMapping("v1/staging/")
class QaController(
    private val qaService: QaService
) {
    @PostMapping("unsign-member")
    fun unsignMember(
        @RequestBody request: UnsignMemberRequest
    ): ResponseEntity<UnsignMemberResponse> = ResponseEntity.ok(
        UnsignMemberResponse(
            qaService.unsignMember(
                personalNumber = request.personalNumber
            )
        )
    )
}
