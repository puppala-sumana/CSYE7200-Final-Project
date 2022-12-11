
import org.apache.spark.sql.{SparkSession, functions}
import com.csye7200.streaming.DBProperties._

import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.clustering.BisectingKMeans
import org.apache.spark.ml.evaluation.ClusteringEvaluator
import org.apache.spark.sql.functions.{col, current_date, row_number, to_date}
import com.csye7200.streaming.ParseUtils._



object ProcessTweets extends App{

  /*TODO:
   *  Read from Mysql: data cleaning/parsing/implicits/exception handling/ unit tests/
   *  After reading/cleaning: Compute distances/ code for figuring out best distance/ mllib: create clusters for each day/wordle/ store in db
   */

  Logger.getLogger(("org")).setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  val spark: SparkSession = SparkSession.builder()
    .master("local[*]")
    .appName("SparkStreamWordles")
    .getOrCreate()

  val wordleUsers = spark.read
    .format("jdbc")
    .option("url", jdbcurl)
    .option("dbtable", tweetsTable)
    .option("user", props.getProperty("user"))
    .option("password", props.getProperty("password"))
    .load()
    .filter(to_date(col("date")) equalTo current_date())


val wordleUserDistances=
    wordleUsers
    .dropDuplicates()
    .select(col("username"),col("tweet"),col("tweetClean"))
    .crossJoin(wordleUsers.withColumnRenamed("username","username_cmp").withColumnRenamed("tweet","tweet_cmp").withColumnRenamed("tweetClean","tweetClean_cmp"))
    .filter(col("username") =!= col("username_cmp"))
    .withColumn("tweetVector", seqAsVector(toDoubles(col("tweet")))).withColumn("tweetVector_cmp", seqAsVector(toDoubles(col("tweet_cmp"))))
    .filter(vecSize(col("tweetVector")) <= 30).filter(vecSize(col("tweetVector_cmp")) <= 30)
    .withColumn("distance", euclideanDistance.apply(col("tweetVector"), col("tweetVector_cmp")))
    .withColumn("rn", row_number.over(w)).orderBy(col("username"), col("distance"))
    .filter(col("rn") <= 10).drop(col("rn"))
    .select(col("date"),col("username"), col("username_cmp"), col("tweetClean"), col("tweetClean_cmp"), col("distance"))
//      .show(50)

  wordleUserDistances
    .repartition(12)
    .write.mode("append").format("jdbc").jdbc(jdbcurl, distanceTable, props)




}
