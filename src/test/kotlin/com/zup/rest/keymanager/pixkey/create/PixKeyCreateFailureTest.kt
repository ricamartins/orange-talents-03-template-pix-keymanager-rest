package com.zup.rest.keymanager.pixkey.create

import com.zup.keymanager.proto.AccountType
import com.zup.keymanager.proto.KeyType
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
class PixKeyCreateFailureTest(
    private val client: PixKeyClient,
    private val factory: TestDataBuilder
) {

    lateinit var service: PixKeyServiceMock

    @Test
    fun `should return not found when client or account does not exists`() {

        val data = factory
            .withClientId(UUID.randomUUID().toString())
            .withKey(KeyType.DOCUMENT)
            .withAccountType(AccountType.SAVINGS)
            .withPixId(UUID.randomUUID().toString())

        val (clientId, request) = data.buildCreateRestRequest()

        service.createOnError(data.buildCreateRequest()) {
            Status.NOT_FOUND with "Client or account does not exists"
        }

        val response = client.create(clientId, request)

        with(response) {
            val body = response.body().to<ErrorResponse>()
            assertEquals(HttpStatus.NOT_FOUND, status)
            assertEquals("Client or account does not exists", body.message)
        }

    }


    @Test
    fun `should return unprocessable entity when pix key already exists`() {

        val data = factory
            .withClientId(UUID.randomUUID().toString())
            .withKey(KeyType.DOCUMENT)
            .withAccountType(AccountType.SAVINGS)
            .withPixId(UUID.randomUUID().toString())

        val (clientId, request) = data.buildCreateRestRequest()

        service.createOnError(data.buildCreateRequest()) {
            Status.ALREADY_EXISTS with "Key value is already registered"
        }

        val e = assertThrows<HttpClientResponseException> { client.create(clientId, request) }

        with(e) {
            val body = (response.body() as String).to<ErrorResponse>()
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, status)
            assertEquals("Key value is already registered", body.message)
        }

    }


    @Test
    fun `should return internal server error when occurs some other error during the request`() {

        val data = factory
            .withClientId(UUID.randomUUID().toString())
            .withKey(KeyType.DOCUMENT)
            .withAccountType(AccountType.SAVINGS)
            .withPixId(UUID.randomUUID().toString())

        val (clientId, request) = data.buildCreateRestRequest()

        service.createOnError(data.buildCreateRequest()) {
            Status.INTERNAL with "Something went wrong"
        }

        val e = assertThrows<HttpClientResponseException> { client.create(clientId, request) }

        with(e) {
            val body = (response.body() as String).to<ErrorResponse>()
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, status)
            assertEquals("Something went wrong", body.message)
        }

    }

}
