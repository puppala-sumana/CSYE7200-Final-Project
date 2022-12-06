import random
import string
import sys
from setup import twint
import nest_asyncio 
import pandas as pd
import numpy
import time
from datetime import datetime, timedelta
nest_asyncio.apply()

limit= 5

def scrape():
    tweets={
        "tweet_id":[],
        "created_at":[],
        "date":[],
        "timezone":[],
        "username":[],
        "tweet":[],
    }
    now=datetime.now()
    since=(now-timedelta(seconds=60)).strftime('%Y-%m-%d %H:%M:%S')
    until=(now+timedelta(seconds=12)).strftime('%Y-%m-%d %H:%M:%S')
    print(now,since,until)
    
    c=twint.Config()
    c.Search="wordle" 
    c.Pandas=True
    c.Limit=100
    c.Filter_retweets=True
    twint.run.Search(c)

    df=twint.storage.panda.Tweets_df
    

    for i in range(len(df)):
        dates=df["date"][len(df)-i-1]
        if dates<since or dates>until:
            continue
        tweets["created_at"].append(df["created_at"][len(df)-i-1])
        tweets["date"].append(df["date"][len(df)-i-1])
        tweets["timezone"].append(df["timezone"][len(df)-i-1])
        tweets["username"].append(df["username"][len(df)-i-1])
        tweets["tweet"].append(df["tweet"][len(df)-i-1])
        
    # print(tweets)
    result = []
    for index in range(len(tweets["date"])):
        #tweet_id=tweets["tweet_id"][index]
        created_at=tweets["created_at"][index]
        date=tweets["date"][index]
        timezone=tweets["timezone"][index]
        username=tweets["username"][index]
        tweet=tweets["tweet"][index]

        result.append({
        "created_at":created_at,
        "date":date,
        "timezone": timezone,
        "username":username,
        "tweet":tweet,
        })

    return result

# scrape()
