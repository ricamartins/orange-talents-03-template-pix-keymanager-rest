package com.zup.rest.keymanager.setup

import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerBuilder
import org.junit.jupiter.api.extension.*
import java.io.File

class GrpcMockServiceExtension: TestInstancePostProcessor, BeforeAllCallback, AfterAllCallback {

    lateinit var services: Map<String, BindableService>
    lateinit var server: Server

    override fun postProcessTestInstance(testInstance: Any, context: ExtensionContext) {
        println("injecting grpc mock services: ${services.values}")

        services.forEach { fieldName, service ->
            testInstance.javaClass.getField(fieldName).set(testInstance, service)
        }
    }

    override fun beforeAll(context: ExtensionContext) {
        println("starting server for class: ${context.testClass.get().name}")

        File("src/test/resources/application.yml").takeIf { it.isFile }
            ?.let { it.readLines().forEach(::println) }

        val port = context.testClass
            .map { it.getAnnotation(GrpcMockService::class.java) }
            .map { it.port }
            .orElse("50051").toInt()

        services = context.testClass.get().declaredFields
            .filter { BindableService::class.java.isAssignableFrom(it.type) }
            .map { it.name to it.type.getDeclaredConstructor().newInstance() as BindableService }
            .toMap()

        server = with(ServerBuilder.forPort(port)) {
            services.values.forEach { service -> this.addService(service) }
            build()
        }

        server.start()
    }

    override fun afterAll(context: ExtensionContext) {
        println("stopping server")
        server.shutdownNow()
    }

}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@ExtendWith(GrpcMockServiceExtension::class)
annotation class GrpcMockService(val port: String = "50051")
