package com.zup.rest.keymanager.validations

import com.zup.keymanager.proto.KeyType
import java.lang.IllegalArgumentException
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [ValidKeyTypeValidator::class])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ValidKeyType(
    val message: String = "Not a valid key type",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<out Payload>> = []
)

@Singleton
class ValidKeyTypeValidator: ConstraintValidator<ValidKeyType, String> {

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {

        if (value.isNullOrBlank()) return false

        return try {
            KeyType.valueOf(value).let { it != KeyType.UNRECOGNIZED && it != KeyType.UNKNOWN_KEY_TYPE }
        } catch (e: IllegalArgumentException) {
            return false
        }

    }

}
