package lab3.phase2;

import java.io.IOException;
import java.net.ServerSocket;

public class KDCServer {

    public static void main(String[] args) throws IOException {
        // we would have seperate port numbers, so that we have seperate terminals
        int portNumberA = 23456;
        int portNumberB = 23457;
        boolean listening = true;

        try (ServerSocket serverSocketA = new ServerSocket(portNumberA);
             ServerSocket serverSocketB = new ServerSocket(portNumberB)) {

            while (listening) {
                // Accepting incoming connections and spawning a new child socket for each client
                new KDCServerThread(serverSocketA.accept()).start();
                new KDCServerThread(serverSocketB.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port");
            System.exit(-1);
        }
    }
}
