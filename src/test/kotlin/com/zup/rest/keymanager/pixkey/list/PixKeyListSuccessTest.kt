package com.zup.rest.keymanager.pixkey.list

import com.google.protobuf.util.JsonFormat
import com.zup.keymanager.proto.KeyType
import com.zup.keymanager.proto.PixKeyListResponse
import com.zup.rest.keymanager.extension.GrpcMockServer
import com.zup.rest.keymanager.setup.PixKeyClient
import com.zup.rest.keymanager.setup.PixKeyServiceMock
import com.zup.rest.keymanager.setup.TestDataBuilder
import io.micronaut.http.HttpStatus
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest @GrpcMockServer
class PixKeyListSuccessTest(
    private val client: PixKeyClient,
    private val factory: TestDataBuilder
) {

    lateinit var service: PixKeyServiceMock

    val parser = JsonFormat.parser()

    @Test
    fun `should list all three pix keys the client has`() {

        val data = factory
            .withClientId(UUID.randomUUID().toString())
            .addKey(KeyType.DOCUMENT)
            .addKey(KeyType.EMAIL)
            .addKey(KeyType.RANDOM)

        val clientId = data.buildListRestRequest()

        service.list(data.buildListRequest()) { data.buildListResponse() }

        val response = client.list(clientId)

        with(response) {
            val body = with(PixKeyListResponse.newBuilder()) { parser.merge(body(), this); build() }
            assertEquals(HttpStatus.OK, status)
            assertTrue(body.pixKeysList.size == 3)
            assertEquals(data.clientId, body.pixKeysList[0].clientId)
        }

    }

    @Test
    fun `should return empty list`() {

        val data = factory.withClientId(UUID.randomUUID().toString())

        val clientId = data.buildListRestRequest()

        service.list(data.buildListRequest()) { data.buildListResponse() }

        val response = client.list(clientId)

        with(response) {
            val body = with(PixKeyListResponse.newBuilder()) { parser.merge(body(), this); build() }
            assertEquals(HttpStatus.OK, status)
            assertTrue(body.pixKeysList.size == 0)
            assertEquals("{\n}", body())
        }

    }
}