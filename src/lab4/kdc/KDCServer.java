package lab4.kdc;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;


public class KDCServer {

    private static List<KDCServerThread> clientList;

    public static void main(String[] args) throws IOException {
        // we would have seperate port numbers, so that we have seperate terminals
        int portNumber = 23456;

        boolean listening = true;
        clientList = new ArrayList<>();

        try (ServerSocket serverSocket = new ServerSocket(portNumber);
             ) {

            while (listening) {
                // Accepting incoming connections and spawning a new child socket for each client
                KDCServerThread client = new KDCServerThread(serverSocket.accept());
                clientList.add(client);
                client.start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port");
            System.exit(-1);
        }
    }
}
