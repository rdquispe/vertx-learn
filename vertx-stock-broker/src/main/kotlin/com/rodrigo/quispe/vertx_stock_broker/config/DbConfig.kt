package com.rodrigo.quispe.vertx_stock_broker.config

data class DbConfig(
  val host: String,
  val port: Int,
  val database: String,
  val user: String,
  val password: String
) {

  constructor() : this("", Int.MIN_VALUE, "", "", "")

  override fun toString(): String {
    return "DbConfig{" +
      "host='" + host + '\'' +
      ", port=" + port +
      ", database='" + database + '\'' +
      ", user='" + user + '\'' +
      ", password='****'" +
      '}'
  }
}
