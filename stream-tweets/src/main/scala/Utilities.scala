package com.csye7200.streaming

import org.apache.spark.sql.Column
import org.apache.spark.sql.expressions.UserDefinedFunction

import java.sql.{Connection, DriverManager, ResultSet}
//import org.apache.spark
import breeze.linalg.DenseVector
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException
import org.apache.hadoop.shaded.org.eclipse.jetty.websocket.common.frames.DataFrame
import org.apache.log4j.spi.Configurator
import org.apache.spark.sql.{DataFrame, Dataset, Row, SaveMode, SparkSession}
import org.apache.log4j.{Level, LogManager, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.mllib.linalg.VectorUDT
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.StructType

import java.util.stream.Collectors
import scala.collection.IterableOnce.iterableOnceExtensionMethods
import scala.collection.mutable
import org.apache.spark.ml.linalg.{Vectors, Vector}
import org.apache.spark.sql.expressions.Window
import scala.collection.parallel.CollectionConverters.ArrayIsParallelizable

object Utilities {

 Logger.getLogger(("org")).setLevel(Level.OFF)
 Logger.getLogger("akka").setLevel(Level.OFF)

 val path = "src/main/resources/tweets.csv"
 val spark: SparkSession = SparkSession
   .builder()
   .appName("Wordle")
   .master("local[*]")
   .getOrCreate()



 import spark.implicits._
 def validateDF(row: Row): Boolean = try {
  row.getString(0).toInt
  true
 } catch {
  case ex: java.lang.NumberFormatException => {
   // Handle exception if you want
   false
  }
 }
 val seqAsVector = udf((xs: Seq[Double]) => Vectors.dense(xs.toArray))

 //----------------------------------------------------------------------------------------------------
 //wordle decode
 val newLineRegex = regexp_replace($"tweet", "\n", "")
 def spaceRegex(c: Column) = regexp_replace(c, " ", "")
 def extract(c: Column) = regexp_extract(c, "([\uD83D\uDFE8\uD83D\uDFE9\u2B1C\u2B1B]+)",0)

 def YReplace(c: Column) = regexp_replace(c, "([\uD83D\uDFE8])", "Y")
 def GReplace(c: Column) = regexp_replace(c, "([\uD83D\uDFE9])", "G")
 def BWReplace(c: Column) = regexp_replace(c, "([\u2B1C|\u2B1B])", "B")


 def fillTweet: (String => String) = {s => s.size match{
  case 30 => s
  case _ =>
   val toFillSize = 30-s.size
   s+",0"*toFillSize
 }}
 val udfFillTweet = udf(fillTweet)
 // val changed = extracted_med.withColumn("tweet", udfFillTweet(extracted_med("tweet")))

 val toDoubleSeq: (Char => Double) = {c => c match {
  case 'Y' => 0.5
  case 'G' => 1
  case 'B' => -0.3
  case '-' => 0
  case _ => -1
 }
 }
 val strReplace: (String => IndexedSeq[Double]) = {s => s.map(c => toDoubleSeq(c))}
 val udfStrReplace = udf(strReplace)
 // val change_final  = changed.withColumn("tweet", myUDF(changed("tweet")))

 //------------------------------------------------------------------------------------------------------------------------

 // Computing user wordle distances on change_final

 val euclideanDistance = udf { (v1: Vector, v2: Vector) =>
  math.sqrt(Vectors.sqdist(v1, v2))
 }

 val countCommas = udf{ (a: String) => a.count(_ == ',')}
 val vecSize = udf( (a: Vector) => a.size)

 def toDoubles: UserDefinedFunction =
  udf { s: String =>
   s.trim
     .split(",")
     .map(_.trim)
     .map(_.toDouble)
  }
 val w = Window.partitionBy("username").orderBy("distance")

 val makeVecString = udf((a: Vector) => a.toString)//a.mkString(", "))
 val makeSeqString = udf((s: IndexedSeq[Double]) => s.mkString(","))



}


