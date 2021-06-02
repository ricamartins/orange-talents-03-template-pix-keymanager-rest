package com.zup.rest.keymanager.setup

import com.fasterxml.jackson.databind.ObjectMapper
import com.zup.keymanager.proto.*
import com.zup.rest.keymanager.pixkey.PixKeyCreateRequestRest
import io.micronaut.core.annotation.Creator
import java.time.LocalDateTime
import java.util.*
import javax.inject.Singleton

@Singleton
class TestDataBuilder(
    val clientId: String? = null,
    val keyType: String? = null,
    val keyValue: String? = null,
    val accountType: String? = null,
    val pixId: String? = null,
    val pixKeys: PixKeyListResponse.Builder = PixKeyListResponse.newBuilder()
) {

    @Creator
    constructor(): this(null, null, null, null, null, PixKeyListResponse.newBuilder())

    fun withClientId(clientId: String): TestDataBuilder {
        return TestDataBuilder(clientId, keyType, keyValue, accountType, pixId, pixKeys)
    }

    fun withKey(keyType: KeyType): TestDataBuilder {
        return TestDataBuilder(clientId, keyType.name, validValueFor(keyType), accountType, pixId, pixKeys)
    }

    fun withKeyType(keyType: KeyType): TestDataBuilder {
        return TestDataBuilder(clientId, keyType.name, keyValue, accountType, pixId, pixKeys)
    }

    fun withKeyValue(keyValue: String): TestDataBuilder {
        return TestDataBuilder(clientId, keyType, keyValue, accountType, pixId, pixKeys)
    }

    fun withAccountType(accountType: AccountType): TestDataBuilder {
        return TestDataBuilder(clientId, keyType, keyValue, accountType.name, pixId, pixKeys)
    }

    fun withPixId(pixId: String): TestDataBuilder {
        return TestDataBuilder(clientId, keyType, keyValue, accountType, pixId, pixKeys)
    }

    fun addKey(keyType: KeyType): TestDataBuilder {
        pixKeys.addPixKeys(createShortInfoResponse(keyType))
        return TestDataBuilder(clientId, this.keyType, keyValue, accountType, pixId, pixKeys)
    }

    fun buildCreateRestRequest() = clientId!! to PixKeyCreateRequestRest(keyType, keyValue, accountType)
    fun buildDeleteRestRequest() = clientId!! to pixId!!
    fun buildInfoByIdRestRequest() = clientId!! to pixId!!
    fun buildInfoByKeyValueRestRequest() = keyValue!!
    fun buildListRestRequest() = clientId!!

    fun buildCreateRequest() = PixKeyCreateRequest.newBuilder()
        .setClientId(clientId)
        .setKeyType(KeyType.valueOf(keyType!!))
        .setKeyValue(keyValue)
        .setAccountType(AccountType.valueOf(accountType!!))
        .build()

    fun buildDeleteRequest() = PixKeyDeleteRequest.newBuilder()
        .setClientId(clientId)
        .setPixId(pixId)
        .build()

    fun buildInfoByIdRequest() = PixKeyInfoRequest.newBuilder()
        .setInfoPair(PixKeyInfoRequest.PixKeyInfoPair.newBuilder()
            .setClientId(clientId)
            .setPixId(pixId)
            .build())
        .build()

    fun buildInfoByKeyValueRequest() = PixKeyInfoRequest.newBuilder()
        .setKeyValue(keyValue)
        .build()

    fun buildListRequest() = PixKeyListRequest.newBuilder()
        .setClientId(clientId)
        .build()

    fun buildCreateResponse() = PixKeyCreateResponse.newBuilder().setClientId(clientId).setPixId(pixId).build()

    fun buildDeleteResponse() = Void.newBuilder().build()
    fun buildInfoByIdResponse() = createFullInfoResponse()
    fun buildInfoByKeyValueResponse() = createFullKeyValueInfoResponse()
    fun buildListResponse() = pixKeys.build()

    private fun createFullInfoResponse(): PixKeyInfoResponse {
        return PixKeyInfoResponse.newBuilder()
            .setClientId(clientId)
            .setPixId(pixId)
            .setKeyType(KeyType.valueOf(keyType!!))
            .setKeyValue(validValueFor(KeyType.valueOf(keyType)))
            .setOwner(OwnerDetails.newBuilder()
                .setName("Rafael M C Ponte")
                .setDocument("02467781054")
                .build())
            .setAccount(AccountDetails.newBuilder()
                .setName("Itaú Unibanco SA")
                .setBranch("0001")
                .setNumber("291900")
                .setAccountType(AccountType.CHECKING)
                .build())
            .setCreatedAt(LocalDateTime.now().toString())
            .build()
    }

    private fun createFullKeyValueInfoResponse(): PixKeyInfoResponse {
        return PixKeyInfoResponse.newBuilder()
            .setKeyType(KeyType.valueOf(keyType!!))
            .setKeyValue(validValueFor(KeyType.valueOf(keyType)))
            .setOwner(OwnerDetails.newBuilder()
                .setName("Rafael M C Ponte")
                .setDocument("02467781054")
                .build())
            .setAccount(AccountDetails.newBuilder()
                .setName("Itaú Unibanco SA")
                .setBranch("0001")
                .setNumber("291900")
                .setAccountType(AccountType.CHECKING)
                .build())
            .setCreatedAt(LocalDateTime.now().toString())
            .build()
    }

    private fun createShortInfoResponse(keyType: KeyType): PixKeyInfoResponse {
        return PixKeyInfoResponse.newBuilder()
            .setClientId(clientId)
            .setPixId(UUID.randomUUID().toString())
            .setKeyType(keyType)
            .setKeyValue(validValueFor(keyType, false))
            .setAccount(AccountDetails.newBuilder()
                .setAccountType(AccountType.SAVINGS)
                .build())
            .setCreatedAt(LocalDateTime.now().toString())
            .build()
    }

    private fun validValueFor(keyType: KeyType, forRequest: Boolean = true): String? {
        return when(keyType) {
            KeyType.DOCUMENT -> "02467781054"
            KeyType.PHONE -> "+5511987654321"
            KeyType.EMAIL -> "rafael@mail.com"
            KeyType.RANDOM -> if (forRequest) "" else UUID.randomUUID().toString()
            else -> null
        }
    }

}

inline fun <reified T> String.to(): T = ObjectMapper().readValue(this, T::class.java)