package com.zup.rest.keymanager.pixkey.delete

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
class PixKeyDeleteSuccessTest(
    private val client: PixKeyClient,
    private val factory: TestDataBuilder
) {

    lateinit var service: PixKeyServiceMock

    @Test
    fun `should return ok with void body when deleting pix key successfully`() {

        val data = factory
            .withClientId(UUID.randomUUID().toString())
            .withPixId(UUID.randomUUID().toString())

        val (clientId, pixId) = data.buildDeleteRestRequest()

        service.delete(data.buildDeleteRequest()) { data.buildDeleteResponse() }

        val response = client.delete(clientId, pixId)

        with(response) {
            assertEquals(HttpStatus.OK, status)
            assertEquals("{\n}", body())
        }

    }
}