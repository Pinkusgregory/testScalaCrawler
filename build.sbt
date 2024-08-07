lazy val scalaReflect = Def.setting { "org.scala-lang" % "scala-reflect" % scalaVersion.value }


lazy val root = (project in file("."))
  .settings(
    scalaVersion := "2.13.10",
    organization := "chernyaev.ru",
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    name := "test-crawler-core",
    libraryDependencies ++= Dependencies.http4sServer,
    libraryDependencies ++= Dependencies.circe,
    libraryDependencies ++= Dependencies.zio,
    libraryDependencies ++= Dependencies.tapir,
    libraryDependencies ++= Dependencies.zioConfig,
    libraryDependencies ++= Dependencies.testContainers,
    libraryDependencies ++= Seq(
      Dependencies.kindProjector,
      Dependencies.logback,
      Dependencies.mockServerClient
    ),
    addCompilerPlugin(Dependencies.kindProjector),
    addCompilerPlugin(Dependencies.betterMonadicFor),
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-language:higherKinds",
      "-language:postfixOps",
      "-feature",
      "-Xfatal-warnings"
    )
  ).enablePlugins(JavaAppPackaging)

Test / fork := true
Test / parallelExecution := false
