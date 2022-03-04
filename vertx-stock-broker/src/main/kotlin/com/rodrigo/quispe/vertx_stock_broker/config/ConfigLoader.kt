package com.rodrigo.quispe.vertx_stock_broker.config

import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

private const val CONFIG_FILE = "application.yml"

class ConfigLoader {

  companion object {
    const val SERVER_PORT = "SERVER_PORT"
    private val EXPOSED_ENVIRONMENT_VARIABLES = listOf(SERVER_PORT)

    fun load(vertx: Vertx): Future<BrokerConfig> {

      val exposedKeys = JsonArray()
      EXPOSED_ENVIRONMENT_VARIABLES.forEach(exposedKeys::add)

      val envStore = ConfigStoreOptions()
        .setType("env")
        .setConfig(JsonObject().put("keys", exposedKeys))

      val propertyStore = ConfigStoreOptions()
        .setType("sys")
        .setConfig(JsonObject().put("cache", false))

      val yamlStore = ConfigStoreOptions()
        .setType("file")
        .setFormat("yaml")
        .setConfig(JsonObject().put("path", CONFIG_FILE))

      val retriever = ConfigRetriever.create(
        vertx,
        ConfigRetrieverOptions()
          // Order defines overload rules
          .addStore(yamlStore)
          .addStore(propertyStore)
          .addStore(envStore)
      )
      return retriever.config.map(BrokerConfig()::from)
    }
  }
}


