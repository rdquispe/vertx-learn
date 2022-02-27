package com.rodrigo.quispe.vertx_stock_broker.config

import io.vertx.core.json.JsonObject
import java.util.Objects


class BrokerConfig {

  var serverPort = 0

  fun from(config: JsonObject): BrokerConfig {
    val serverPort = config.getInteger(ConfigLoader.SERVER_PORT)
    if (Objects.isNull(serverPort)) {
      throw RuntimeException(ConfigLoader.SERVER_PORT + " not configured!")
    }
    val brokerConfig = BrokerConfig()
    brokerConfig.serverPort = config.getInteger(ConfigLoader.SERVER_PORT)
    return brokerConfig
  }
}
