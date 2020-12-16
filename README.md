# wafka

Have you ever wondered to connect to Kafka through your web browser in order to stream data to your users?
Wafka is a Spring service which exposes Apache Kafka API over the web using the WebSocket and REST protocols.

You can adopt a streaming approach using a WebSocket from your client or a Request/Response approach using REST
protocol.

### Build
___

You can customize your build environment simply creating a directory under the ```env```
directory. Don't forget to put the ```application.properties``` file. Once you created
the environment directory you can build the package. For example, if you would create a build
for my test environment you could do:

```
mkdir -p env/test/envConf
cp conf/application.properties env/test/envConf  # You should customize your properties.
mvn clean install -Dtargetenv=test               # If you have a maven-settings file you can specify it with -s switch.
```



### Examples
___

By default, Wafka is listening (both for WebSocket and REST) on port 8787, but you can change it in 
`application.properties` file.<br><br>

### 1) WebSocket

Using wafka through WebSocket is very easy. Let's see a Javascript example. You can talk to Wafka through a WebSocket
issuing a set of commands. First thing first, let's create a WebSocket.

```javascript
// Create the websocket.
const consumerWebSocket = new WebSocket("ws://localhost:8787/kafka/consumer/ws/v1"); 

// Define the callback that will be invoked on connection creation.
consumerWebSocket.onopen = () => {
    console.log("WebSocket connection opened.");

    // We'll see these functions in the next section.
    createConsumer();
    subscribeTopics();
    startConsumer();
};

// Define the callback to invoke in case of errors.
consumerWebSocket.onerror = (event) => {
    console.error("An error occurred: " + event);
};

// Define the callback to invoke when the connection is closed.
consumerWebSocket.onclose = () => {
    console.log("The connection was closed or timed out.");
};

// Define the callback to invoke when a message arrive through the websocket.
consumerWebSocket.onmessage = (event) => {
    event.data.text().then(response => {
        const jsonResponse = JSON.parse(response);
        if (jsonResponse.responseType !== "INCOMING_DATA") {
            console.log(jsonResponse);
            return;
        }

       // Wafka talks binary, it doesn't know which kind of data travels through Kafka. So for example, here we
       // will convert the incoming binary data into text.
       
       const fetchedContents = jsonResponse.fetchedContents;
       for (let index in fetchedContents) {
            let decodedContent = JSON.parse(convertBinaryToString(fetchedContents[index].content));
            // Do something with your data, like updating your view.
       }
   });
};

// Simple function to convert a binary input data into a string
function convertBinaryToString(binaryData: any) {
    let result = "";
    for (let i = 0; i < binaryData.length; ++i) {
        result += String.fromCharCode(parseInt(binaryData[i]));
    }
    return result;
}
```

in the `onopen` callback there are three functions. Let's see what they do. As I said before, you talk with Wafka
issuing command on the websocket. So let's define a function to send commands.

```javascript
function sendMessage(commandObject) {
    consumerWebSocket.send(JSON.stringify(commandObject));
}
```

Now let's create our Kafka Consumer issuing the proper command to Wafka:

```javascript
function createConsumer() {
    const command = {
        commandName: "create-consumer",
        arguments: {
            kafkaClusterUri: "localhost:9092", // Specify here your Kafka cluster URI
            groupId: "my-group-id",            // Specify here your group ID
            enableAutoCommit: true             // You can choose to enable auto commit or no.
        }
   };
   sendMessage(command);
}
```

Done. You have created your consumer. Now you should subscribe it to some topics. Let's do this defining
another function.

```javascript
function subscribeTopics() {
    const command = {
        commandName: "subscribe-topic",
        arguments: {
            topics: ["my-topic"]
        }
    };
    sendMessage(command);
}
```

Done. Your subscription list is updated. Note that you can call this function whenever you want. Wafka will deal
with the missing Kafka consumer thread safety for you, so you don't have to worry about that.

All you need to do now is start your consumer remote poll loop. Wafka will start a thread for your consumer, which will
be associated with this very websocket and will stream data over it.

```javascript
function startConsumer() {
    const command = {
        commandName: "start-consumer",
        arguments: {
            pollDuration: 1
        }
    };
    sendMessage(command);
}
```

You can specifiy your poll duration interval in seconds. After this, you will have your consumer up and runnig.<br><br>

### 2) REST

If you don't want to use websockets and you prefer a request/response approach, you can use the Wafka REST interface. 
In the following example I'll assume you are running Wafka on localhost on the default port.

In order to create a Kafka Consumer the endpoint to call is:

```
http://localhost:8787/kafka/consumer/rest/v1/<consumer-id>/<consumer-group-id>/create
```

For example:

```bash
curl -X POST "http://localhost:8787/kafka/consumer/rest/v1/my-consumer-id/my-group-id/create?enableAutoCommit=true&kafkaClusterId=localhost:9092"

{
    "consumerId": "my-consumer-id",
    "kafkaClusterUri": "localhost:9092",
    "groupId": "my-group-id",
    "enableAutoCommit": true,
    "message": "Consumer successfully created."
}
```

Now we have a consumer identified by the string "my-consumer-id" belonging to the "my-group-id" group. Next step is 
to suscribe to topics. In order to do it, we can use the endpoint:

```
http://localhost:8787/kafka/consumer/rest/v1/<consumer-id>/subscribe
```

For example:

```bash
curl -X POST -H "Content-Type: application/json" --data '["testing-topic"]' http://localhost:8787/kafka/consumer/rest/v1/my-consumer-id/subscribe

{
    "subscriptions": [
        "testing-topic"
    ],
    "consumerId": "my-consumer-id",
    "message": "Subscriptions updated."
}
```

Now, if you use a Kafka producer to sent some data to that topic, you can issue a fetch call in order to get some data.
The endpoint to call in this case is:

```
http://localhost:8787/kafka/consumer/rest/v1/<consumer-id>/fetch"
```

For example:

```bash
curl -X GET "http://localhost:8787/kafka/consumer/rest/v1/my-consumer-id/fetch?pollDuration=1"

{
    "data": {
        "responseType": "INCOMING_DATA",
        "message": "Successfully fetched data.",
        "fetchedContents": [
            {
                "key": null,
                "content": [
                    99,
                    111,
                    110,
                    116,
                    101,
                    110,
                    116,
                    32,
                    115,
                    101,
                    110,
                    116
                ],
                "topic": "testing-topic",
                "partition": 0,
                "offset": 4446
            }
        ]
    },
    "consumerId": "my-consumer-id",
    "message": "Successfully fetched data from topics."
}
```

In this case, I used the Kafka console producer in order to send a message on the subscribed topic. The binary message
is visible under the JSON key "fetched_content".
