package com.csye7200.streaming

import org.apache.spark.sql.SparkSession
import ParseUtils._
import org.apache.spark.sql.functions.{col, current_date, to_date}
import org.scalatest.matchers.should.Matchers
import org.scalatest.tagobjects.Slow
import org.scalatest.{BeforeAndAfter, flatspec}
import org.apache.log4j.{Level, LogManager, Logger}

import scala.util.Try

class ParseSpec extends flatspec.AnyFlatSpec with Matchers with BeforeAndAfter {

  Logger.getLogger(("org")).setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

    implicit var spark: SparkSession = _

      spark = SparkSession
        .builder()
        .appName("WordleMatch")
        .master("local[*]")
        .getOrCreate()
      spark.sparkContext.setLogLevel("ERROR")

//  import scala.io.{Codec, Source}
//  val ingester = new Ingest[Response]()
//  val codec = Codec.UTF8
//  val source = Source.fromFile("src/main/resources/response.json")


  val path = "/home/sumana/CSYE7200-Final-Project/stream-tweets/src/main/resources/result.csv"
  val df = spark.read.option("header", true)
    .option("multiLine", true)
//    .option("truncate", false)
    .option("ignoreTrailingWhiteSpace", true).csv(path)

  behavior of "Regex Transformations"

  it should "regex check to trim spaces in tweet" in{
    val x = df.select(col("tweet")).withColumn("tweet", spaceRegex(col("tweet")))
      .first()
    x.get(0).toString.size shouldBe 38
  }
  it should "regex check to extract unicode characters from tweet" in {
    val x = df.select(col("tweet")).withColumn("tweet", extract(col("tweet"))).take(5).last
    x.get(0).toString.size shouldBe 8
  }
  it should "BReplace: regex check to replace black tiles from tweet" in {
    val x = df.select(col("tweet")).withColumn("tweet", BReplace(col("tweet"))).take(5).last
    x.get(0).toString.charAt(16) shouldBe '⬜'
    x.get(0).toString.size shouldBe 44
  }
  it should "YReplace: regex check to replace yellow tiles with Y from tweet" in {
    val x = df.select(col("tweet")).withColumn("tweet", YReplace(col("tweet"))).take(3).last
    x.get(0).toString.charAt(17) shouldBe 'Y'
  }
  it should "GReplace: regex check to replace yellow tiles with Y from tweet" in {
    val x = df.select(col("tweet")).withColumn("tweet", GReplace(col("tweet"))).take(5).last
    x.get(0).toString.charAt(25) shouldBe 'G'
  }
  it should "BWReplace: regex check to replace yellow tiles with Y from tweet" in {
    val x = df.select(col("tweet")).withColumn("tweet", BWReplace(col("tweet"))).take(3).last
    println(x.get(0).toString)
    x.get(0).toString.charAt(16) shouldBe 'B'
  }

  behavior of "preProcessing"

  it should "trim spaces for a given string" in {
    val sampleString = "    ~@octobergloom Wordle 533 3/6   "
    trimStringSeq(sampleString).size shouldBe 29
  }
  it should "check String size =30" in {
    val sampleString = "    ~@octobergloom Wordle 533 3/6   "
    trimStringSeq(sampleString).size shouldBe 29
  }

  it should "fill tweet with size less than 30" in {
    val sampleString = "    ~@octobergloom Wordle 533 3/6   "
    trimStringSeq(sampleString).size shouldBe 29
  }

  it should "replace string formatted doubles to sequence of doubles" in {
    val sampleString = "    ~@octobergloom Wordle 533 3/6   "
    trimStringSeq(sampleString).size shouldBe 29
  }

  it should "convert a sequence of doubles to string" in {
    val sampleString = "    ~@octobergloom Wordle 533 3/6   "
    trimStringSeq(sampleString).size shouldBe 29
  }


}
