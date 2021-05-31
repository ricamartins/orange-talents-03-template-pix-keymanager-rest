package com.zup.rest.keymanager.validations

import javax.validation.Constraint
import javax.validation.Payload
import javax.validation.constraints.Pattern
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@Constraint(validatedBy=[])
@Retention(RUNTIME)
@Target(FIELD, VALUE_PARAMETER, CONSTRUCTOR)
@Pattern(
    regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}\$",
    message = "Must be a valid UUID"
)
annotation class ValidUUID(
    val message: String = "Must be a valid UUID",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
