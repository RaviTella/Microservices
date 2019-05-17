name := """eBookStore"""
organization := "ratella.sample"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.8"

libraryDependencies += guice

libraryDependencies += "org.projectlombok" % "lombok" % "1.16.16"

libraryDependencies += "net.jodah" % "failsafe" % "2.0.1"

libraryDependencies += "org.mockito" % "mockito-core" % "2.1.0" 

libraryDependencies += ws

packageName in Docker := "ravitella/ebookstore"

version in Docker := "latest"



