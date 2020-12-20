from kafka import KafkaProducer
from random import random, seed, choice, randint
import json
import time
import string
import sys

seed(1)

brokers = ["localhost:9092"]
topic = "testing-topic"

letters = string.ascii_uppercase
producer = KafkaProducer(
        retries=5,
        api_version=(2,5,0),
        bootstrap_servers=brokers,
        value_serializer=lambda m: json.dumps(m).encode('ascii')
)

index = []
isin = []
vectorSize = 1000
for j in range(vectorSize):
    index.append(j)
    isin.append(''.join(choice(letters) for k in range(20)))

iterations = 1000
for i in range(0, iterations):
        firstValue = []
        secondValue = []
        thirdValue = []
        fourthValue = []
        fifthValue = []
        sixthValue = []
        seventhValue = []
        eighthValue = []
        ninethValue = []
        tenthValue = []

        for j in range(vectorSize):
            firstValue.append(round(random(), 4))
            secondValue.append(round(random(), 4))
            thirdValue.append(round(random(), 4))
            fourthValue.append(round(random(), 4))
            fifthValue.append(round(random(), 4))
            sixthValue.append(round(random(), 4))
            seventhValue.append(round(random(), 4))
            eighthValue.append(round(random(), 4))
            ninethValue.append(round(random(), 4))
            tenthValue.append(round(random(), 4))

        toSend = {
            "index": index,
            "isin": isin,
            "firstValue": firstValue,
            "secondValue": secondValue,
            "thirdValue": thirdValue,
            "fourthValue": fourthValue,
            "fifthValue": fifthValue,
            "sixthValue": sixthValue,
            "seventhValue": seventhValue,
            "eighthValue": eighthValue,
            "ninethValue": ninethValue,
            "tenthValue": tenthValue
        }

        print("Sending object {}/{}".format(str(i), str(iterations)))
        x = producer.send(topic, toSend)
        x.get()
        #time.sleep(0.1)
        print("Sent object {}/{}\n".format(str(i), str(iterations)))

producer.flush()
producer.close()
