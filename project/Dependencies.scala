import sbt._

object Dependencies {

  import Versions._

  lazy val betterMonadicFor = "com.olegpy" %% "better-monadic-for" % BetterMonadicForVersion

  lazy val kindProjector = "org.typelevel" %% "kind-projector" % KindProjectorVersion

  lazy val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-generic" % CirceVersion,
    "io.circe" %% "circe-generic-extras" % CirceVersion,
    "io.circe" %% "circe-parser" % CirceVersion
  )

  lazy val http4sServer: Seq[ModuleID] = Seq(
    "org.http4s" %% "http4s-ember-client" % Http4sVersion,
    "org.http4s" %% "http4s-ember-server" % Http4sVersion,
    "org.http4s" %% "http4s-dsl" % Http4sVersion,
    "org.http4s" %% "http4s-circe" % Http4sVersion
  )

  lazy val zio: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio" % ZioVersion,
    "dev.zio" %% "zio-interop-cats" % "3.1.1.0",
    "dev.zio" %% "zio-streams" % ZioVersion,
    "dev.zio" %% "zio-kafka" % "2.1.3",
    "dev.zio" %% "zio-logging-slf4j2" % "2.1.11",
    "dev.zio" %% "zio-macros" % ZioVersion,
    "dev.zio" %% "zio-test" % ZioVersion % Test,
    "dev.zio" %% "zio-test-sbt" % ZioVersion % Test,
    "dev.zio" %% "zio-test-magnolia" % ZioVersion % Test
  )

  lazy val tapir: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-zio" % TapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server-zio" % TapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-cats" % TapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % TapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % TapirVersion
  )

  lazy val logback = "ch.qos.logback" % "logback-classic" % LogbackVersion

  lazy val zioConfig: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio-config" % ZioConfigVersion,
    "dev.zio" %% "zio-config-magnolia" % ZioConfigVersion,
    "dev.zio" %% "zio-config-typesafe" % ZioConfigVersion,
    "dev.zio" %% "zio-config-refined" % ZioConfigVersion
  )

  lazy val testContainers = Seq(
    "com.dimafeng" %% "testcontainers-scala-postgresql" % TestContainersScalaVersion % Test,
    "com.dimafeng" %% "testcontainers-scala-scalatest" % TestContainersScalaVersion % Test,
    "com.dimafeng" %% "testcontainers-scala-elasticsearch" % TestContainersScalaVersion % Test,
    "com.dimafeng" %% "testcontainers-scala-mockserver" % TestContainersScalaVersion % Test
  )

  lazy val mockServerClient = "org.mock-server" % "mockserver-client-java" % MockServerClientVersion

  object Versions {

    lazy val BetterMonadicForVersion = "0.3.1"

    lazy val KindProjectorVersion = "0.10.3"

    lazy val LogbackVersion = "1.4.6"

    lazy val CirceVersion = "0.14.1"

    lazy val Http4sVersion = "0.23.18"

    lazy val ZioVersion = "2.0.10"

    lazy val TapirVersion = "1.2.10"

    lazy val TestContainersScalaVersion = "0.41.3"

    lazy val ZioConfigVersion = "4.0.0-RC12"

    lazy val MockServerClientVersion = "5.13.2"

  }

}
