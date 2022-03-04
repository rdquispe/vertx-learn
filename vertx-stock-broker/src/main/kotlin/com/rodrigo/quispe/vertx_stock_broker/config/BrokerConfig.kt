package com.rodrigo.quispe.vertx_stock_broker.config

import io.vertx.core.json.JsonObject
import java.util.Objects


class BrokerConfig {

  var serverPort = 0
  var version = ""
  var dbConfig: DbConfig = DbConfig()

  fun from(config: JsonObject): BrokerConfig {
    val serverPort = config.getInteger(ConfigLoader.SERVER_PORT)
    if (Objects.isNull(serverPort)) {
      throw RuntimeException(ConfigLoader.SERVER_PORT + " not configured!")
    }

    val version = config.getString("version")
    if (Objects.isNull(version)) {
      throw RuntimeException("version is not configured in config file!")
    }

    val brokerConfig = BrokerConfig()
    brokerConfig.serverPort = config.getInteger(ConfigLoader.SERVER_PORT)
    brokerConfig.version = version
    brokerConfig.dbConfig = parseDbConfig(config)
    return brokerConfig
  }

  private fun parseDbConfig(config: JsonObject): DbConfig =
    DbConfig(
      host = config.getString(ConfigLoader.DB_HOST),
      port = config.getInteger(ConfigLoader.DB_PORT),
      database = config.getString(ConfigLoader.DB_DATABASE),
      user = config.getString(ConfigLoader.DB_USER),
      password = config.getString(ConfigLoader.DB_PASSWORD)
    )

  override fun toString(): String {
    return "BrokerConfig(serverPort=$serverPort, version='$version', dbConfig=$dbConfig)"
  }
}
