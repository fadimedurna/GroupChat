// Fig. 28.3: Server.java
// Server portion of a client/server stream-socket connection.
//TCP

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ChatServer {
    private ServerSocket serverSocket;         // server socket
    private Socket connection;           // connection to client //side socket

    private List<ClientThread> clientThreads = Collections.synchronizedList(new ArrayList<ClientThread>());

    public static void main(String[] args) {
        int serverPort = 12345;
        if (args.length > 0) {
            try {
                serverPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Port" + args[0] + " must be an integer");
                System.exit(1);
            }
        }
        //Server objesi oluşturulup startServer metoduna atanır
        ChatServer server = new ChatServer();
        server.runServer(serverPort);
    }

    // set up and run server
    public void runServer(int serverPort) {
        try // set up server to receive connections; process connections
        {
            serverSocket = new ServerSocket(serverPort); // create ServerSocket
            System.out.println("ChatServer is listening to port: " + serverPort);

            while (true) {
                try {
                    waitForConnection();     // wait for a connection +
                    ClientThread newClient = new ClientThread(connection, this);
                    new Thread(newClient).start();
                } catch (EOFException eofException) { //!!!!!!!!!!!!!!!
                    System.out.println("\nServer terminated connection");
                } catch (IOException e) {
                    System.out.println("Something wrong in serverSocket");
                    e.printStackTrace();
                }
            }
        } catch (IOException ioException) {
            System.out.println("Something wrong in server " + ioException.getMessage());
        }
    }

    // wait for connection to arrive, then display connection info in the server CLI
    //waitForConnection();
    private void waitForConnection() throws IOException {
        System.out.println("Waiting for connection\n");
        connection = serverSocket.accept(); // allow server to accept connection
        System.out.println("New user connected");
        System.out.println("Connection received from: " +
                connection.getInetAddress().getHostName());//When client connect to the server!!!!!!!!!!
    }

    void broadcast(String message, ClientThread excludeUser) { //excludeUser:sender
        synchronized (clientThreads) {
            for (ClientThread aUser : clientThreads) {
                if (aUser != excludeUser) {
                    aUser.sendMessage(message);
                }
            }
        }
    }

    //String serverMessage
    private void directMessage(String userTo, String message) {

        synchronized (clientThreads) {
            for (ClientThread aUser : clientThreads) {
                if (aUser.getUserName().equals(userTo)) {
                    aUser.sendMessage(message);
                    return;
                }
            }
        }
    }

    //checks that if user exists
    public boolean userExists(String user) {

        System.out.println(user);
        for (ClientThread aUser : clientThreads) {
            System.out.println(aUser.getUserName());
            if (aUser.getUserName().equals(user)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sends a list of online users to the newly connected user.
     */
    private String sendConnectedUsers() {
        StringBuilder allUsers = new StringBuilder();
        allUsers.append("List of connected users; ");

        synchronized (clientThreads) {
            for (ClientThread user : clientThreads) {
                allUsers.append(user.getUserName() + ", ");
            }
        }
        return allUsers.toString();
    }

    /*void addUserName(String userName) {
            userNames.add(userName);
    }*/

    //(important part)
    public class ClientThread implements Runnable {
        private Socket connection;//side socket
        private ChatServer server;//main socket
        private String userName;
        private BufferedReader reader; //INPUT
        private PrintWriter writer; //OUTPUT

        public ClientThread(Socket connection, ChatServer server) throws IOException {
            this.connection = connection;
            this.server = server;
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            writer = new PrintWriter(connection.getOutputStream(), true);
        }


        //Sends a message to the client. ////serverdan gönderip clienta yazdırır(*)
        void sendMessage(String message) {
            writer.println(message);
            System.out.println(message);
        }

        public void closeConnection() throws IOException {

            System.out.println("Client " + userName + " closed, exiting...");
            broadcast("(" + userName + "): left the group chat", this);

            reader.close();
            connection.close(); //side socket
            clientThreads.remove(this);
        }

        public String getUserName() {
            return userName;
        }

        public void run() {

            String clientMessage = " ";

            //checking user that logged in or not
            try {
                userName = reader.readLine();

                if (userExists(userName)) {
                    sendMessage("Alert from the Server -> User already connected!");
                    connection.close();
                    reader.close();
                    return;
                }
                Thread.currentThread().setName(userName);
                clientThreads.add(this);

                directMessage(userName, sendConnectedUsers()); //sendConnectedUsers():string döndürür
                String serverMessage = "New user connected: " + userName;
                broadcast(serverMessage, this);       // announce the newly connected client to the online clients.

            } catch (SocketException e) {
                System.out.println("Socket Connection Lost!");
            } catch (IOException ex) {
                System.out.println("Error in UserThread: " + ex.getMessage());
            }


            //main loop part of the chat-getting message
            do {
                try {
                    clientMessage = reader.readLine();

                    if (clientMessage == null) {
                        closeConnection();
                        return;
                    }

                    if (!clientMessage.isEmpty()) {

                        //private message
                        if (clientMessage.startsWith("@")) {

                            String[] privateMessage = clientMessage.split(" ", 2);
                            String userTo = privateMessage[0].substring(1);

                            if (userExists(userTo)) {
                                clientMessage = privateMessage[1];
                                directMessage(userTo, "(" + userName + "): " + clientMessage);
                            }

                        } else {
                            //broadcasting
                            server.broadcast("(" + userName + "): " + clientMessage, this);
                        }
                    }

                } catch (IOException e) {
                    System.out.println("Receiving error on " + userName + " : " + e.getMessage());
                }
            } while (!connection.isClosed() && !clientMessage.equalsIgnoreCase("Quit"));

            try {
                closeConnection();
            } catch (IOException e) {
                System.out.println("Receiving error closing... " + userName + " : " + e.getMessage());
            }

            /*server.removeUser(userName, this);
            connection.close();

            serverMessage = userName + " has quitted.";
            broadcast(serverMessage, this);*/
        }

        /*void removeUser(String userName, ClientThread aUser) {
            boolean removed = userNames.remove(userName);
            if (removed) {
                clientThreads.remove(aUser);
                System.out.println("The user " + userName + " left the chat!!!");
            }
        }*/
    }
}
