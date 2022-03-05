package com.quispe.rodrigo.mutiny.vertx_mutiny.db

import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.mutiny.core.Vertx
import io.vertx.mutiny.ext.web.Router
import io.vertx.mutiny.ext.web.RoutingContext
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.Pool
import io.vertx.pgclient.PgConnectOptions
import io.vertx.sqlclient.PoolOptions
import org.slf4j.LoggerFactory
import java.time.LocalTime


class VertxMutinyReactiveSQL : AbstractVerticle() {

  val LOG = LoggerFactory.getLogger(VertxMutinyReactiveSQL::class.java)

  override fun asyncStart(): Uni<Void> {
    vertx.periodicStream(5000L).toMulti()
      .subscribe().with { item -> LOG.info("{}", LocalTime.now().minute) }

    val db = createPgPool(config())

    val router = Router.router(vertx)
    router.route().failureHandler(this::failureHandler)
    router.get("/users").respond { context -> executeQuery(db) }
    return vertx.createHttpServer()
      .requestHandler(router)
      .listen(8111)
      .replaceWithVoid()
  }

  private fun executeQuery(db: Pool): Uni<JsonArray> {
    LOG.info("Executing DB query to find all users...")
    return db.query("SELECT * FROM users ")
      .execute()
      .onItem()
      .transform { rows ->
        var data = JsonArray()
        rows.forEach { data.add(it.toJson()) }
        LOG.info("Return data: {}", data)
        data
      }.onFailure()
      .invoke { failure -> LOG.info("Failed query: ", failure) }
      .onFailure()
      .recoverWithItem(JsonArray())
  }

  private fun createPgPool(config: JsonObject): Pool {
    val connect = PgConnectOptions()
      .setHost("localhost")
      .setPort(config.getInteger("port"))
      .setDatabase(EmbeddedPostgres.DATABASE_NAME)
      .setUser(EmbeddedPostgres.USERNAME)
      .setPassword(EmbeddedPostgres.PASSWORD)

    val options = PoolOptions().setMaxSize(5)

    return PgPool.pool(vertx, connect, options)
  }

  private fun failureHandler(context: RoutingContext) {
    context.response().setStatusCode(500).endAndForget("Something wrong :(")
  }
}

fun main() {
  val LOG = LoggerFactory.getLogger(VertxMutinyReactiveSQL::class.java)

  val port = EmbeddedPostgres.startPostgres()
  val options = DeploymentOptions()
    .setConfig(JsonObject().put("port", port))

  Vertx.vertx()
    .deployVerticle(VertxMutinyReactiveSQL(), options)
    .subscribe()
    .with { id -> LOG.info("Started: $id") }
}
