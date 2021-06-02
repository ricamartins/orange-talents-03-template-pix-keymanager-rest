package com.zup.rest.keymanager.pixkey

import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import io.micronaut.validation.exceptions.ConstraintExceptionHandler
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Produces
@Singleton
@Replaces(bean = ConstraintExceptionHandler::class)
@Requirements(Requires(classes = [ConstraintViolationException::class, ExceptionHandler::class]))
class ValidationExceptionHandler: ExceptionHandler<ConstraintViolationException, HttpResponse<ErrorResponse>> {

    override fun handle(request: HttpRequest<*>, exception: ConstraintViolationException): HttpResponse<ErrorResponse> {
        return exception.constraintViolations
            .map { FieldError(it.propertyPath.last().name, it.message) }
            .toList()
            .let { HttpResponse.badRequest(ErrorResponse(errors = it)) }
    }
}