package com.zup.rest.keymanager.setup

import com.zup.keymanager.proto.*
import com.zup.keymanager.proto.PixKeyServiceGrpc.*
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import com.zup.rest.keymanager.extension.implement
import io.micronaut.context.annotation.Value
import javax.inject.Singleton

@Singleton
class PixKeyServiceMock: PixKeyServiceImplBase() {

	@Value("\${grpc.channels.keymanager.address}")
	lateinit var address: String

	var list: ((PixKeyListRequest) -> (PixKeyListResponse))? = null
	var listOnError: ((PixKeyListRequest) -> (StatusRuntimeException))? = null
	private var listRequest: PixKeyListRequest? = null

	var info: ((PixKeyInfoRequest) -> (PixKeyInfoResponse))? = null
	var infoOnError: ((PixKeyInfoRequest) -> (StatusRuntimeException))? = null
	private var infoRequest: PixKeyInfoRequest? = null

	var delete: ((PixKeyDeleteRequest) -> (Void))? = null
	var deleteOnError: ((PixKeyDeleteRequest) -> (StatusRuntimeException))? = null
	private var deleteRequest: PixKeyDeleteRequest? = null

	var create: ((PixKeyCreateRequest) -> (PixKeyCreateResponse))? = null
	var createOnError: ((PixKeyCreateRequest) -> (StatusRuntimeException))? = null
	private var createRequest: PixKeyCreateRequest? = null

	fun list(request: PixKeyListRequest?, fn: ((PixKeyListRequest) -> (PixKeyListResponse))?) {
		listRequest = request
		list = fn
	}

	fun listOnError(request: PixKeyListRequest?, fn: ((PixKeyListRequest) -> (StatusRuntimeException))?) {
		listRequest = request
		listOnError = fn
	}

	override fun list(request: PixKeyListRequest, observer: StreamObserver<PixKeyListResponse>) {
		implement(request, observer, list, listOnError, listRequest)
	}

	fun info(request: PixKeyInfoRequest?, fn: ((PixKeyInfoRequest) -> (PixKeyInfoResponse))?) {
		infoRequest = request
		info = fn
	}

	fun infoOnError(request: PixKeyInfoRequest?, fn: ((PixKeyInfoRequest) -> (StatusRuntimeException))?) {
		infoRequest = request
		infoOnError = fn
	}

	override fun info(request: PixKeyInfoRequest, observer: StreamObserver<PixKeyInfoResponse>) {
		implement(request, observer, info, infoOnError, infoRequest)
	}

	fun delete(request: PixKeyDeleteRequest?, fn: ((PixKeyDeleteRequest) -> (Void))?) {
		deleteRequest = request
		delete = fn
	}

	fun deleteOnError(request: PixKeyDeleteRequest?, fn: ((PixKeyDeleteRequest) -> (StatusRuntimeException))?) {
		deleteRequest = request
		deleteOnError = fn
	}

	override fun delete(request: PixKeyDeleteRequest, observer: StreamObserver<Void>) {
		implement(request, observer, delete, deleteOnError, deleteRequest)
	}

	fun create(request: PixKeyCreateRequest?, fn: ((PixKeyCreateRequest) -> (PixKeyCreateResponse))?) {
		createRequest = request
		create = fn
	}

	fun createOnError(request: PixKeyCreateRequest?, fn: ((PixKeyCreateRequest) -> (StatusRuntimeException))?) {
		createRequest = request
		createOnError = fn
	}

	override fun create(request: PixKeyCreateRequest, observer: StreamObserver<PixKeyCreateResponse>) {
		implement(request, observer, create, createOnError, createRequest)
	}

}