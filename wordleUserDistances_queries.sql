select * from dbo.wordleTweets limit 50;
-- select length(tweet), tweetClean,tweet from dbo.wordleTweets where length(tweet) > 134 group by tweetClean, tweet;

-- select * from wordleTweets where length(tweet) = 240;

-- select count(1) from dbo.wordleUserDistances;
select * from dbo.wordleUserDistances where distance <1 order by username; 
select * from dbo.wordleUserDistances where username = 'TommyMilagro' order by distance