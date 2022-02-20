package com.rodrigo.quispe.vertx_hello_world.json

data class Person(
  val id: Int,
  val name: String,
  val lovesVertx: Boolean
) {
  constructor() : this(0, "", false)
}
