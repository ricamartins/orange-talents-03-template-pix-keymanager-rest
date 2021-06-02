package com.zup.rest.keymanager.pixkey.info

import com.google.protobuf.util.JsonFormat
import com.zup.keymanager.proto.KeyType
import com.zup.keymanager.proto.PixKeyInfoResponse
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
class PixKeyInfoSuccessTest(
    private val client: PixKeyClient,
    private val factory: TestDataBuilder
) {

    lateinit var service: PixKeyServiceMock

    val parser = JsonFormat.parser()

    @Test
    fun `should return pix key info when id pair is provided`() {

        val data = factory
            .withClientId(UUID.randomUUID().toString())
            .withPixId(UUID.randomUUID().toString())
            .withKey(KeyType.EMAIL)

        val (clientId, pixId) = data.buildInfoByIdRestRequest()

        service.info(data.buildInfoByIdRequest()) { data.buildInfoByIdResponse() }

        val response = client.infoById(clientId, pixId)

        with(response) {
            val body = with(PixKeyInfoResponse.newBuilder()) { parser.merge(body(), this); build() }
            assertEquals(HttpStatus.OK, status)
            assertEquals(data.clientId, body.clientId)
            assertEquals(data.pixId, body.pixId)
            assertEquals(KeyType.valueOf(data.keyType!!), body.keyType)
            assertEquals(data.keyValue, body.keyValue)
        }

    }

    @Test
    fun `should return pix key info when key value is provided`() {

        val data = factory.withKey(KeyType.DOCUMENT)

        val keyValue = data.buildInfoByKeyValueRestRequest()

        service.info(data.buildInfoByKeyValueRequest()) { data.buildInfoByKeyValueResponse() }

        val response = client.infoByKeyValue(keyValue)

        with(response) {
            val body = with(PixKeyInfoResponse.newBuilder()) { parser.merge(body(), this); build() }
            assertEquals(HttpStatus.OK, status)
            assertEquals(KeyType.valueOf(data.keyType!!), body.keyType)
            assertEquals(data.keyValue, body.keyValue)
        }

    }

}