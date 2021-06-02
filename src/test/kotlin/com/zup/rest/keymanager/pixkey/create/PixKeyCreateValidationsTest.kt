package com.zup.rest.keymanager.pixkey.create

import com.zup.rest.keymanager.setup.PixKeyServiceMock
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test

@MicronautTest
class PixKeyCreateValidationsTest(val service: PixKeyServiceMock) {

    @Test
    fun `test getting address from bean`() {
        println(service.address)
    }
}