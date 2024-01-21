package lab1.sirichat.project1;

import lab1.sirichat.Cipher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SiriClient {
    public static void main(String[] args) throws IOException {

//        if (args.length != 2) {
//            System.err.println(
//                    "Usage: java EchoClient <host name> <port number>");
//            System.exit(1);
//        }

        String hostName = "localhost";
        int portNumber = Integer.parseInt("23456");
        // The above number is a port number.
        try (
                Socket siriSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(siriSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(siriSocket.getInputStream()));
        ) {
            BufferedReader stdIn =
                    new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;


            while ((fromServer = in.readLine()) != null) {
                //Decrypted message from the Server
                System.out.println("(ENCRYPT) Server: " + fromServer);
                fromServer = Cipher.decryption(fromServer);
                System.out.println("(DECRYPT) Server: " + fromServer);
                if (fromServer.equalsIgnoreCase("Bye."))
                    break;

                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    //System.out.println("Client: " + fromUser);
                    out.println(Cipher.encryption(fromUser)) ;
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }
    
}
