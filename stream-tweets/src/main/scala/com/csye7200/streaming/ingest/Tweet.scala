package com.csye7200.streaming

import spray.json.DefaultJsonProtocol

import scala.util._




case class Tweet(created_at: Long,date: String, username: String, tweet: String)

//case class User(name: String)

case class Result(wordleTweets: List[Tweet])

//case class TweetList(tweet: List[Tweet])


object Tweets extends DefaultJsonProtocol {
//  implicit val formattedTweetList = jsonFormat1(TweetList.apply)
  implicit val formattedTweet = jsonFormat4(Tweet.apply)
  implicit val formattedResponse = jsonFormat1(Result.apply)
}

object Result {
  import spray.json._

  trait IngestibleResult extends Ingestible[Result] {

    def fromString(w: String): Try[Result] = {
      import Tweets._
      Try(w.parseJson.convertTo[Result])
    }
  }

  implicit object IngestibleResult extends IngestibleResult

}


object Tweet {
  import spray.json._

  trait IngestibleTweet extends Ingestible[Tweet] {

    def fromString(w: String): Try[Tweet] = {
      import Tweets._
      Try(w.parseJson.convertTo[Tweet])
    }
  }

  implicit object IngestibleTweet extends IngestibleTweet

}

//object TweetList {
//  import spray.json._
//
//  trait IngestibleTweetList extends Ingestible[TweetList] {
//
//    def fromString(w: String): Try[TweetList] = {
//      import Tweets._
//      Try(w.parseJson.convertTo[TweetList])
//    }
//  }
//
//  implicit object IngestibleTweetList extends IngestibleTweetList
//
//}