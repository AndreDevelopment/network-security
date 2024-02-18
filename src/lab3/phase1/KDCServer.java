package lab3.phase1;




import lab1.sirichat.project2.SiriMultiServerThread;


import java.io.IOException;

import java.net.ServerSocket;

public class KDCServer {



    public static void main(String[] args) throws IOException {



        int portNumber = Integer.parseInt("23456");
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                //Here we accept incoming connections and create a new child socket to be used for each client
                new KDCServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
    }

