package com.zup.rest.keymanager.pixkey

import com.zup.keymanager.proto.PixKeyServiceGrpc.newBlockingStub
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class PixKeyClientFactory(@GrpcChannel("keymanager") val channel: ManagedChannel) {

    @Singleton
    fun pixKeyClient() = newBlockingStub(channel)
}