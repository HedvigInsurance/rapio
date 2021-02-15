package com.hedvig.rapio.exceptionhandler

import com.hedvig.rapio.comparison.web.dto.ExternalErrorResponseDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.IllegalArgumentException


@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(IllegalArgumentException::class)
    protected fun handleException(error: IllegalArgumentException): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExternalErrorResponseDTO("" + error.message))
    }
}