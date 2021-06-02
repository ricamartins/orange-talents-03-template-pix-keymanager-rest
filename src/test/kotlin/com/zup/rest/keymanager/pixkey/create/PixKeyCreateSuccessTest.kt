package com.zup.rest.keymanager.pixkey.create

import com.google.protobuf.util.JsonFormat
import com.zup.keymanager.proto.*
import com.zup.rest.keymanager.extension.GrpcMockServer
import com.zup.rest.keymanager.setup.PixKeyClient
import com.zup.rest.keymanager.setup.PixKeyServiceMock
import com.zup.rest.keymanager.setup.TestDataBuilder
import io.micronaut.http.HttpStatus
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest @GrpcMockServer
class PixKeyCreateSuccessTest(
    private val client: PixKeyClient,
    private val factory: TestDataBuilder
) {

    lateinit var service: PixKeyServiceMock
    val parser = JsonFormat.parser()

    @Test
    fun `should return created with client id and pix id when valid document`() {

        val data = factory
            .withClientId(UUID.randomUUID().toString())
            .withKey(KeyType.DOCUMENT)
            .withAccountType(AccountType.SAVINGS)
            .withPixId(UUID.randomUUID().toString())

        val (clientId, request) = data.buildCreateRestRequest()

        service.create(data.buildCreateRequest()) { data.buildCreateResponse() }

        val response = client.create(clientId, request)

        with(response) {
            val body = with(PixKeyCreateResponse.newBuilder()) { parser.merge(body(), this); build() }
            assertEquals(HttpStatus.CREATED, status)
            assertEquals(data.clientId, body.clientId)
            assertEquals(data.pixId, body.pixId)
        }
    }

    @Test
    fun `should return created with client id and pix id when valid email`() {

        val data = factory
            .withClientId(UUID.randomUUID().toString())
            .withKey(KeyType.EMAIL)
            .withAccountType(AccountType.CHECKING)
            .withPixId(UUID.randomUUID().toString())

        val (clientId, request) = data.buildCreateRestRequest()

        service.create(data.buildCreateRequest()) { data.buildCreateResponse() }

        val response = client.create(clientId, request)

        with(response) {
            val body = with(PixKeyCreateResponse.newBuilder()) { parser.merge(body(), this); build() }
            assertEquals(HttpStatus.CREATED, status)
            assertEquals(data.clientId, body.clientId)
            assertEquals(data.pixId, body.pixId)
        }

    }

    @Test
    fun `should return created with client id and pix id when valid phone`() {

        val data = factory
            .withClientId(UUID.randomUUID().toString())
            .withKey(KeyType.PHONE)
            .withAccountType(AccountType.SAVINGS)
            .withPixId(UUID.randomUUID().toString())

        val (clientId, request) = data.buildCreateRestRequest()

        service.create(data.buildCreateRequest()) { data.buildCreateResponse() }

        val response = client.create(clientId, request)

        with(response) {
            val body = with(PixKeyCreateResponse.newBuilder()) { parser.merge(body(), this); build() }
            assertEquals(HttpStatus.CREATED, status)
            assertEquals(data.clientId, body.clientId)
            assertEquals(data.pixId, body.pixId)
        }

    }

    @Test
    fun `should return created with client id and pix id when valid random`() {

        val data = factory
            .withClientId(UUID.randomUUID().toString())
            .withKey(KeyType.RANDOM)
            .withAccountType(AccountType.CHECKING)
            .withPixId(UUID.randomUUID().toString())

        val (clientId, request) = data.buildCreateRestRequest()

        service.create(data.buildCreateRequest()) { data.buildCreateResponse() }

        val response = client.create(clientId, request)

        with(response) {
            val body = with(PixKeyCreateResponse.newBuilder()) { parser.merge(body(), this); build() }
            assertEquals(HttpStatus.CREATED, status)
            assertEquals(data.clientId, body.clientId)
            assertEquals(data.pixId, body.pixId)
        }

    }

}
