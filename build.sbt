name := "notification-system"
version := "1.0.0"
scalaVersion := "2.13.8"

lazy val AkkaHttpVersion = "10.2.6"
lazy val AkkaVersion = "2.6.17"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.10"
)