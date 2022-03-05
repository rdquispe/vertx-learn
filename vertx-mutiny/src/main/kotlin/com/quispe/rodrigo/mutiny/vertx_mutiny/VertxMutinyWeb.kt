package com.quispe.rodrigo.mutiny.vertx_mutiny

import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.mutiny.core.Vertx
import io.vertx.mutiny.ext.web.Router
import io.vertx.mutiny.ext.web.RoutingContext
import org.slf4j.LoggerFactory

class VertxMutinyWeb : AbstractVerticle() {

  val LOG = LoggerFactory.getLogger(VertxMutinyWeb::class.java)

  override fun asyncStart(): Uni<Void> {

    val router = Router.router(vertx)
    router.route().failureHandler(this::failureHandler)
    router.get("/users").respond(this::getUsers)
    return vertx.createHttpServer()
//      .requestHandler { req -> req.response().endAndForget("Hello!") }
      .requestHandler(router)
      .listen(8111)
      .replaceWithVoid()
  }

  private fun failureHandler(context: RoutingContext) {
    context.response().setStatusCode(500).endAndForget("Something wrong :(")
  }

  private fun getUsers(context: RoutingContext): Uni<JsonArray> {
    var response = JsonArray()
      .add(JsonObject().put("name", "Alice"))
      .add(JsonObject().put("name", "Bob"))

    return Uni.createFrom().item(response)
  }
}

fun main() {
  Vertx.vertx()
    .deployVerticle(VertxMutinyWeb())
    .subscribe()
    .with { id -> println("Started: $id") }
}
