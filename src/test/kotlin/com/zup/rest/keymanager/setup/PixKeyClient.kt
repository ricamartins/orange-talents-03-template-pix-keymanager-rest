package com.zup.rest.keymanager.setup

import com.zup.rest.keymanager.pixkey.PixKeyCreateRequestRest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("/pixkey")
interface PixKeyClient {

    @Post("/clients/{clientId}")
    fun create(@PathVariable clientId: String, @Body request: PixKeyCreateRequestRest): HttpResponse<String>

    @Delete("/clients/{clientId}/pix/{pixId}")
    fun delete(@PathVariable clientId: String, @PathVariable pixId: String): HttpResponse<String>

    @Get
    fun infoByKeyValue(@QueryValue value: String): HttpResponse<String>

    @Get("/clients/{clientId}/pix/{pixId}")
    fun infoById(@PathVariable clientId: String, @PathVariable pixId: String): HttpResponse<String>

    @Get("/clients/{clientId}")
    fun list(@PathVariable clientId: String): HttpResponse<String>

}