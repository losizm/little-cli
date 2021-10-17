organization := "com.github.losizm"
name := "little-cli"
version := "2.0.0"

description := "The Scala library that provides extension methods to Apache Commons CLI"
homepage := Some(url("https://github.com/losizm/little-cli"))
licenses := List("Apache License, Version 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

scalaVersion := "3.0.2"
scalacOptions ++= Seq("-deprecation", "-feature", "-new-syntax", "-Xfatal-warnings", "-Yno-experimental")

Compile / doc / scalacOptions ++= Seq(
  "-project-version", {
  val ver = version.value
  ver.substring(0, ver.lastIndexOf(".")) ++ ".x"
})

libraryDependencies ++= Seq(
  "commons-cli"   %  "commons-cli" % "1.4"   % "provided",
  "org.scalatest" %% "scalatest"   % "3.2.9" % "test"
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
