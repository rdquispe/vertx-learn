package com.rodrigo.quispe.vertx_hello_world.json

import io.vertx.core.json.JsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JsonObjectExample {

  @Test
  fun jsonObjectCanBeMapped() {
    val myJsonObject = JsonObject()
    myJsonObject.put("id", 1)
    myJsonObject.put("name", "Rodrigo")
    myJsonObject.put("love_vertx", true)

    val encoded = myJsonObject.encode()
    assertEquals("""{"id":1,"name":"Rodrigo","love_vertx":true}""", encoded)

    val decodedJsonObject = JsonObject(encoded)
    assertEquals(myJsonObject, decodedJsonObject)
  }

  @Test
  fun jsonObjectCanBCreatedFromMap() {
    val myMap = HashMap<String, Any>()
    myMap["id"] = 1
    myMap["name"] = "Rodrigo"
    myMap["love_vertx"] = true

    val asJsonObject = JsonObject(myMap)
    assertEquals(myMap, asJsonObject.map)
    assertEquals(1, asJsonObject.getInteger("id"))
    assertEquals("Rodrigo", asJsonObject.getString("name"))
    assertEquals(true, asJsonObject.getBoolean("love_vertx"))
  }
}

