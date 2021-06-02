package com.zup.rest.keymanager.pixkey.info

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
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest @GrpcMockServer
class PixKeyInfoFailureTest(
    private val client: PixKeyClient,
    private val factory: TestDataBuilder
) {

    lateinit var service: PixKeyServiceMock

    @Test
    fun `should return not found when could not find pix key with id pair`() {

        val data = factory
            .withClientId(UUID.randomUUID().toString())
            .withPixId(UUID.randomUUID().toString())
            .withKey(KeyType.PHONE)

        val (clientId, pixId) = data.buildInfoByIdRestRequest()

        service.infoOnError(data.buildInfoByIdRequest()) {
            Status.NOT_FOUND with "Pix ID does not exists"
        }

        val response = client.infoById(clientId, pixId)

        with(response) {
            val body = body().to<ErrorResponse>()
            assertEquals(HttpStatus.NOT_FOUND, status)
            assertEquals("Pix ID does not exists", body.message)
        }

    }

    @Test
    fun `should return not found when could not find pix key with key value`() {

        val data = factory
            .withKey(KeyType.DOCUMENT)

        val keyValue = data.buildInfoByKeyValueRestRequest()

        service.infoOnError(data.buildInfoByKeyValueRequest()) {
            Status.NOT_FOUND with "Pix key not registered in the central bank"
        }

        val response = client.infoByKeyValue(keyValue)

        with(response) {
            val body = body().to<ErrorResponse>()
            assertEquals(io.micronaut.http.HttpStatus.NOT_FOUND, status)
            assertEquals("Pix key not registered in the central bank", body.message)
        }

    }

}