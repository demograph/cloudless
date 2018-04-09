lazy val root = project.in(file("."))
  .settings(
    inThisBuild(List(
      organization := "io.demograph",
      scalaVersion := "2.11.12",
      version := "0.1.0-SNAPSHOT"
    )),
    name := "overlay")
  .settings(GenericConf.settings())
  .settings(DependenciesConf.common)
  .settings(libraryDependencies += DependenciesConf.refined)
  .settings(libraryDependencies += DependenciesConf.reactor)
  .settings(libraryDependencies += DependenciesConf.akkaTestKit)
  .settings(LicenseConf.settings)
  .settings(addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(TutConf.settings)
  .enablePlugins(TutPlugin)