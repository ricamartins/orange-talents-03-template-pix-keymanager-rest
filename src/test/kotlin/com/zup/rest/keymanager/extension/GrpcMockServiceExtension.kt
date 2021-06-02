package com.zup.rest.keymanager.extension

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerBuilder
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestInstancePostProcessor
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.RuntimeException

class GrpcMockServiceExtension: TestInstancePostProcessor, BeforeAllCallback, AfterAllCallback {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    lateinit var services: Map<String, BindableService>
    lateinit var server: Server

    private val mainFile = File("src/main/resources/application.yml")
    private val testFile = File("src/test/resources/application.yml")

    private val mapper = ObjectMapper(YAMLFactory())

    override fun beforeAll(context: ExtensionContext) {
        LOGGER.info("Starting server for test class: ${context.testClass.get().name}")

        val port = getPort(context)

        services = getServicesDefinedAsTestClassProperties(context)

        server = ServerBuilder.forPort(port).addServices(services.values).build()

        server.start()

        LOGGER.info("Server started on port: ${server.port}")
    }

    override fun postProcessTestInstance(testInstance: Any, context: ExtensionContext) {
        LOGGER.info("Injecting gRPC mock services for test instance: ${testInstance.javaClass}")

        services.forEach { (fieldName, service) ->
            testInstance.javaClass.getField(fieldName)
                .apply { trySetAccessible() }
                .set(testInstance, service)
        }

        LOGGER.info("gRPC mock services injected: ${services.values}")
    }

    override fun afterAll(context: ExtensionContext) {
        LOGGER.info("Stopping the server on port: ${server.port}")

        server.shutdownNow()

        LOGGER.info("Server on port ${server.port} was stopped successfully")
    }

    private fun getPort(context: ExtensionContext): Int {
        val (port, channel) = getGrpcMockServerProperties(context)
        return port.ifBlank {
            if (channel.isNotBlank())
                getPortFromApplicationYML(channel).ifBlank {
                    throw RuntimeException("Client channel '${channel}' not found on either application.yml")
                }
            else
                "50051"
        }.toInt()
    }

    private fun getGrpcMockServerProperties(context: ExtensionContext): Pair<String, String> {
        return context.testClass
            .map { it.getAnnotation(GrpcMockServer::class.java) }
            .map { it.port to it.channel }
            .get()
    }

    private fun getPortFromApplicationYML(channelName: String): String {
        return runCatching { mapper.readTree(testFile).getPort(channelName) }
            .recoverCatching { mapper.readTree(mainFile).getPort(channelName) }
            .getOrDefault("")
    }

    private fun getServicesDefinedAsTestClassProperties(context: ExtensionContext): Map<String, BindableService> {
        return context.testClass.get().declaredFields
            .filter { BindableService::class.java.isAssignableFrom(it.type) }
            .map { it.name to it.type.getDeclaredConstructor().newInstance() as BindableService }
            .toMap()
    }

}

fun JsonNode.getPort(channelName: String): String {
    return this.get("grpc").get("channels").get(channelName).get("address").asText().split(":")[1]
}

fun ServerBuilder<*>.addServices(services: Collection<BindableService>): ServerBuilder<*> {
    services.forEach { service -> this.addService(service) }
    return this
}
