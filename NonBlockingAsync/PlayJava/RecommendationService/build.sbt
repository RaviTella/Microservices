name := """RecommendationAPI"""
organization := "ratella.samples"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.8"

libraryDependencies += guice

libraryDependencies += "org.projectlombok" % "lombok" % "1.16.16"

libraryDependencies += "io.reactivex" % "rxjava" % "1.0.2"

packageName in Docker := "ravitella/recommendations" 

version in Docker := "latest"
