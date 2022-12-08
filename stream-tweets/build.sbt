version := "0.1"


val sparkVersion = "3.2.1"
val scalaTestVersion = "3.2.3"


libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion,
  "mysql" % "mysql-connector-java" % "5.1.46" % "provided",
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
  "io.spray" %% "spray-json" % "1.3.2"
)

