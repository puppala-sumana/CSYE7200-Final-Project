//
//package com.csye7200.streaming
//
//import java.sql.DriverManager
//import java.sql.Connection
////import org.apache.spark
//import breeze.linalg.DenseVector
//import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException
//import org.apache.hadoop.shaded.org.eclipse.jetty.websocket.common.frames.DataFrame
//import org.apache.log4j.spi.Configurator
//import org.apache.spark.sql.{DataFrame, Dataset, Row, SaveMode, SparkSession}
//import org.apache.log4j.{Level, LogManager, Logger}
//import org.apache.spark.{SparkConf, SparkContext}
//import org.apache.spark.ml.feature.VectorAssembler
//import org.apache.spark.mllib.linalg.VectorUDT
//import org.apache.spark.sql.functions._
//import org.apache.spark.sql.types.StructType
//
//import java.util.stream.Collectors
//import scala.collection.IterableOnce.iterableOnceExtensionMethods
//import scala.collection.mutable
//import org.apache.spark.ml.linalg.{Vectors, Vector}
//import org.apache.spark.sql.expressions.Window
//import scala.collection.parallel.CollectionConverters.ArrayIsParallelizable
//
//object Parse {
//
// Logger.getLogger(("org")).setLevel(Level.OFF)
// Logger.getLogger("akka").setLevel(Level.OFF)
//
// val path = "src/main/resources/tweets.csv"
// val spark: SparkSession = SparkSession
//   .builder()
//   .appName("Wordle")
//   .master("local[*]")
//   .getOrCreate()
//
// val df = spark.read.option("header", true)
//   .option("multiLine", true)
//   .option("ignoreLeadingWhiteSpace", true)
//   .option("ignoreTrailingWhiteSpace", true).csv(path)
//
//
// val wordleDsitinctCount: Int = df.select("wordle_id").distinct().count().toInt
// println(df.select("wordle_id").distinct().count())
// println(df.select("tweet_username").count())
// println(df.select("tweet_username").distinct().count())
//println("------------------------------")
//// val newDf = df.rdd.repartition(wordleDsitinctCount)
//// println(newDf.getNumPartitions)
//
// val small_df = df//.filter(df("wordle_id").isin(210,211,212,213))
// println(small_df.count())
//
// import spark.implicits._
// def validateDF(row: Row): Boolean = try {
//  row.getString(0).toInt
//  true
// } catch {
//  case ex: java.lang.NumberFormatException => {
//   // Handle exception if you want
//   false
//  }
// }
// val newDf = small_df.filter(validateDF(_)).select("wordle_id","tweet_username","tweet_text")
// val seqAsVector = udf((xs: Seq[Double]) => Vectors.dense(xs.toArray))
// val wordles =  small_df.filter(validateDF(_)).select("wordle_id").distinct().collect()
//
////----------------------------------------------------------------------------------------------------
// //wordle decode
// val newLineRegex = regexp_replace($"tweet_text", "\n", "")
// val changed_med = newDf.withColumn("tweet_text", newLineRegex)
// val extract = regexp_extract($"tweet_text", "([\uD83D\uDFE8\uD83D\uDFE9\u2B1C\u2B1B]+)", 0)
// val YReplace = regexp_replace($"tweet_text", "([\uD83D\uDFE8])", "Y")
// val GReplace = regexp_replace($"tweet_text", "([\uD83D\uDFE9])", "G")
// val BWReplace = regexp_replace($"tweet_text", "([\u2B1C|\u2B1B])", "B")
//
// val extracted = changed_med.withColumn("tweet_text", extract)
// val extracted_med = extracted.withColumn("tweet_text", YReplace).withColumn("tweet_text", GReplace).withColumn("tweet_text", BWReplace)
//
// def fillTweet: (String => String) = {s => s.size match{
//  case 30 => s
//  case _ =>
//     val toFillSize = 30-s.size
//     s+",0"*toFillSize
// }}


