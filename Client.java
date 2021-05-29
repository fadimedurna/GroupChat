import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {

   private Socket clientSocket;               // socket to communicate with server
   private String userName;

   public static void main(String[] args) {
      String serverName = "127.0.0.1";
      int serverPort = 12345;
      if (args.length > 0) {
         serverName = args[0];
         try {
            serverPort = Integer.parseInt(args[1]);
         } catch (NumberFormatException e) {
            System.err.println("Port" + args[1] + " must be an integer");
            System.exit(1);
         }
      }

      new Client().runClient(serverName, serverPort); // use args to connect
   }

   private void runClient(String serverName, int port) {
      try {
         connectToServer(serverName, port); // create a Socket to make connection

         new Thread(new InMessages()).start();   //reads messages from server
         new Thread(new OutMessages()).start();  //sends message to server

      } catch (UnknownHostException exception) {
         System.err.println("Unknown host: " + exception.getMessage());
         System.exit(1);
      } catch (IOException e) {
         System.err.println(e.getMessage());
         System.exit(1);

      }
   }

   private void connectToServer(String serverName, int port) throws IOException {
      System.out.println("Attempting connection\n");

      // create Socket to make connection to server
      clientSocket = new Socket(serverName, port);

      // display connection information
      System.out.println("Connected(client socket): " + clientSocket);
   }


   //thread to handle incoming messages
   private class InMessages implements Runnable {

      @Override
      public void run() {
         try {
            BufferedReader reader = new BufferedReader
                    (new InputStreamReader(clientSocket.getInputStream()));

            while (!clientSocket.isClosed()) {
               //reader:input
               String response = reader.readLine();

               if (response != null) {

                  System.out.println("\n" + response);

                  if (userName != null) {
                     System.out.println("[" + userName + "]: ");
                  }
               } else {
                  try {
                     System.out.println("Connection closed. Leaving Chat");
                     reader.close();
                     clientSocket.close();

                  } catch (IOException e) {
                     System.out.println("Error closing connection " + e.getMessage());
                  }
               }
            }

         } catch (SocketException e) {
            // Socket closed by other thread
         } catch (IOException ex) {
            System.out.println("Error reading from server: " + ex.getMessage());
         }
         System.exit(1);
      }
   }

   //thread to handle outgoing messages
   private class OutMessages implements Runnable {

      @Override
      public void run() {

         try {

            BufferedReader consoleMsg = new BufferedReader(new InputStreamReader(System.in)); //gets input from console terminal
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            String message = "";

            System.out.print("Enter your UserName: "); //servera gideceği için output olmalı
            userName = consoleMsg.readLine();

            writer.println(userName);

            while (!clientSocket.isClosed() && !message.equalsIgnoreCase("Quit")) {

               message = consoleMsg.readLine();
               writer.println(message); // display message
               System.out.println("[" + userName + "]: ");

            }

            System.out.println("Connection closed. Leaving Chat");
            try {
               consoleMsg.close();
               writer.close();
               clientSocket.close();

            } catch (SocketException ex) {
               System.out.println("Error closing socket " + ex.getMessage());
            }


         } catch (SocketException e) {
            System.out.println("Error sending message... " + e.getMessage());

         } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());

         }
         System.exit(0);
      }
   }

   /*private void sendData(String s) {
      try // send object to server
      {
         output.writeObject(s);
         output.flush(); // flush data to output
      }
      catch (IOException ioException)
      {
         System.out.println("\nError writing object");
      }
   }*/
}

