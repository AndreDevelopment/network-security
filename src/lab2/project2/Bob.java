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
            System.out.println("(GENERATED) Bob's Nonce: "+nonceBob);

            //BEGIN SERVER WHILE
            while ((inputLine = in.readObject()) != null) {

                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM ALICE: "+Colour.ANSI_RESET);

                if (inputLine instanceof PublicKey){
                    alicePublicKey = (PublicKey) inputLine;
                    System.out.println("Public Key received");

                    outputLine = keyGenPair.getPublicKey();
                }
                else if (inputLine instanceof NonceID){
                    System.out.println(inputLine);

                    String prvEncrypt = RSA.encrypt(keyGenPair.getPrivateKey(),((NonceID) inputLine).getNonce()+"");

                    int mid = prvEncrypt.length() / 2;
                    String firstHalf = prvEncrypt.substring(0,mid);
                    String secondHalf = prvEncrypt.substring(mid);

                    String enFirstHalf = RSA.encrypt(alicePublicKey,firstHalf);
                    String enSecondHalf = RSA.encrypt(alicePublicKey,secondHalf);

                    //String pubEncrypt = Helper.encrypt(alicePublicKey,prvEncrypt);
                    outputLine = nonceBob+enFirstHalf+enSecondHalf;


                } else if (inputLine instanceof String) {
                    //DECRYPTION PROCESS
                    System.out.println(Colour.ANSI_RED+"-ENCRYPTED-"+Colour.ANSI_RESET);
                    System.out.println(inputLine);

                    //inputLine will now be a decrypted Message Object

                    int mid = ((String) inputLine).length() / 2;
                    String firstHalf = ((String) inputLine).substring(0,mid);
                    String secondHalf = ((String) inputLine).substring(mid);

                    String deFirstHalf = RSA.decrypt(keyGenPair.getPrivateKey(),firstHalf);
                    String deSecondHalf = RSA.decrypt(keyGenPair.getPrivateKey(),secondHalf);

                    //fromBob will now be a decrypted Message Object
                    String decryptPub = deFirstHalf+deSecondHalf;

                    String decryptPrv = RSA.decrypt(alicePublicKey, decryptPub);
                    System.out.println(Colour.ANSI_CYAN+"-DECRYPTED-"+ Colour.ANSI_RESET);
                    System.out.println("My Decrypted Nonce: "+decryptPrv);


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
