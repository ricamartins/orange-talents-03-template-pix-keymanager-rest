package com.zup.rest.keymanager.pixkey.delete

import com.zup.rest.keymanager.extension.GrpcMockServer
import com.zup.rest.keymanager.extension.with
import com.zup.rest.keymanager.pixkey.ErrorResponse
import com.zup.rest.keymanager.setup.PixKeyClient
import com.zup.rest.keymanager.setup.PixKeyServiceMock
import com.zup.rest.keymanager.setup.TestDataBuilder
import com.zup.rest.keymanager.setup.to
import io.grpc.Status
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

@MicronautTest @GrpcMockServer
class PixKeyDeleteFailureTest(
    private val client: PixKeyClient,
    private val factory: TestDataBuilder
) {

    lateinit var service: PixKeyServiceMock

    @Test
    fun `should return not found when pix id does not exists`() {

        val data = factory
            .withClientId(UUID.randomUUID().toString())
            .withPixId(UUID.randomUUID().toString())

        val (clientId, pixId) = data.buildDeleteRestRequest()

        service.deleteOnError(data.buildDeleteRequest()) {
            Status.NOT_FOUND with "Pix ID does not exists"
        }

        val response = client.delete(clientId, pixId)

        with(response) {
            val body = body().to<ErrorResponse>()
            assertEquals(HttpStatus.NOT_FOUND, status)
            assertEquals("Pix ID does not exists", body.message)
        }
    }

    @Test
    fun `should return unauthorized when pix key does not belong to client`() {

        val data = factory
            .withClientId(UUID.randomUUID().toString())
            .withPixId(UUID.randomUUID().toString())

        val (clientId, pixId) = data.buildDeleteRestRequest()

        service.deleteOnError(data.buildDeleteRequest()) {
            Status.PERMISSION_DENIED with "Pix key does not belong to this client"
        }

        val e = assertThrows<HttpClientResponseException> { client.delete(clientId, pixId) }

        with(e) {
            val body = (e.response.body() as String).to<ErrorResponse>()
            println(HttpStatus.UNAUTHORIZED.code)
            assertEquals(HttpStatus.UNAUTHORIZED, status)
            assertEquals("Pix key does not belong to this client", body.message)
        }

    }

    @Test
    fun `should return unauthorized when could not remove pix key from the central bank`() {

        val data = factory
            .withClientId(UUID.randomUUID().toString())
            .withPixId(UUID.randomUUID().toString())

        val (clientId, pixId) = data.buildDeleteRestRequest()

        service.deleteOnError(data.buildDeleteRequest()) {
            Status.PERMISSION_DENIED with "Client can not delete this pix key"
        }

        val e = assertThrows<HttpClientResponseException> { client.delete(clientId, pixId) }

        with(e) {
            val body = (e.response.body() as String).to<ErrorResponse>()
            assertEquals(HttpStatus.UNAUTHORIZED, status)
            assertEquals("Client can not delete this pix key", body.message)
        }
    }
}
