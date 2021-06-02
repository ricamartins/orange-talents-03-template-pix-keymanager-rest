package com.zup.rest.keymanager.extension

import com.zup.keymanager.proto.PixKeyServiceGrpc.*
import io.grpc.BindableService
import java.io.File
import java.lang.reflect.Method

fun generateMockServicesFor(packageName: String, vararg classes: Class<out BindableService>) {
    classes.forEach { clazz ->
        var content = ""

        content += packageDeclaration(packageName)
        content += imports(clazz)
        content += classDeclaration(clazz)

        clazz.declaredMethods.filter(::returnsVoid).onEach { method ->

            content += createSuccessField(method)
            content += createErrorField(method)
            content += createRequestField(method)

        }.forEach { method ->

            content += createMockMethod(method)
            content += createMockOnErrorMethod(method)
            content += overrideMethod(method)

        }

        content += "}"

        File(getFilePath(packageName, clazz)).writeText(content)
    }
}

fun packageDeclaration(packageName: String): String {
    return "package $packageName\n\n"
}

fun imports(clazz: Class<out BindableService>): String {
    return "import ${clazz.packageName}.*\n" +
            "import ${clazz.name.substringBefore("$")}.*\n" +
            "import io.grpc.StatusRuntimeException\n" +
            "import io.grpc.stub.StreamObserver\n" +
            "import com.zup.rest.keymanager.extension.implement\n\n"
}

fun classDeclaration(clazz: Class<out BindableService>): String {
    return "class ${classFileName(clazz)}: ${clazz.simpleName}() {\n\n"
}

fun createSuccessField(method: Method): String {
    return "\tvar ${method.name}: ((${getRequestType(method)}) -> (${getResponseType(method)}))? = null\n"
}

fun createErrorField(method: Method): String {
    return "\tvar ${method.name}OnError: ((${getRequestType(method)}) -> (StatusRuntimeException))? = null\n"
}

fun createRequestField(method: Method): String {
    return "\tprivate var ${method.name}Request: ${getRequestType(method)}? = null\n\n"
}

fun createMockMethod(method: Method): String {
    return "\tfun ${method.name}(request: ${getRequestType(method)}?, fn: ((${getRequestType(method)}) -> (${getResponseType(method)}))?) {\n" +
            "\t\t${method.name}Request = request\n" +
            "\t\t${method.name} = fn\n" +
            "\t}\n\n"
}

fun createMockOnErrorMethod(method: Method): String {
    return "\tfun ${method.name}OnError(request: ${getRequestType(method)}?, fn: ((${getRequestType(method)}) -> (StatusRuntimeException))?) {\n" +
            "\t\t${method.name}Request = request\n" +
            "\t\t${method.name}OnError = fn\n" +
            "\t}\n\n"
}

fun overrideMethod(method: Method): String {
    return "\toverride fun ${method.name}(request: ${getRequestType(method)}, observer: StreamObserver<${getResponseType(method)}>) {\n" +
            "\t\timplement(request, observer, ${method.name}, ${method.name}OnError, ${method.name}Request)\n" +
            "\t}\n\n"
}

fun getFilePath(packageName: String, clazz: Class<out BindableService>): String {
    return "src\\test\\kotlin\\${toPath(packageName)}\\${classFileName(clazz)}.kt"
}

fun toPath(packageName: String): String {
    return packageName.replace(".", "\\")
}

fun classFileName(clazz: Class<out BindableService>): String {
    return clazz.simpleName.replace("ImplBase", "Mock")
}

fun getRequestType(method: Method): String {
    return method.parameters[0].type.simpleName
}

fun getResponseType(method: Method): String {
    return method.parameters[1].parameterizedType.typeName.let {
        it.substring(it.indexOf("<"), it.indexOf(">")).split(".").last()
    }
}

fun returnsVoid(method: Method) = Void.TYPE.isAssignableFrom(method.returnType)

fun main() {
    generateMockServicesFor("com.zup.rest.keymanager", PixKeyServiceImplBase::class.java)
}