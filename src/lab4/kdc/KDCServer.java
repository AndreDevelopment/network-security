package lab4.kdc;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;


public class KDCServer {


    public static void main(String[] args) throws IOException {
        // we would have seperate port numbers, so that we have seperate terminals
        int portNumber = 23456;
        boolean listening = true;
        try (ServerSocket serverSocket = new ServerSocket(portNumber);
             ) {

            while (listening) {
                // Accepting incoming connections and spawning a new child socket for each client
                new KDCServerThread(serverSocket.accept()).start();

            }
        } catch (IOException e) {
            System.err.println("Could not listen on port");
            System.exit(-1);
        }
    }
}
