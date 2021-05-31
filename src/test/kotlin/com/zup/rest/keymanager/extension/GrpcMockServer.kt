package com.zup.rest.keymanager.extension

import org.junit.jupiter.api.extension.ExtendWith

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@ExtendWith(GrpcMockServiceExtension::class)
annotation class GrpcMockService(val port: String = "50051")