# Build My Own Mini Http Server

## Result:
![](https://media.giphy.com/media/AeeeCQxhkUNwJwVFGi/giphy.gif)


## Goal
The goal of this project is to further understand the mechanism of how HTTP networking programming works.
As a web programming, the HTTP server is the gateway to the world therefore understanding 
how HTTP Request and Response are handled is crucial for debugging, 
for performance improvement and string parsing using the core library.

The other goal is to have **zero or minimum** dependency mainly rely on Java's core library.

### next goal
- Changing to virtual thread, and about to handle different files, such as images, JSON, etc.
- Adding tests

## Non-Goal
This is not a production/commercial/industry grade project, it is a gateway for curious programmers, like myself, to learn about the how to build HTTP server from scratch in Java.

## Design
Client -> ServerSocket -> Socket (each Client) -> BufferedReader -> Parsing Messages -> OutputStream -> Client

## Explanation
This project is based on Joshua Davies' post on mini HTTP server (see in [reference](#references)), from there I have refactored some of the code to parse string faster.

The HTTP server is made of **Socket**, **Reader**, and **Writer**,they are the core parts for the communication between Client and Server.
Socket is let us listen and reply to client, Reader is the reads the *Requests*, and Writer writers the *Response*

### Socket
We will be using Java's ServerSocket as the base to open the gate and handle incoming Requests, by that we will need to have port number,
similar to Express.js, that is also connecting to Socket under the hood.

Then for the actual client requests will be using another Socket connection from there we will spawn a Thread to handle the actual Request, Response.

### Request
Before we implement the handle for client socket, we will create Reader for incoming messages,
all the messages coming in a string, and there is special format known as the [HTTP Messages]("https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages#http_requests).

From there we are able to get the HTTP methods, URI, path parameter (if any),
we will use StringTokenizer from the Java util library to dissect, and use while loop to get all reat of the information from the HTTP Message

Finally, we also need a BufferedReader to turn all the body of the Request for further processing. 
### Response
Another crucial part of HTTP Server is responding different kind of messages or protocol such as JSON, to response we are rely on the socket's output stream,
here will respond accord to the Request

### Thread
The Socket handler, we are leveraging on the multithreading capability of Java, the socket handler act as a worker, 
whenever there is a new connection it will spawn a Thread to handle in the coming traffics.

### Logger
During the development, I have also learnt how to create a logger from scratch, where I based on the idea of JojoZhang's ([reference](#references)) LogUtils, and I made a wrapper around the Java util Logger.

[//]: # (### GET)

[//]: # (### POST)

[//]: # (### Keep-Alive)

## Completely New
### Cookies (Learnt the most from Joshua's post)
Although Apple and Google are removing Cookies, but it is an important relic, 
from Joshua's blog post on making a mini HTTP Server he [explains how a Cookies is constructed ](https://commandlinefanatic.com/cgi-bin/showarticle.cgi?article=art078).

### SSL (yet to apply)
Still from Joshua's post he talked about [how to set up a SSL](https://commandlinefanatic.com/cgi-bin/showarticle.cgi?article=art077) 
connetion for a HTTP Server, this is something I have never dream of learn until now, but for now I have omitted it in future it will be included in this project. 


## References
#### Joshua's blog post series
[Joshua Davies' Blog Post - 1](https://commandlinefanatic.com/cgi-bin/showarticle.cgi?article=art076)

[Joshua Davies' Blog Post - 2](https://commandlinefanatic.com/cgi-bin/showarticle.cgi?article=art077)

[Joshua Davies' Blog Post - 3](https://commandlinefanatic.com/cgi-bin/showarticle.cgi?article=art078)

#### others
[JojoZhang's HTTP server - Learn the part of creating a Logger ](https://jojozhuang.github.io/programming/building-web-server-with-java-socket/)

[Tutorial Point - Java Networking](https://www.tutorialspoint.com/java/java_socket_programming.htm)

[Java Basic Networking](https://docs.oracle.com/javase/tutorial/networking/overview/networking.html)