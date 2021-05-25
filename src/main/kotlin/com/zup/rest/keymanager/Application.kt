package com.zup.rest.keymanager

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("com.zup.rest.keymanager")
		.start()
}

