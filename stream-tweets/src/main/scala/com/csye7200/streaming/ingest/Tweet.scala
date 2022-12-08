package com.csye7200.streaming

import spray.json.DefaultJsonProtocol

import scala.util._


case class Tweet(created_at: String,date: String, user: User, tweet: String)

case class User(username: String)

case class Response(wordles: List[Tweet])


object TweetProtocol extends DefaultJsonProtocol {
  implicit val formatUser = jsonFormat1(User.apply)
  implicit val formatTweet = jsonFormat4(Tweet.apply)
  implicit val formatResponse = jsonFormat1(Response.apply)
}

object Response {
  import spray.json._

  trait IngestibleResponse extends Ingestible[Response] {

    def fromString(w: String): Try[Response] = {
      import TweetProtocol._
      Try(w.parseJson.convertTo[Response])
    }
  }

  implicit object IngestibleResponse extends IngestibleResponse

}


object Tweet {
  import spray.json._

  trait IngestibleTweet extends Ingestible[Tweet] {

    def fromString(w: String): Try[Tweet] = {
      import TweetProtocol._
      Try(w.parseJson.convertTo[Tweet])
    }
  }

  implicit object IngestibleTweet extends IngestibleTweet

}