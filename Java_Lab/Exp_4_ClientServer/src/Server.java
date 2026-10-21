import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Server started...");
            System.out.println("Waiting for client...");

            // accept() blocks until a client connects
            try (Socket socket = serverSocket.accept();
                 BufferedReader input = new BufferedReader(
                         new InputStreamReader(socket.getInputStream()));
                 PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

                System.out.println("Client connected.");

                String message = input.readLine();
                System.out.println("Client says: " + message);

                output.println("Hello Client");
            }

            System.out.println("Connection closed.");

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}