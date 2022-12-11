package com.csye7200.streaming

import org.apache.spark.sql.functions._
import ParseUtils._
import com.csye7200.streaming.DBProperties._
import org.apache.spark.sql.types.{ArrayType, MapType, StringType, StructType}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.log4j.{Level, LogManager, Logger}
import org.apache.spark.sql.catalyst.dsl.expressions.StringToAttributeConversionHelper
import org.apache.spark.sql.expressions.Window

object StreamTweets extends App{

  Logger.getLogger(("org")).setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  val spark: SparkSession = SparkSession.builder()
      .master("local[*]")
      .appName("SparkStreamWordles")
      .getOrCreate()

    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers",  "127.0.0.1:9092")
      .option("subscribe", "messages")
      .load()

  val tweetSchema= new StructType().add("tweet",StringType)
  val internalSchema = new StructType().add("created_at", StringType).add("date", StringType).add("timezone", StringType).add("username", StringType).add("tweet", StringType)

//Data cleaning lambdas

   val tweetStripped =  df.selectExpr("cast (value as string)").select(from_json(col("value"),tweetSchema).as("tweet")).select(col("tweet.*"))
   val tweets = tweetStripped.selectExpr("cast (tweet as string)")
         .select(from_json(col("tweet"), internalSchema)
         .as("final")).select("final.*")
         .withColumn("tweetClean",spaceRegex(col("tweet")))
         .withColumn("tweetClean",extract(col("tweetClean")))
        .withColumn("tweetClean", BReplace(col("tweetClean")))
         .filter(col("tweetClean") =!= "")
         .withColumn("tweet",YReplace(col("tweet")))
         .withColumn("tweet",GReplace(col("tweet")))
         .withColumn("tweet",BWReplace(col("tweet")))
         .withColumn("tweet", udftrimStringSeq(col("tweet")))
         .filter(udfcheckStringSeqSize(col("tweet")))
         .withColumn("tweet", udfFillTweet(col("tweet")))
         .withColumn("tweet",udfStrReplace(col("tweet")))
        .withColumn("tweet", makeSeqString(col("tweet")))
     .dropDuplicates()

/*     .withColumn("currentDate", current_date())
     .withColumn("wordle_id", row_number().over(Window.partitionBy(col("currentDate")).orderBy(col("currentDate"))))//when (to_date(col("date")) === current_date(), )
     */

/*tweets.writeStream
    .format("console")
    .outputMode("append")
    .option("truncate","False")
    .start()
    .awaitTermination()*/

  tweets.writeStream.foreachBatch { (batchDF: DataFrame, batchId: Long) =>
    println("foreachBatch")
    batchDF.persist()
    batchDF.write.mode("append").format("jdbc").jdbc(jdbcurl, tweetsTable, props)
    }.start().awaitTermination()

  /*TODO: Add columns: Current Date/ timestamp, Batch Id/ auto-generate wordle id based on date
  *  Check stream consistency, all the data getting inserted properly from kafka stream?, check all parameters: offsets
  *  Read from Mysql: data cleaning/parsing/implicits/exception handling/ unit tests/
  *  After reading/cleaning: Compute distances/ code for figuring out best distance/ mllib: create clusters for each day/wordle/ store in db
  */

  }