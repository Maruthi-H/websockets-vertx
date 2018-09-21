## websockets-vertx
Demonstration of how one can implement web-socket functionality using plain web-socket API and SockJS client 

## Requirements

Java 8                                                                                                                        
Maven 3.3.9.

## Building

You build the project using:

```
mvn clean install
```

## Testing

The application is tested using [vertx-unit](http://vertx.io/docs/vertx-unit/java/).

## Packaging

The application is packaged as a _fat jar_, using the 
[Maven Shade Plugin](https://maven.apache.org/plugins/maven-shade-plugin/).

## Running

Once packaged, just launch the _fat jar_ as follows:

```
java -jar target/my-first-app-1.0-SNAPSHOT-fat.jar
```

## Running in eclipse or intellij
 
Simply run Start.java as java application

Hit browser with http://localhost:9090/address-workflowStatus to listen to real time update of workflow status(Using vertx SockJS client)
Hit browser with http://localhost:9091/web-socket to listen to real time update of workflow status (without using vertx SockJS client)

