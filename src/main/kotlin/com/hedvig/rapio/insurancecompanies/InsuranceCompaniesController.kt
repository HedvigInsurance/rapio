package com.hedvig.rapio.insurancecompanies

import com.hedvig.libs.logging.calls.LogCall
import com.hedvig.rapio.externalservices.underwriter.Underwriter
import com.hedvig.rapio.insurancecompanies.dto.InsuranceCompanyDto
import com.hedvig.rapio.insurancecompanies.dto.toDto
import com.neovisionaries.i18n.CountryCode
import feign.FeignException
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("v1/insurance-companies")
class InsuranceCompaniesController(
    val underwriter: Underwriter
) {

    @GetMapping
    @Secured("ROLE_DISTRIBUTION")
    @LogCall
    fun getInsuranceCompanies(@RequestParam countryCode: CountryCode): ResponseEntity<List<InsuranceCompanyDto>> {
        return try {
            ResponseEntity.ok(underwriter.getInsuranceCompanies(countryCode).map { it.toDto() })
        } catch (ex: FeignException) {
            logger.error(ex) { "Failed to get insurance companies from underwriter for countryCode: $countryCode" }
            ResponseEntity.status(502).build<List<InsuranceCompanyDto>>()
        }
    }
}