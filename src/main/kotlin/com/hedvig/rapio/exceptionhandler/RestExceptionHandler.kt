package com.hedvig.rapio.exceptionhandler

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.fasterxml.jackson.module.kotlin.readValue
import com.hedvig.rapio.comparison.web.dto.ExternalErrorResponseDTO
import feign.FeignException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.reflect.InvocationTargetException

@ControllerAdvice
class RestExceptionHandler(
    private val mapper: ObjectMapper
) : ResponseEntityExceptionHandler() {

    override fun handleHttpMessageNotReadable(
        e: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {

        val message = when (val cause = e.cause) {
            is MissingKotlinParameterException -> {
                // handle RequestBody and Valid non nullable request parameter, with out @NotNull
                "'${cause.parameter.name}' must not be null"
            }
            is JsonParseException -> {
                // handle illegal json formatted request body.
                "JSON parse error: ${cause.message}"
            }
            else ->
                e.message
        }

        return super.handleExceptionInternal(e, message, headers, status, request)
    }

    override fun handleMethodArgumentNotValid(
        e: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {

        val message = e.bindingResult.allErrors.groupBy {
            it as FieldError
            it.field
        }.map {
            it.key to it.value.mapNotNull { it.defaultMessage }
        }.toMap().toString()

        return super.handleExceptionInternal(e, message, headers, status, request)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: Exception): ResponseEntity<ExternalErrorResponseDTO> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExternalErrorResponseDTO(e.message ?: ""))
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handle(e: AccessDeniedException): ResponseEntity<ExternalErrorResponseDTO> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ExternalErrorResponseDTO(e.message ?: ""))
    }

    override fun handleMissingServletRequestParameter(
        e: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExternalErrorResponseDTO(e.message))
    }

    @ExceptionHandler
    fun handle(e: Exception): ResponseEntity<ExternalErrorResponseDTO> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ExternalErrorResponseDTO(e.message ?: ""))
    }

    @ExceptionHandler(FeignException::class)
    fun handle(e: FeignException): ResponseEntity<Any> {
        val content = try {
            val jsonContent = mapper.readValue<MutableMap<String, Any>>(e.contentUTF8())
            jsonContent.apply {
                remove("path")
            }
        } catch (parseException: Exception) {
            e.contentUTF8()
        }
        return when {
            e.status() in 400..499 -> {
                ResponseEntity(content, HttpStatus.valueOf(e.status()))
            }
            else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @ExceptionHandler
    fun handle(e: HttpServerErrorException): ResponseEntity<ExternalErrorResponseDTO> {
        return ResponseEntity.status(e.statusCode).body(ExternalErrorResponseDTO(e.responseBodyAsString))
    }

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handle(e: MissingRequestHeaderException): ResponseEntity<ExternalErrorResponseDTO> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExternalErrorResponseDTO(e.message))
    }
}
