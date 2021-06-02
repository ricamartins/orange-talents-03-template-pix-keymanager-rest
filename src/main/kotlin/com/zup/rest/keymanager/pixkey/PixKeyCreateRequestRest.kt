package com.zup.rest.keymanager.pixkey

import com.zup.keymanager.proto.AccountType
import com.zup.keymanager.proto.KeyType
import com.zup.keymanager.proto.PixKeyCreateRequest
import com.zup.rest.keymanager.validations.ValidAccountType
import com.zup.rest.keymanager.validations.ValidKeyType
import com.zup.rest.keymanager.validations.ValidKeyValue
import io.micronaut.core.annotation.Introspected
import org.hibernate.validator.constraints.br.CPF
import javax.validation.constraints.Size

@Introspected @ValidKeyValue
data class PixKeyCreateRequestRest(
    val keyType: String?,
    @field:Size(max=77) //@field:CPF
    val keyValue: String?,
    @ValidAccountType val accountType: String?
) {

    fun toGrpcRequest(clientId: String): PixKeyCreateRequest {
        return PixKeyCreateRequest.newBuilder()
            .setClientId(clientId)
            .setKeyType(KeyType.valueOf(keyType!!))
            .setKeyValue(keyValue ?: "")
            .setAccountType(AccountType.valueOf(accountType!!))
            .build()
    }
}
