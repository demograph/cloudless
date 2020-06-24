import DependenciesConf._

lazy val root = project.in(file("."))
  .settings(
    inThisBuild(List(
      organization := "io.demograph",
      scalaVersion := "2.11.12",
      version := "0.1.0-SNAPSHOT"
    )),
    name := "cloudless")
  .settings(GenericConf.settings())
  .settings(DependenciesConf.common)
  .settings(libraryDependencies += refined)
  .settings(libraryDependencies += reactor)
  .settings(libraryDependencies += akkaTestKit)
  .settings(LicenseConf.settings)
  .settings(addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(TutConf.settings)
  .enablePlugins(TutPlugin)
  .dependsOn(remoting)
  .aggregate(remoting)

lazy val remoting = project.in(file("remoting"))
  .settings(
    inThisBuild(List(
      organization := "io.demograph",
      scalaVersion := "2.11.12",
      version := "0.1.0-SNAPSHOT"
    )),
    name := "cloudless-remoting")
  .settings(GenericConf.settings())
  .settings(DependenciesConf.common)
  .settings(libraryDependencies += reactor)
  .settings(libraryDependencies ++= scalaPB)
  .settings(PB.targets in Compile := Seq(scalapb.gen() -> (sourceManaged in Compile).value))
  .settings(LicenseConf.settings)
  .enablePlugins(AutomateHeaderPlugin)
  .settings(TutConf.settings)
  .enablePlugins(TutPlugin)