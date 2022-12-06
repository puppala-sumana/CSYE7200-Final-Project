--  drop table dbo.wordleTweets;
CREATE TABLE IF NOT EXISTS dbo.wordleTweets (
	currentDate datetime,
	wordleId  INT NOT NULL ,
	created_at bigint,
    date varchar(100),
    timezone int,
    username varchar(100),
    tweet varchar(500),
    tweetClean varchar(100)
);

CREATE TABLE IF NOT EXISTS dbo.wordleUserDistances (
	date varchar(100),
    username varchar(100),
    username_cmp varchar(100),
   --  tweet varchar(500),
    tweetClean varchar(100),
    tweetClean_cmp varchar(100),
   --  tweet_cmp varchar(500),
    
    distance double
);

-- drop table dbo.wordleUserDistances; 
commit;

