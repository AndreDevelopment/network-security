package lab1.sirichat.project2;

import lab1.knockmulti.KKMultiServerThread;

import java.io.IOException;
import java.net.ServerSocket;

public class SiriMultiServer {

    public static void main(String[] args) throws IOException {

//        if (args.length != 1) {
//            System.err.println("Usage: java KKMultiServer <port number>");
//            System.exit(1);
//        }

        int portNumber = Integer.parseInt("23456");
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                //Here we accept incoming connections and create a new child socket to be used for each client
                new SiriMultiServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }

}
