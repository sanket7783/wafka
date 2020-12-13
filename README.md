wafka
=====

Have you ever wondered to connect to Kafka through your web browser in order to stream data to your users?
Wafka is a Spring service which exposes Apache Kafka API over the web using the WebSocket and REST protocols.

You can adopt a streaming approach using a WebSocket from your client or a Request/Response approach using REST
protocol.

### Examples

By default, Wafka is listening (both for WebSocket and REST) on port 8787, but you can change it in 
`application.properties` file.<br><br>

##### 1) WebSocket

Using wafka through WebSocket is very easy. Let's see a Javascript example. You can talk to Wafka through a WebSocket
issuing a set of commands. First thing first, let's create a WebSocket.

```
// Create the websocket.
const consumerWebSocket = new WebSocket("ws://localhost:8787/kafka/consumer/ws/v1"); 

// Define the callback that will be invoked on connection creation.
consumerWebSocket.onopen = () => {
    console.log("WebSocket connection opened.");
    
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

```
function sendMessage(commandObject) {
    consumerWebSocket.send(JSON.stringify(commandObject));
}
```

Now let's create our Kafka Consumer issuing the proper command to Wafka:

```
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

```
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

```
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

##### 2) REST