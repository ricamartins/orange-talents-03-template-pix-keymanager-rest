package com.zup.rest.keymanager.pixkey

import com.google.protobuf.util.JsonFormat
import com.zup.keymanager.proto.PixKeyDeleteRequest
import com.zup.keymanager.proto.PixKeyInfoRequest
import com.zup.keymanager.proto.PixKeyListRequest
import com.zup.keymanager.proto.PixKeyServiceGrpc.PixKeyServiceBlockingStub
import com.zup.rest.keymanager.validations.ValidUUID
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Controller("/pixkey") @Validated
class PixKeyController(private val pixKeyClient: PixKeyServiceBlockingStub) {

    private val printer = JsonFormat.printer()

    @Post("/clients/{clientId}")
    fun create(@PathVariable @ValidUUID clientId: String, @Valid @Body request: PixKeyCreateRequestRest): HttpResponse<String> {
        return request.toGrpcRequest(clientId)
            .let(pixKeyClient::create)
            .let(printer::print)
            .let { HttpResponse.created(it) }
    }

    @Delete("/clients/{clientId}/pix/{pixId}")
    fun delete(@PathVariable @ValidUUID clientId: String, @PathVariable @ValidUUID pixId: String): String {
        return PixKeyDeleteRequest.newBuilder().setClientId(clientId).setPixId(pixId).build()
            .let(pixKeyClient::delete)
            .let(printer::print)
    }

    @Get
    fun infoByKeyValue(@QueryValue @NotBlank @Size(max=77) value: String): String {
        return PixKeyInfoRequest.newBuilder().setKeyValue(value).build()
            .let(pixKeyClient::info)
            .let(printer::print)
    }

    @Get("/clients/{clientId}/pix/{pixId}")
    fun infoById(@PathVariable @ValidUUID clientId: String, @PathVariable @ValidUUID pixId: String): String {
        return with(PixKeyInfoRequest.newBuilder()) {
            infoPairBuilder.let { pair -> pair.clientId = clientId; pair.pixId = pixId }
            build()
        }
        .let(pixKeyClient::info)
        .let(printer::print)
    }

    @Get("/clients/{clientId}")
    fun list(@PathVariable @ValidUUID clientId: String): String {
        return PixKeyListRequest.newBuilder().setClientId(clientId).build()
            .let(pixKeyClient::list)
            .let(printer::print)
    }
}
