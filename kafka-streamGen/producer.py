import time
import json
import random
from datetime import datetime
from data_generator import scrape
from kafka import KafkaProducer

# Messages will be serialized as JSON
def json_serializer(message):
    return json.dumps(message).encode('utf-8')

# Kafka Producer
producer = KafkaProducer(
    bootstrap_servers=['localhost:9092'],
    value_serializer=json_serializer
)


if __name__ == "__main__":
    try:
        while True:
            tweet=scrape()
            for i in range(len(tweet)):
                producer.send('messages', {"tweet": tweet[i]})
            time.sleep(60)
    except Exception as e:
        producer.close()
        print("\r", end="")
        exit(0)
