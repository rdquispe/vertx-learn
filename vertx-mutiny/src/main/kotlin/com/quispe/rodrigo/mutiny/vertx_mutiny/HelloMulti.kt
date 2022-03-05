package com.quispe.rodrigo.mutiny.vertx_mutiny

import io.smallrye.mutiny.Multi
import org.slf4j.LoggerFactory
import java.lang.String.valueOf
import java.util.stream.IntStream

class HelloMulti {

  private val LOG = LoggerFactory.getLogger(HelloMulti::class.java)

  fun multiIntStream() {
    Multi.createFrom()
      .items(IntStream.rangeClosed(1, 10).boxed())
      .onItem().transform { value -> value * 2 }
      .onItem().transform(::valueOf)
      .select().last(4)
      .subscribe().with(
        LOG::info
      ) { failure -> LOG.info("Failed: {}", failure) }
  }
}


fun main() {

  val multi = HelloMulti()
  multi.multiIntStream()
}
