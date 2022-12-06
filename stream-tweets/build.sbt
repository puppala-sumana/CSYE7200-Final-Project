ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

val sparkVersion = "3.2.1"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion,
  "mysql" % "mysql-connector-java" % "5.1.46" % "provided",
  "co.theasi" %% "plotly" % "0.2.0"
)

//ThisBuild / libraryDependencies += "org.apache.spark" %% "spark-core" % "3.2.1"
//ThisBuild / libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.2.1" % "provided"

lazy val root = (project in file("."))
  .settings(
    name := "wordle-final-project"
  )
