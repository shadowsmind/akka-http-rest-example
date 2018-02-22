lazy val commonSettings = Defaults.coreDefaultSettings ++ Formatting.formatSettings ++ Seq(
  organization := "com.shadowsmind",
  version := "1.0.0",
  scalaVersion := Dependencies.scalaLastVersion,
  scalacOptions ++= List("-unchecked", "-deprecation", "-encoding", "UTF8", "-feature")
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    name := "example",
    libraryDependencies ++= Dependencies.core ++ Dependencies.test
  )
