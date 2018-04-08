import sbt.Keys._
import sbt._

object GenericConf {

  def javaVersionSettings(version: String): Seq[Def.Setting[Task[Seq[String]]]] = Seq(
    javacOptions ++= Seq("-source", version, "-target", version),
    scalacOptions += s"-target:jvm-$version"
  )

  def settings(javaVersion: String = "1.8"): Seq[Setting[_]] = Seq(
    scalacOptions ++= Seq("-feature", "-language:higherKinds", "-language:implicitConversions", "-deprecation", "-Ydelambdafy:method", "-Ypartial-unification"),
    javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked", "-Xlink:-warn-missing-interpolator", "-g:vars"),
    cancelable in Global := true
//    parallelExecution in Test := false,
//    fork in Test := true
  ) ++ javaVersionSettings(javaVersion)
}