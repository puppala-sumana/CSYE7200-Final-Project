package com.csye7200.streaming

object DBProperties {

  val server_name = "jdbc:mysql://127.0.0.1:3306"
  val database_name = "dbo"
  val jdbcurl = server_name + "/" + database_name + "?useSSL=false"
  val tweetsTable = "wordleTweets"
  val distanceTable = "wordleUserDistances"
  val props = new java.util.Properties
  props.setProperty("driver", "com.mysql.jdbc.Driver")
  props.setProperty("user", "root")
  props.setProperty("password", "Malaysia123#")

}
