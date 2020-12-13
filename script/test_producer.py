from kafka import KafkaProducer
from random import random, seed, choice, randint
import json
import time
import string
import sys

seed(1)

brokers = [
        "localhost:9092"
]

topic = "testing-topic"


letters = string.ascii_uppercase
producer = KafkaProducer(
        retries=2,
        api_version=(2,5,0),
        bootstrap_servers=brokers,
        value_serializer=lambda m: json.dumps(m).encode('ascii')
)

vectors_num = 2
content_size = 1000
iterations = 1000
vectors = {}


# Perform "content_size" iterations building a vector of vectors
for i in range(0, vectors_num):
    toSend = []
    for j in range(content_size):
        toSend.append({
            "randomValue" : ''.join(choice(letters) for k in range(20)),
            "firstValue" : round(random(), 4),
            "secondValue": round(random(), 4),
            "thirdValue": round(random(), 4),
            "fourthValue": round(random(), 4),
            "fifthValue": round(random(), 4)
        })
    vectors[i] = toSend


# Send randomically to the producer an object from the vector of vectors
for i in range(iterations):
    index = randint(0, vectors_num - 1)

    print("Sending object {}/{}".format(str(i), str(iterations)))
    response = producer.send(topic, vectors[index])
    response.get()
    print("Sent object {}/{}\n".format(str(i), str(iterations)))

producer.flush()
producer.close()
