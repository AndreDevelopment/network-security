package lab2.project2;


import lab2.Colour;



import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;


/*
* Bob will act as the server
* */
public class Bob {
    private static KeyGenPair keyGenPair;
    private static PublicKey alicePublicKey;
    public static void main(String[] args) throws IOException {

        int portNumber = Integer.parseInt("23456");
        keyGenPair = new KeyGenPair();
        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

        ) {

            Object inputLine, outputLine="No object";

            int nonceBob = RSA.generateNonce();
            System.out.println("[GENERATED] Bob's Nonce: "+nonceBob);

            //BEGIN SERVER WHILE
            while ((inputLine = in.readObject()) != null) {

                if (!(inputLine instanceof PublicKey))
                    System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM ALICE: "+Colour.ANSI_RESET);

                if (inputLine instanceof PublicKey){
                    alicePublicKey = (PublicKey) inputLine;
                    //System.out.println("->Public Key received");

                    outputLine = keyGenPair.getPublicKey();
                }
                else if (inputLine instanceof NonceID){
                    System.out.println("->"+inputLine);

                    String prvEncrypt = RSA.encrypt(keyGenPair.getPrivateKey(),((NonceID) inputLine).getNonce()+"");

                    outputLine = nonceBob+RSA.encryptLongString(alicePublicKey,prvEncrypt) ;
                    System.out.println("<-Sending nonce and encrypted message...");

                } else if (inputLine instanceof String) {
                    //DECRYPTION PROCESS
                    System.out.println(Colour.ANSI_RED+"[ENCRYPTED]"+Colour.ANSI_RESET);
                    System.out.println("->"+inputLine);

                    String decryptPub = RSA.decryptLongString(keyGenPair.getPrivateKey(),(String)inputLine);

                    String decryptPrv = RSA.decrypt(alicePublicKey, decryptPub);
                    System.out.println(Colour.ANSI_CYAN+"[DECRYPTED]"+ Colour.ANSI_RESET);
                    System.out.println("->Decrypted Nonce: "+decryptPrv);

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
