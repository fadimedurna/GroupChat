# Group Chat Application

This is a simple group chat application built using Java's TCP sockets. The application consists of two main components:

1. **ChatServer**: The server that manages the connections from clients and facilitates communication between them.
2. **Client**: The client application that connects to the server, allows the user to send and receive messages.

### Features:
- **Multiple Clients Support**: Multiple clients can connect to the server and participate in the chat.
- **Broadcast Messages**: Messages can be sent to all connected clients.
- **Direct Messages**: Clients can send private messages to each other using the `@username` syntax.
- **User Authentication**: Each client is required to log in with a unique username.
- **Connected Users List**: The server sends a list of currently connected users to new clients upon connection.

### Technologies Used:
- Java Sockets (TCP)
- Threads for handling multiple clients simultaneously
- BufferedReader and PrintWriter for I/O communication

---

## How to Run the Application

### Prerequisites:
- Java Development Kit (JDK) 8 or higher installed on your system.
- Basic understanding of how to run Java programs.

### Instructions:

#### 1. **Running the Server (ChatServer.java)**:
- Compile and run the `ChatServer` class.
- The server listens on a specified port (default: 12345). You can specify a different port by passing the port number as a command-line argument.

```bash
javac ChatServer.java
java ChatServer <optional-port-number>
```

#### 2. **Running the Client (Client.java):**:
- Compile and run the Client class.
- The client connect to the server using the IP address (127.0.0.1 for localhost) and port number (default: 12345).

```bash
javac Client.java
#IF PORT NUMBER SPECIFIED IN THE SERVER SIDE
java Client 127.0.0.1 <specified-port-number>
#IF NOT SPECIFIED 
java Client
```
After starting the client, the user will be prompted to enter a username. Once logged in, they can send messages to all users or direct messages to specific users.

# How It Works

## Commands:

- **Broadcast message:** Just type the message and press enter.
- **Private message:** Type `@username <message>` to send a private message to a specific user.
- **Quit:** Type `Quit` to close the connection to the server.

## Server:

- **Accepting Connections:** The server listens for incoming connections on a specified port.
- **Handling Clients:** For each new client connection, the server creates a new `ClientThread`, which runs in a separate thread to handle communication with that specific client.
- **Broadcasting Messages:** When a client sends a message, the server broadcasts it to all other connected clients.
- **Direct Messages:** Clients can send private messages by using the `@username` syntax. The server directs the message to the appropriate client.
- **Handling Disconnects:** If a client disconnects, the server removes them from the list of connected clients and notifies others.

## Client:

- **User Login:** Upon starting, the client asks the user for a username. If the username already exists, the client is disconnected.
- **Sending Messages:** The client can send messages to all other clients or to a specific user (by prefixing the message with `@username`).
- **Receiving Messages:** The client listens for messages from the server and displays them in the console.
- **Closing Connection:** When the user types `Quit`, the client closes the connection to the server.


