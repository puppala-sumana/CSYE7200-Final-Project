
import org.apache.spark.sql.{SparkSession, functions}
import com.csye7200.streaming.DBProperties._
import java.sql.{DriverManager, ResultSet}
import org.apache.log4j.{Level, LogManager, Logger}
import org.apache.spark.ml.clustering.BisectingKMeans
import org.apache.spark.ml.evaluation.ClusteringEvaluator
import org.apache.spark.sql.functions.{col, current_date, row_number, to_date}
import com.csye7200.streaming.Utilities._


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

  val user = "root"
  val password = "Malaysia123#"

/*  def createConnection() = {
    Class.forName("com.mysql.jdbc.Driver");
    DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbo?useSSL=false",user, password);
  }
  def extractValues(r: ResultSet) = {
    (r.getInt(1), r.getString(2))
  }
  val data = createConnection().createStatement.executeQuery("SELECT * FROM wordleTweets ")

 */

  val wordleUsers = spark.read
    .format("jdbc")
    .option("url", "jdbc:mysql://127.0.0.1:3306/dbo?useSSL=false")
    .option("dbtable", "wordleTweets")
    .option("user", "root")
    .option("password", "Malaysia123#")
    .load()

  val wordleUserDistances =
    wordleUsers
    .filter(to_date(col("date")) === current_date())//.filter(countCommas(col("tweet")) <= 29)
    .dropDuplicates()
    .select(col("username"),col("tweet"),col("tweetClean"))
    .crossJoin(wordleUsers.withColumnRenamed("username","username_cmp").withColumnRenamed("tweet","tweet_cmp").withColumnRenamed("tweetClean","tweetClean_cmp"))
    .filter(col("username") =!= col("username_cmp"))
    .withColumn("tweetVector", seqAsVector(toDoubles(udfFillTweet(col("tweet"))))).withColumn("tweetVector_cmp", seqAsVector(toDoubles(udfFillTweet(col("tweet_cmp")))))
    .filter(vecSize(col("tweetVector")) <= 30).filter(vecSize(col("tweetVector_cmp")) <= 30)
    .withColumn("distance", euclideanDistance.apply(col("tweetVector"), col("tweetVector_cmp")))
    .withColumn("rn", row_number.over(w)).orderBy(col("username"), col("distance"))
    .filter(col("rn") <= 10).drop(col("rn"))
    .select(col("date"),col("username"), col("username_cmp"), col("tweetClean"), col("tweetClean_cmp"), col("distance"))


  wordleUserDistances
  .repartition(12)
  .write.mode("append").format("jdbc").jdbc(jdbcurl, distanceTable, props)

  wordleUserDistances
    .repartition(12)
    .write.mode("append").format("jdbc").jdbc(jdbcurl, distanceTable, props)



}
