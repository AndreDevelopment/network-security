package lab2.project1;


import javax.crypto.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


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
//                BufferedReader in = new BufferedReader(
//                        new InputStreamReader(clientSocket.getInputStream()));
        ) {

            Object inputLine, outputLine=new Message("No object recv");
            System.out.println("The Key for Bob: "+key.toString());
            int nonceBob = Helper.getNonce();

            //BEGIN SERVER WHILE
            while ((inputLine = in.readObject()) != null) {

                //Decrypt Server side
//                System.out.println("(ENCRYPT) Alice: "+inputLine);
//                inputLine = Cipher.decryption(inputLine);

                System.out.println("(RECV) Alice: "+inputLine);

                if (inputLine instanceof NonceID){
                    //System.out.println("Enter a message: ");


                    String id = "Bob";
                    EMessage encryptMsg = new EMessage(key,new NonceID(((NonceID) inputLine).getNonce(), id));

                    Cipher cipher = Cipher.getInstance("AES");

                    cipher.init(Cipher.ENCRYPT_MODE,key);
                    byte[] objBytes = cipher.doFinal(Helper.convertToByteArray(encryptMsg));

                    outputLine = new Message(objBytes);
                }

                out.writeObject(outputLine);

//                if (outputLine.equalsIgnoreCase("Bye."))
//                    break;
            }//end of Server while
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
           e.printStackTrace();
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
