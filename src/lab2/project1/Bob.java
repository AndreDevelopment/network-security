package lab2.project1;


import lab2.Colour;

import javax.crypto.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;




/*
* Bob will act as the server
* */
public class Bob {
    private static final SecretKey key = KeyGen.getInstance().getKey();
    public static void main(String[] args) throws IOException {



        int portNumber = Integer.parseInt("23456");

        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

        ) {

            Object inputLine, outputLine="No object";

            int nonceBob = AES.generateNonce();
            System.out.println("[GENERATED] Bob's Nonce: "+nonceBob);

            //BEGIN SERVER WHILE
            while ((inputLine = in.readObject()) != null) {

                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM ALICE: "+Colour.ANSI_RESET);

                if (inputLine instanceof NonceID){

                    System.out.println("->"+inputLine);
                    String enObj = AES.encrypt(key,new NonceID(((NonceID) inputLine).getNonce(),"Bob"));
                    outputLine = nonceBob+enObj;
                    System.out.println("<-Sending nonce and encrypted message...");

                } else if (inputLine instanceof String) {

                    System.out.println(Colour.ANSI_RED+"[ENCRYPTED]"+Colour.ANSI_RESET);
                    System.out.println("->"+inputLine);


                    NonceID aliceFinal = AES.decrypt(key,(String)inputLine);

                    System.out.println(Colour.ANSI_CYAN+"[DECRYPTED]\n"+Colour.ANSI_RESET+"->"+aliceFinal);
                    break;

                }

                out.writeObject(outputLine);

            }//end of Server while
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
           e.printStackTrace();
        }
    }
}
