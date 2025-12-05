import play.sbt.PlayImport._
import sbt.Keys._
import sbt._

lazy val root = (project in file("."))
  .enablePlugins(PlayJava, PlayNettyServer)
  .settings(
    name := "course-backend",
    organization := "com.sanketika",
    version := "1.0.0",

    scalaVersion := "2.13.12",

    libraryDependencies ++= Seq(
      guice,                 // Play's Guice DI
      ws,
      filters,               // Play WS client (we'll use for Keycloak JWKs)

      // Play JPA
      javaJpa,

      // Database / JPA
      "org.postgresql"          %  "postgresql"              % "42.7.1",
      "jakarta.persistence"     %  "jakarta.persistence-api" % "3.1.0",
      "org.hibernate.orm"       %  "hibernate-core"          % "6.4.4.Final",
      "jakarta.validation"      %  "jakarta.validation-api"  % "3.0.2",

      // Redis
      "redis.clients"           %  "jedis"                   % "5.1.0",

      // JWT
      "com.auth0"               %  "java-jwt"                % "4.4.0",
      "com.auth0"               %  "jwks-rsa"                % "0.22.1",

      // Jackson (JSON) - force version 2.14.3 for compatibility with Play's Scala module
      "com.fasterxml.jackson.core" % "jackson-databind"      % "2.14.3" force(),
      "com.fasterxml.jackson.core" % "jackson-core"          % "2.14.3" force(),
      "com.fasterxml.jackson.core" % "jackson-annotations"   % "2.14.3" force(),

      // Lombok (same as your Spring project)
      "org.projectlombok"       %  "lombok"                  % "1.18.36" % "provided",

      // Logging
      "ch.qos.logback"          %  "logback-classic"         % "1.4.14"
    ),

    // Java 17
    javacOptions ++= Seq(
      "-source", "17",
      "-target", "17",
      "-Xlint:unchecked",
      "-Xlint:deprecation"
    ),

    // Force Jackson 2.14.3 to avoid conflicts with Play's Scala module
    dependencyOverrides ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.14.3",
      "com.fasterxml.jackson.core" % "jackson-core" % "2.14.3",
      "com.fasterxml.jackson.core" % "jackson-annotations" % "2.14.3"
    ),

    // Ignore JavaDoc generation to speed up builds
    Compile / doc / sources := Seq.empty
  )
