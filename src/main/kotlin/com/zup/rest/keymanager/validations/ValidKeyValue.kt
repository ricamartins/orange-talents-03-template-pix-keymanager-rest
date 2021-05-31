package com.zup.rest.keymanager.validations

import com.zup.keymanager.proto.KeyType
import com.zup.rest.keymanager.pixkey.PixKeyCreateRequestRest
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [ValidKeyValueValidator::class])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class ValidKeyValue(
    val message: String = "Not a valid key for chosen key type",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<out Payload>> = []
) {
}

@Singleton
class ValidKeyValueValidator: ConstraintValidator<ValidKeyValue, PixKeyCreateRequestRest> {

    override fun isValid(request: PixKeyCreateRequestRest, context: ConstraintValidatorContext): Boolean {

        if (!ValidKeyTypeValidator().isValid(request.keyType, context))
            return false.also { addConstraint("Not a valid key type", "keyType", context) }

        return when(request.keyType) {
            KeyType.RANDOM.name -> request.keyValue.isNullOrBlank()
                .also { if (!it) addConstraint("Must be blank or null", "keyValue", context) }

            KeyType.PHONE.name -> (request.keyValue?.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex()) ?: false)
                .also { addConstraint("Must be a valid phone number", "keyValue", context) }

            KeyType.EMAIL.name -> (request.keyValue?.let {
                it.isNotBlank().and(EmailValidator().isValid(request.keyValue, context))
            } ?: false).also { addConstraint("Must be a valid email", "keyValue", context) }

            KeyType.DOCUMENT.name -> CPFValidator().run { initialize(null)
                isValid(request.keyValue, context)
            }.also { if (!it) addConstraint("Must be valid CPF", "keyValue", context) }

            else -> false.also { addConstraint("Not a valid key type", "keyType", context) }
        }
    }

    private fun addConstraint(message: String, field: String, context: ConstraintValidatorContext) {
        context.disableDefaultConstraintViolation()
        context.buildConstraintViolationWithTemplate(message)
            .addPropertyNode(field)
            .addConstraintViolation()
    }
}