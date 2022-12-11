package com.csye7200.streaming.ingest

import com.csye7200.streaming.{Ingest, Result}
import scala.io.{Codec, Source}
import scala.util._
object IngestStreamTweetsSampleJson extends App{
  val ingester = new Ingest[Result]()
  implicit val codec = Codec.UTF8
  val source = Source.fromFile("/home/sumana/CSYE7200-Final-Project/stream-tweets/src/main/resources/response.json")
  val ts = for (t <- ingester(source).toSeq) yield t
  val rs = ts.flatMap(_.toOption)
  val tss = rs.map(r => r.wordleTweets)
  for (s <- tss; ss <- s) yield ss
  source.close()
}
