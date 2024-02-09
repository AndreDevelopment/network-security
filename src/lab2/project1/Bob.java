package lab2.project1;


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

//        if (args.length != 1) {
//            System.err.println("Usage: java KnockKnockServer <port number>");
//            System.exit(1);
//        }

        int portNumber = Integer.parseInt("23456");

        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

        ) {

            Object inputLine, outputLine=new Message("No object");

            int nonceBob = Helper.getNonce();

            //BEGIN SERVER WHILE
            while ((inputLine = in.readObject()) != null) {

                if (inputLine instanceof NonceID){

                    String id = "Bob";
                    System.out.println("Got from Alice: "+inputLine);
                    Message enObj = Helper.encrypt(key,new NonceID(((NonceID) inputLine).getNonce(),"Bob"));
                    enObj.setNonce(nonceBob);
                    outputLine = enObj;

                } else if (inputLine instanceof Message) {

                    System.out.println("The encrypted object is: ");
                    for (byte b: ((Message) inputLine).getInformation()){
                        System.out.print(b+" ");
                    }
                    System.out.println();

                    Message aliceFinal = Helper.decrypt(key,((Message) inputLine).getInformation());

                    System.out.println("The actual Object: "+aliceFinal);
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
