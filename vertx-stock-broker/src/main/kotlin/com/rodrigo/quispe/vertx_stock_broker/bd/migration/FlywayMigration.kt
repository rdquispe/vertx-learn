package com.rodrigo.quispe.vertx_stock_broker.bd.migration

import com.rodrigo.quispe.vertx_stock_broker.config.DbConfig
import io.vertx.core.Future
import io.vertx.core.Vertx
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationInfo
import org.slf4j.LoggerFactory
import java.util.Arrays
import java.util.Objects
import java.util.Optional
import java.util.stream.Collectors

class FlywayMigration {

  companion object {

    private val logger = LoggerFactory.getLogger(FlywayMigration::class.java)

    fun migrate(vertx: Vertx, dbConfig: DbConfig): Future<Void> {
      return vertx.executeBlocking<Void> { promise ->
        // Flyway migration is blocking => use JDBC
        excute(dbConfig)
        promise.complete()
      }.onFailure { error -> logger.error("FAILED_MIGRATION: {}", error) }
    }

    private fun excute(dbConfig: DbConfig) {
      val database = "postgresql"
      val jdbcUrl = "jdbc:$database://${dbConfig.host}:${dbConfig.port}/${dbConfig.database}"

      logger.info("MIGRATING_DB: {}", jdbcUrl)

      val flyway = Flyway.configure()
        .dataSource(jdbcUrl, dbConfig.user, dbConfig.password)
        .schemas("broker")
        .defaultSchema("broker")
        .load()

      val current = Optional.ofNullable(flyway.info().current())
      current.ifPresent { migrationInfo -> logger.info(migrationInfo.version.version) }

      val pendingMigrations = flyway.info().pending()

      logger.info("PENDING_MIGRATIONS: {}", printMigrations(pendingMigrations))

      flyway.migrate()
    }

    private fun printMigrations(pending: Array<MigrationInfo>): String {
      return if (Objects.isNull(pending)) {
        "[]"
      } else {
        Arrays.stream(pending)
          .map { each -> "${each.version} - ${each.description}" }
          .collect(Collectors.joining(",", "[", "]"))
      }
    }
  }
}
