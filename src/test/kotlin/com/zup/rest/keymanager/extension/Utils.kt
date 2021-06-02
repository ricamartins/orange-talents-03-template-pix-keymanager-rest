package com.zup.rest.keymanager.extension

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver

inline fun <reified T, R> implement(
    request: T,
    observer: StreamObserver<R>,
    noinline successFn: ((T) -> R)?,
    noinline errorFn: ((T) -> StatusRuntimeException)?,
    expectedRequest: T?
) {
    if (expectedRequest == null || compare(request, expectedRequest)) {
        if (successFn != null) {
            observer.onNext(successFn(request))
            observer.onCompleted()
        } else if (errorFn != null) {
            observer.onError(errorFn(request))
        } else {
            observer.onError(Status.UNIMPLEMENTED with "No implementation given to this mock")
        }
    } else {
        observer.onError(Status.UNIMPLEMENTED with "Incoming request is different than expected")
    }
}

inline fun <reified T, reified S> compare(request: T, expectedRequest: S): Boolean {
    if (expectedRequest == null) return true

    if (T::class.java != S::class.java)
        return false

    return T::class.java.declaredFields
        .filter { it.name.endsWith("_") }
        .onEach { it.trySetAccessible() }
        .map { it.get(request) == it.get(expectedRequest) }
        .all { it }
}

infix fun Status.with(message: String): StatusRuntimeException {
    return this.withDescription(message).asRuntimeException()
}
