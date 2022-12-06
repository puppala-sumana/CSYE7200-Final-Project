import json
from kafka import KafkaConsumer

if __name__ == '__main__':
    # Kafka Consumer
    consumer = KafkaConsumer(
        'messages',
        bootstrap_servers='localhost:9092',
        auto_offset_reset='earliest'
    )
    for message in consumer:
        print(json.loads(message.value))
        # print(csv.loads(message.value))




# from kafka import KafkaConsumer
# import json
# import os
# # from pymongo import MongoClient 

# try:
#     consumer = KafkaConsumer(
#             "tweet",
#             bootstrap_servers='localhost:9092',
#             auto_offset_reset='earliest',
#             group_id="twitter_consumer"
#         )

#     if __name__ == "__main__":
#         print('Starting the Consumer...')
#         print('Data-Scraping will take some time...')
#         for msg in consumer:
#             print(msg)
#             new_tweet = {"$set":json.loads(msg.value)}
#             # Collection.update_one(json.loads(msg.value), new_tweet, upsert=True)
#             #Collection.insert_one(json.loads(msg.value))
#             print("Tweet = {}".format(json.loads(msg.value)))
#         consumer.close()

# except:
#     consumer.close()
#     print("\r", end="")
#     exit(0)