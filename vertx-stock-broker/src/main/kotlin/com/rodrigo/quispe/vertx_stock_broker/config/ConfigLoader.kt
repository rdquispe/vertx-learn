package com.rodrigo.quispe.vertx_stock_broker.config

import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject


class ConfigLoader {

  companion object {
    const val CONFIG_FILE = "application.yml"
    const val SERVER_PORT = "SERVER_PORT"
    const val DB_HOST = "DB_HOST"
    const val DB_PORT = "DB_PORT"
    const val DB_DATABASE = "DB_DATABASE"
    const val DB_USER = "DB_USER"
    const val DB_PASSWORD = "DB_PASSWORD"
    private val EXPOSED_ENVIRONMENT_VARIABLES = listOf(SERVER_PORT, DB_HOST, DB_PORT, DB_DATABASE, DB_USER, DB_PASSWORD)

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


