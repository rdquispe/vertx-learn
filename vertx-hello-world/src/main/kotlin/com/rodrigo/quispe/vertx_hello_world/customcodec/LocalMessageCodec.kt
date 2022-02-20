package com.rodrigo.quispe.vertx_hello_world.customcodec

import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec

class LocalMessageCodec<T> : MessageCodec<T, T> {

  private var typeName: String = ""

  constructor(type: Class<T>) {
    this.typeName = type.name
  }

  override fun encodeToWire(buffer: Buffer?, s: T) {
    TODO("Not yet implemented")
  }

  override fun decodeFromWire(pos: Int, buffer: Buffer?): T {
    TODO("Not yet implemented")
  }

  override fun transform(s: T): T {
    return s
  }

  override fun name(): String {
    return this.typeName
  }

  override fun systemCodecID(): Byte {
    return -1
  }
}
