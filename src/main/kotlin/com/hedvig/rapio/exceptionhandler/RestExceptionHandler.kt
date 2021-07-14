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
import org.springframework.web.util.WebUtils

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

        return handleExceptionInternal(e, message, headers, status, request)
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

        return handleExceptionInternal(e, message, headers, status, request)
    }

    override fun handleMissingServletRequestParameter(
        e: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> =
        handleExceptionInternal(
            ex = e,
            body = ExternalErrorResponseDTO(e.message),
            headers = HttpHeaders.EMPTY,
            status = HttpStatus.BAD_REQUEST,
            request = request
        )

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        when {
            status.is5xxServerError -> logger.error(ex.localizedMessage, ex)
            status.is4xxClientError -> logger.info(ex.localizedMessage, ex)
            else -> logger.warn(ex.localizedMessage, ex)
        }

        return ResponseEntity(body, headers, status)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: Exception, request: WebRequest): ResponseEntity<Any> =
        handleExceptionInternal(
            ex = e,
            body = ExternalErrorResponseDTO(e.message ?: ""),
            headers = HttpHeaders.EMPTY,
            status = HttpStatus.BAD_REQUEST,
            request = request
        )

    @ExceptionHandler(AccessDeniedException::class)
    fun handle(e: AccessDeniedException, request: WebRequest): ResponseEntity<Any> =
        handleExceptionInternal(
            ex = e,
            body = ExternalErrorResponseDTO(e.message ?: ""),
            headers = HttpHeaders.EMPTY,
            status = HttpStatus.FORBIDDEN,
            request = request
        )

    @ExceptionHandler(FeignException::class)
    fun handle(e: FeignException, request: WebRequest): ResponseEntity<Any> {
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
                handleExceptionInternal(
                    ex = e,
                    body = content,
                    headers = HttpHeaders.EMPTY,
                    status = HttpStatus.valueOf(e.status()),
                    request = request
                )
            }
            else -> handleExceptionInternal(
                ex = e,
                body = null,
                headers = HttpHeaders.EMPTY,
                status = HttpStatus.INTERNAL_SERVER_ERROR,
                request = request
            )
        }
    }

    @ExceptionHandler
    fun handle(e: HttpServerErrorException, request: WebRequest): ResponseEntity<Any> =
        handleExceptionInternal(
            ex = e,
            body = ExternalErrorResponseDTO(e.responseBodyAsString),
            headers = HttpHeaders.EMPTY,
            status = e.statusCode,
            request = request
        )

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handle(e: MissingRequestHeaderException, request: WebRequest): ResponseEntity<Any> =
        handleExceptionInternal(
            ex = e,
            body = ExternalErrorResponseDTO(e.message),
            headers = HttpHeaders.EMPTY,
            status = HttpStatus.BAD_REQUEST,
            request = request
        )

    @ExceptionHandler
    fun handle(e: Exception, request: WebRequest): ResponseEntity<Any> =
        handleExceptionInternal(
            ex = e,
            body = ExternalErrorResponseDTO(e.message ?: ""),
            headers = HttpHeaders.EMPTY,
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            request = request
        )
}
