import com.csye7200.streaming
import com.csye7200.streaming.{Ingest, Tweet}
import com.csye7200.streaming.DBProperties
import org.apache.spark.sql.{SparkSession, functions}

import scala.io.{Codec, Source}
import scala.util.parsing.json.JSON
val ingester = new Ingest[Tweet]()
val codec = Codec.UTF8
val json_content = Source.fromFile("/home/sumana/CSYE7200-Final-Project/stream-tweets/src/main/resources/result.csv")

//val json_data = JSON.parseFull(json_content)


val ts = for (t <- ingester(json_content).toSeq) yield t
println(ts)

  val spark: SparkSession = SparkSession
   .builder()
   .appName("Wordle")
   .master("local[*]")
   .getOrCreate()
val path = "/home/sumana/CSYE7200-Final-Project/stream-tweets/src/main/resources/result.csv"
val df = spark.read.option("header", true)
  .option("multiLine", true)
  .option("truncate", false)
  .option("ignoreTrailingWhiteSpace", true).csv(path)

df.show(2)