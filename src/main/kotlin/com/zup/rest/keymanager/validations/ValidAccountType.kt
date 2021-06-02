package com.zup.rest.keymanager.validations

import com.zup.keymanager.proto.AccountType
import com.zup.keymanager.proto.KeyType
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [ValidAccountTypeValidator::class])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ValidAccountType(
    val message: String = "Not a valid account type",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<out Payload>> = []
)

@Singleton
class ValidAccountTypeValidator: ConstraintValidator<ValidAccountType, String> {

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {

        if (value.isNullOrBlank()) return false

        return try {
            AccountType.valueOf(value).let { it != AccountType.UNRECOGNIZED && it != AccountType.UNKNOWN_ACCOUNT_TYPE }
        } catch (e: IllegalArgumentException) {
            return false
        }
    }

}
