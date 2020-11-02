organization := "com.github.losizm"
name := "little-cli"
version := "0.8.0"

description := "The Scala library that provides extension methods to Apache Commons CLI"
homepage := Some(url("https://github.com/losizm/little-cli"))
licenses := List("Apache License, Version 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

scalaVersion := "2.13.3"
scalacOptions ++= Seq("-deprecation", "-feature", "-Xcheckinit")

Compile / doc / scalacOptions ++= Seq(
  "-doc-title"  , name.value,
  "-doc-version", version.value
)

crossScalaVersions := Seq("2.12.12")

unmanagedSourceDirectories in Compile += {
  (sourceDirectory in Compile).value / s"scala-${scalaBinaryVersion.value}"
}

libraryDependencies ++= Seq(
  "commons-cli"   %  "commons-cli" % "1.4"   % "provided",
  "org.scalatest" %% "scalatest"   % "3.2.2" % "test"
)

developers := List(
  Developer(
    id    = "losizm",
    name  = "Carlos Conyers",
    email = "carlos.conyers@hotmail.com",
    url   = url("https://github.com/losizm")
  )
)

scmInfo := Some(
  ScmInfo(
    url("https://github.com/losizm/little-cli"),
    "scm:git@github.com:losizm/little-cli.git"
  )
)

publishMavenStyle := true

pomIncludeRepository := { _ => false }

publishTo := {
  val nexus = "https://oss.sonatype.org"
  isSnapshot.value match {
    case true  => Some("snaphsots" at s"$nexus/content/repositories/snapshots")
    case false => Some("releases" at s"$nexus/service/local/staging/deploy/maven2")
  }
}