// val udfFillTweet = udf(fillTweet)
// val changed = extracted_med.withColumn("tweet_text", udfFillTweet(extracted_med("tweet_text")))
//
// val toDoubleSeq: (Char => Double) = {c => c match {
//  case 'Y' => 0.5
//  case 'G' => 1
//  case 'B' => -0.3
//  case '-' => 0
//  case _ => -1
// }
//}
// val strReplace: (String => IndexedSeq[Double]) = {s => s.map(c => toDoubleSeq(c))}
// val myUDF = udf(strReplace)
// val change_final  = changed.withColumn("tweet_text", myUDF(changed("tweet_text")))
//
//
// println(change_final.columns.toList)
//
//
//// change_final.foreach((r: Row) => {
////  println(r)
//// })
//
// //------------------------------------------------------------------------------------------------------------------------
//
// // Computing user wordle distances on change_final
////val forDistance = change_final//change_final.select("tweet_username","tweet_text")
//// val sc = spark.sparkContext
//// val distanceCartesian = forDistance.crossJoin(forDistance.withColumnRenamed("tweet_username", "tweet_username_cmp").withColumnRenamed("tweet_text", "tweet_text_cmp"))
//
//// val out = distanceCartesian.select(col("tweet_username"),col("tweet_username_cmp"),seqAsVector(col("tweet_text")).as("tweet_text_v"), seqAsVector(col("tweet_text_cmp")).as("tweet_text_cmp_v"))
//// out.printSchema()
//val euclideanDistance = udf { (v1: Vector, v2: Vector) =>
// math.sqrt(Vectors.sqdist(v1, v2))
//}
// val w = Window.partitionBy("tweet_username","wordle_id","wordle_id_cmp").orderBy("distance")
//// val x = change_final.select("wordle_id","tweet_username")
//// x.write.mode(SaveMode.Overwrite).format("csv").saveAsTable { val w = 210.toString(); s"$w"}
////   //parquet("/src/resources/output/change_final.parquet")
//val mkString = udf((a: Vector) => a.toString)//a.mkString(", "))
//
//// println(change_final.filter(change_final("wordle_id") === 210).select(col("tweet_username")).count())
//// change_final.filter(change_final("wordle_id") === 210).select(col("tweet_username")).orderBy("tweet_username").foreach(println)
//
//
// wordles.par.foreach {
//  wordle => {
//   val localLogger: Logger = Logger.getLogger("PartitionLogger")
//   localLogger.info("INFO: pass")};
//   change_final.filter(change_final("wordle_id") === wordle(0))
//     .crossJoin(change_final.filter(change_final("wordle_id") === wordle(0)).withColumnRenamed("wordle_id", "wordle_id_cmp").withColumnRenamed("tweet_username", "tweet_username_cmp").withColumnRenamed("tweet_text", "tweet_text_cmp"))
//     .filter(col("tweet_username") =!= col("tweet_username_cmp"))
//     .select(col("wordle_id"), col("wordle_id_cmp"), col("tweet_username"), col("tweet_username_cmp"), seqAsVector(col("tweet_text")).as("tweet_text_v"), seqAsVector(col("tweet_text_cmp")).as("tweet_text_cmp_v"))
//     .withColumn("distance", euclideanDistance.apply($"tweet_text_v", $"tweet_text_cmp_v"))
//     .withColumn("rn", row_number.over(w)).orderBy(col("tweet_username"),col("distance"))
//     .filter(col("rn") <= 10).drop(col("rn"))
//     .withColumn("tweet_text_v", mkString(col("tweet_text_v"))).withColumn("tweet_text_cmp_v", mkString(col("tweet_text_cmp_v")))
////     .printSchema()
//     .select(col("wordle_id"),col("tweet_username"),col("tweet_username_cmp"),col("tweet_text_v"),col("tweet_text_cmp_v"),col("distance"))
////     .take(5).foreach(println)
//     .withColumnRenamed("tweet_text_v", "tweet_text").withColumnRenamed("tweet_text_cmp_v", "tweet_text_cmp").withColumnRenamed("distance","euclidean_distance")
//     .write.partitionBy("wordle_id").mode("append").format("csv").saveAsTable("distance_calculated")
////     .write.mode("append").format("jdbc").option("url","jdbc:mysql://127.0.0.1:3306/dbo?useSSL=false").option("driver", "com.mysql.jdbc.Driver").option("dbtable", "wordle_distancee_matrix").option("user", "root").option("password", "Malaysia123#").save()
//     //saveAsTable("distance_calculated")
////     .write.partitionBy("wordle_id").csv("/src/resources/output")
// }
//
//
//
////  out.withColumn("distance", euclideanDistance.apply($"tweet_text_v", $"tweet_text_cmp_v")).show()
//
//
//
// //Connection to SQlite and storing data
//
//}
//
//
