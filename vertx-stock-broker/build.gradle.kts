import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.6.10"
  application
  id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.rodrigo.quispe"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.2.5"
val junitJupiterVersion = "5.8.2"

val mainVerticleName = "com.rodrigo.quispe.vertx_stock_broker.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-web-client")
  implementation("io.vertx:vertx-lang-kotlin")
  implementation("io.vertx:vertx-config-yaml")

  // Postgres Client
  implementation("io.vertx:vertx-pg-client")
  implementation("org.postgresql:postgresql:42.3.2")

  implementation("io.vertx:vertx-sql-client-templates")
  implementation("org.flywaydb:flyway-core:8.5.0")
  implementation(kotlin("stdlib-jdk8"))
  implementation("ch.qos.logback:logback-classic:1.2.10")
  implementation("com.fasterxml.jackson.core:jackson-core:2.13.1")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.13.1")
  implementation("io.vertx:vertx-config:4.2.4")
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "11"

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf(
    "run",
    mainVerticleName,
    "--redeploy=$watchForChange",
    "--launcher-class=$launcherClassName",
    "--on-redeploy=$doOnChange"
  )
}
