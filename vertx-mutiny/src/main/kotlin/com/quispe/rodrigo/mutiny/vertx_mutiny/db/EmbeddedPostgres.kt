package com.quispe.rodrigo.mutiny.vertx_mutiny.db

import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

class EmbeddedPostgres {

  companion object {

    const val DATABASE_NAME = "users"
    const val USERNAME = "postgres"
    const val PASSWORD = "secret"

    fun startPostgres(): Int {
      // Using test containers to spin up a Postgres DB
      val pg = PostgreSQLContainer(DockerImageName.parse("postgres:13.5-alpine"))
        .withDatabaseName(DATABASE_NAME)
        .withUsername(USERNAME)
        .withPassword(PASSWORD)
        .withInitScript("db/setup.sql")
      pg.start()
      return pg.firstMappedPort
    }
  }
}
