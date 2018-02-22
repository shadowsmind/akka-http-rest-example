import sbt._

object Dependencies {

  val scalaLastVersion      = "2.12.4"

  val akkaVersion           = "2.4.20"
  val akkaHttpVersion       = "10.0.11"
  val macWireVersion        = "2.3.0"
  val typesafeConfigVersion = "1.3.3"
  val pureConfigVersion     = "0.9.0"
  val hikariCPVersion       = "2.7.7"
  val postgresDriverVersion = "42.2.1"
  val flyWayVersion         = "5.0.7"
  val slickVersion          = "3.2.1"
  val slickPgVersion        = "0.15.7"
  val catsVersion           = "1.0.1"
  val bCryptScalaVersion    = "3.1"
  val jwtScalaVersion       = "0.14.1"

  val scalaTestVersion      = "3.0.5"
  val scalacheckVersion     = "1.13.5"
  val scalaMeterVersion     = "0.9"
  val scalaMockVersion      = "3.6.0"

  val core = Seq(
    "com.typesafe.akka"        %% "akka-actor"           % akkaVersion,
    "com.typesafe.akka"        %% "akka-stream"          % akkaVersion,
    "com.typesafe.akka"        %% "akka-http"            % akkaHttpVersion,
    "com.typesafe.akka"        %% "akka-http-spray-json" % akkaHttpVersion,
    "com.softwaremill.macwire" %% "macros"               % macWireVersion % "provided",
    "com.typesafe"             %  "config"               % typesafeConfigVersion,
    "com.github.pureconfig"    %% "pureconfig"           % pureConfigVersion,
    "org.typelevel"            %% "cats-core"            % catsVersion,
    "com.zaxxer"               %  "HikariCP"             % hikariCPVersion,
    "org.postgresql"           %  "postgresql"           % postgresDriverVersion,
    "org.flywaydb"             %  "flyway-core"          % flyWayVersion,
    "com.typesafe.slick"       %% "slick"                % slickVersion,
    "com.typesafe.slick"       %% "slick-hikaricp"       % slickVersion,
    "com.github.tminglei"      %% "slick-pg"             % slickPgVersion,
    "com.github.tminglei"      %% "slick-pg_spray-json"  % slickPgVersion,
    "com.github.tminglei"      %% "slick-pg_jts"         % slickPgVersion,
    "com.github.t3hnar"        %% "scala-bcrypt"         % bCryptScalaVersion,
    "com.pauldijou"            %% "jwt-core"             % jwtScalaVersion
  )

  val test = Seq(
    "org.scalatest"       %% "scalatest"                   % scalaTestVersion,
    "org.scalacheck"      %% "scalacheck"                  % scalacheckVersion,
    "com.storm-enroute"   %% "scalameter"                  % scalaMeterVersion,
    "org.scalactic"       %% "scalactic"                   % scalaTestVersion,
    "org.scalamock"       %% "scalamock-scalatest-support" % scalaMockVersion,
    "com.typesafe.akka"   %% "akka-testkit"                % akkaVersion,
    "com.typesafe.akka"   %% "akka-stream-testkit"         % akkaVersion,
    "com.typesafe.akka"   %% "akka-http-testkit"           % akkaHttpVersion,
    "com.typesafe.slick"  %% "slick-testkit"               % slickVersion
  ).map(_ % Test)

}
