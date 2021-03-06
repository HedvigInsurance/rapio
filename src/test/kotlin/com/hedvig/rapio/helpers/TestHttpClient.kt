package com.hedvig.rapio.helpers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase
import org.apache.http.client.methods.HttpUriRequest
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.stereotype.Component
import java.net.URI

@Component
class TestHttpClient(
    private val mapper: ObjectMapper,
    private val template: TestRestTemplate
) {

    init {
        template.restTemplate.requestFactory = GetRequestWithBodyAllowingRequestFactory()
    }

    fun withUser(user: String) = TestHttpClient(mapper, template.withBasicAuth(user, ""))

    fun get(uri: String, headers: Map<String, String> = emptyMap(), body: Any? = null): Response {
        return exchange(HttpMethod.GET, uri, body, headers)
    }

    fun post(uri: String, body: Any? = null, headers: Map<String, String> = emptyMap()): Response {
        return exchange(HttpMethod.POST, uri, body, headers)
    }

    fun put(uri: String, body: Any? = null, headers: Map<String, String> = emptyMap()): Response {
        return exchange(HttpMethod.PUT, uri, body, headers)
    }

    private fun exchange(method: HttpMethod, uri: String, body: Any?, headers: Map<String, String>): Response {
        val httpEntity = HttpEntity(
            body,
            HttpHeaders().apply {
                headers.forEach { (key, value) ->
                    set(key, value)
                }
            }
        )
        val entity = try {
            template.exchange(uri, method, httpEntity, Any::class.java)
        } catch (e: Exception) {
            template.exchange(uri, method, httpEntity, String::class.java)
        }
        return Response(mapper, entity)
    }

    class Response(
        private val mapper: ObjectMapper,
        private val entity: ResponseEntity<*>
    ) {

        fun assert2xx(): Response {
            assertThat(entity.statusCode.series())
                .describedAs("${entity.statusCode}, ${entity.body}")
                .isEqualTo(HttpStatus.Series.SUCCESSFUL)
            return this
        }

        fun assertStatus(status: HttpStatus): Response {
            assertThat(entity.statusCode).isEqualTo(status)
            return this
        }

        fun status() : HttpStatus {
            return entity.statusCode
        }

        inline fun <reified T> body(): T {
            return body(object : TypeReference<T>() {})
        }

        @PublishedApi
        internal fun <T> body(type: TypeReference<T>): T {
            return mapper.convertValue(entity.body, type)
        }
    }
}

private class GetRequestWithBodyAllowingRequestFactory : HttpComponentsClientHttpRequestFactory() {
    override fun createHttpUriRequest(httpMethod: HttpMethod, uri: URI): HttpUriRequest {
        if (httpMethod == HttpMethod.GET) {
            return HttpGetRequestWithEntity(uri)
        }
        return super.createHttpUriRequest(httpMethod, uri)
    }
}

private class HttpGetRequestWithEntity(uri: URI) : HttpEntityEnclosingRequestBase() {
    init {
        super.setURI(uri)
    }

    override fun getMethod(): String = HttpMethod.GET.name
}
