import sbt.{ModuleID, _}
import sbt.Keys._

object DependenciesConf {

  /* === Aggregated dependency configurations === */
  lazy val scala: Seq[Setting[_]] = Seq(
    scalaVersion := "2.11.12",
    resolvers ++= Seq(
      Resolver.jcenterRepo,
      Resolver.sonatypeRepo("releases")
    )
  )

  lazy val common: Seq[Setting[_]] = scala ++ Seq(
    libraryDependencies ++= Seq(logback, log4s, pureconfig, scalatest, scalacheck),
    libraryDependencies ++= cats
  )

  lazy val akka: Seq[Setting[_]] = scala ++ Seq(
    libraryDependencies ++= akkaActor ++ akkaStreams ++ akkaHttp ++ akkaCluster
  )

  /* === Logging === */
  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.3.0-alpha4"

  /* === Various Scala utility libraries === */
  lazy val betterfiles = "com.github.pathikrit" %% "better-files" % "3.4.0"
  lazy val enumeratum = "com.beachape" %% "enumeratum" % "1.5.13"
  lazy val log4s = "org.log4s" %% "log4s" % "1.6.1"
  lazy val nscalaTime = "com.github.nscala-time" %% "nscala-time" % "2.18.0"
  lazy val scalaz = "org.scalaz" %% "scalaz-core" % "7.2.18"


  /* === Typesafe / Lightbend dependencies === */
  val akkaVersion = "2.5.11"
  val akkaHttpVersion = "10.1.1"

  lazy val akkaActor: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion
  )

  lazy val akkaStreams: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion
  )

  lazy val akkaHttp: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion
  )

  lazy val akkaCluster: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
    "com.typesafe.akka" %% "akka-distributed-data" % akkaVersion,
    "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion
  )

  /* === Typelevel dependencies === */
  lazy val algebra: Seq[ModuleID] = {
    val algebraVersion = "1.0.0"
    Seq(
      "org.typelevel" %% "algebra" % algebraVersion,
      "org.typelevel" %% "algebra-laws" % algebraVersion % Test
    )
  }

  lazy val cats: Seq[ModuleID] = {
    // Resolution via Maven central is stuck at 0.9.0 as Typelevel only publishes to Sonatype
    lazy val catsVersion = "1.0.1"
    Seq(
      "org.typelevel" %% "cats-core" % catsVersion,
      "org.typelevel" %% "alleycats-core" % catsVersion,
      "org.typelevel" %% "cats-testkit" % catsVersion % Test,
      "org.typelevel" %% "cats-laws" % catsVersion % Test
    )
  }

  lazy val catseffect: Seq[ModuleID] = {
    val catsEffectVersion = "0.10-5b8214f"
    Seq(
      "org.typelevel" %% "cats-effect" % catsEffectVersion,
      "org.typelevel" %% "cats-effect-laws" % catsEffectVersion % Test
    )
  }

  lazy val discipline = "org.typelevel" %% "discipline" % "0.9.0"

  lazy val monix = "io.monix" %% "monix" % "3.0.0-8084549"

  lazy val monocle: Seq[ModuleID] = {
    lazy val monocleVersion = "1.5.1-cats"
    Seq(
      "com.github.julien-truffaut" %% "monocle-core" % monocleVersion,
      "com.github.julien-truffaut" %% "monocle-macro" % monocleVersion,
      "com.github.julien-truffaut" %% "monocle-law" % monocleVersion % Test
    )
  }

  lazy val pureconfig = "com.github.pureconfig" %% "pureconfig" % "0.9.1"

  lazy val reactor = "io.reactors" %% "reactors" % "0.8"

  lazy val refined = "eu.timepit" %% "refined" % "0.8.7"

  lazy val shapeless = "com.chuusai" %% "shapeless" % "2.3.3"

  lazy val simulacrum = "com.github.mpilquist" %% "simulacrum" % "0.11.0"

  lazy val spire: Seq[ModuleID] = {
    val spireVersion = "0.15.0"
    Seq(
      "org.typelevel" %% "spire" % spireVersion,
      "org.typelevel" %% "spire-extras" % spireVersion,
      "org.typelevel" %% "spire-laws" % spireVersion % Test
    )
  }

  lazy val squants = "org.typelevel"  %% "squants"  % "1.3.0"

  /* === Scala Testing Frameworks === */
  lazy val scalatest = "org.scalatest" %% "scalatest" % "3.2.0-SNAP10" % Test
  lazy val scalacheck = "org.scalacheck" %% "scalacheck" % "1.13.5" % Test
  lazy val scalamock = "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % Test
  lazy val scalameter = "com.storm-enroute" %% "scalameter" % "0.9" % Test

  /* === Interop === */
  lazy val pureconfigEnumeratum = "com.github.pureconfig" %% "pureconfig-enumeratum" % "0.9.1"
  lazy val refinedPureconfig = "eu.timepit" %% "refined-pureconfig" % "0.8.7"
  lazy val refinedScalacheck = "eu.timepit" %% "refined-scalacheck" % "0.8.7" % Test
}