package com.csye7200.streaming.ingest

import com.csye7200.streaming.{Ingest, Result}
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfter, flatspec}

import scala.io.{Codec, Source}
import scala.util.{Failure, Success}

class IngestSpec extends flatspec.AnyFlatSpec with Matchers with BeforeAndAfter{

  behavior of "Ingest Tweets from json"

  it should "match the size" in{
    val ingester = new Ingest[Result]()
    implicit val codec = Codec.UTF8
    val source = Source.fromFile("/home/sumana/CSYE7200-Final-Project/stream-tweets/src/main/resources/response.json")
    val ts = for (t <- ingester(source).toSeq) yield t
    val rs = ts.flatMap(_.toOption)
    val tss = rs.map(r => r.wordleTweets)
    val ssr = for (s <- tss; ss <- s) yield ss
    ssr.size shouldBe 32
    source.close()
  }

  it should "match pattern" in {
    val ingester = new Ingest[Result]()
    implicit val codec = Codec.UTF8
    val source = Source.fromFile("/home/sumana/CSYE7200-Final-Project/stream-tweets/src/main/resources/response.json")
    val ts = for (t <- ingester(source).toSeq) yield t
    ts should matchPattern { case Stream(Success(_)) => }
    source.close()
  }

}
