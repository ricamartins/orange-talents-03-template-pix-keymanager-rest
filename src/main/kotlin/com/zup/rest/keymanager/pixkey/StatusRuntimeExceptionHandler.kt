package com.zup.rest.keymanager.pixkey

import com.fasterxml.jackson.databind.ObjectMapper
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import javax.inject.Singleton

@Produces
@Singleton
@Requirements(Requires(classes = [StatusRuntimeException::class, ExceptionHandler::class]))
class StatusRuntimeExceptionHandler: ExceptionHandler<StatusRuntimeException, HttpResponse<ErrorResponse>>  {

    override fun handle(request: HttpRequest<*>, e: StatusRuntimeException): HttpResponse<ErrorResponse> {
        return when(e.status.code) {
            Status.Code.INVALID_ARGUMENT -> toHttpResponse(e, HttpStatus.BAD_REQUEST)
            Status.Code.NOT_FOUND -> toHttpResponse(e, HttpStatus.NOT_FOUND)
            Status.Code.ALREADY_EXISTS -> toHttpResponse(e, HttpStatus.UNPROCESSABLE_ENTITY)
            Status.Code.PERMISSION_DENIED -> toHttpResponse(e, HttpStatus.UNAUTHORIZED)
            Status.Code.FAILED_PRECONDITION -> toHttpResponse(e, HttpStatus.PRECONDITION_FAILED)
            else -> toHttpResponse(e, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    private fun toHttpResponse(e: StatusRuntimeException, httpStatus: HttpStatus): HttpResponse<ErrorResponse> {
        return if (e.status.code == Status.INVALID_ARGUMENT.code) {
                    ErrorResponse(errors = e.status.description?.toFieldErrors())
                } else {
                    ErrorResponse(message = e.status.description)
                }.let { HttpResponse.status<ErrorResponse>(httpStatus).body(it) }
    }

    private fun String.toFieldErrors(): List<FieldError> {
        return this.to<List<String>>().map { it.split(": ") }.map { FieldError(it[0], it[1]) }
    }

    inline fun <reified T> String.to(): T = ObjectMapper().readValue(this, T::class.java)
}

data class ErrorResponse(val message: String? = "", val errors: List<FieldError>? = listOf())
data class FieldError(val field: String, val message: String)
