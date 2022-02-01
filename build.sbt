name := "scala-http4s-middleware-workshop"

version := "0.1"

scalaVersion := "2.13.8"

val http4s = "0.23.1"
val http4sJwtAuth = "1.0.0"
val circe = "0.14.1"
def http4s(artifact: String): ModuleID = "org.http4s" %% s"http4s-$artifact" % http4s
def circe(artifact: String): ModuleID = "io.circe" %% s"circe-$artifact" % circe

lazy val root = (project in file("."))
  .settings(
    name := "minimal",
    libraryDependencies ++= Seq(
      compilerPlugin(
        "org.typelevel" %% "kind-projector" % "0.13.2"
          cross CrossVersion.full
      ),
      "org.typelevel" %% "cats-core" % "2.6.1",
      "org.typelevel" %% "cats-effect" % "3.1.1",
      "org.typelevel" %% "cats-mtl" % "1.2.1",
      "co.fs2" %% "fs2-core" % "3.0.3",
      "dev.optics" %% "monocle-core" % "3.0.0",
      "dev.optics" %% "monocle-macro" % "3.0.0",
      "io.estatico" %% "newtype" % "0.4.4",
      "eu.timepit" %% "refined" % "0.9.25",
      "eu.timepit" %% "refined-cats" % "0.9.25",
      "tf.tofu" %% "derevo-cats" % "0.12.5",
      "tf.tofu" %% "derevo-cats-tagless" % "0.12.5",
      "tf.tofu" %% "derevo-circe-magnolia" % "0.12.5",
      "tf.tofu" %% "tofu-core-higher-kind" % "0.10.2",
      "org.typelevel" %% "log4cats-slf4j" % "2.2.0",
      "ch.qos.logback" % "logback-classic" % "1.2.10",
      "dev.profunktor" %% "http4s-jwt-auth" % "1.0.0",
      http4s("dsl"),
      http4s("ember-server"),
      http4s("ember-client"),
      http4s("circe"),
      circe("core"),
      circe("generic"),
      circe("parser"),
      circe("refined")
    ),
    scalacOptions ++= Seq(
      "-Ymacro-annotations", "-Wconf:cat=unused:info"
    )
  )
