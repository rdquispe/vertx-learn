package com.quispe.rodrigo.mutiny.vertx_mutiny

import io.smallrye.mutiny.Uni
import org.slf4j.LoggerFactory

class HelloUni {

  val LOG = LoggerFactory.getLogger(HelloUni::class.java)

  fun helloMutiny() {
    Uni.createFrom()
      .item("Hello")
      .onItem().transform { item -> "$item Mutiny!" }
      .onItem().transform(String::uppercase)
      .subscribe().with { item ->
        LOG.info("Item: {}", item)
      }
  }

  fun failedIgnored() {
    Uni.createFrom()
      .item("Ignored due failed")
      .onItem().castTo(Int::class.java)
      .subscribe().with(
        { item -> LOG.info("Item: {}", item) },
        { failure -> LOG.info("Failed: {}", failure) }
      )
  }
}

fun main() {

  val helloUni = HelloUni()

  helloUni.helloMutiny()
  helloUni.failedIgnored()
}
