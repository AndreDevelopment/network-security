package lab2.project2;



import lab2.Colour;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PrivateKey;
import java.security.PublicKey;


/*
* Alice is the Client
* */
public class Alice {

    private static KeyGenPair keyGenPair;
    private static PublicKey bobPublicKey;
    public static void main(String[] args) throws IOException {


        String hostName = "localhost";
        int portNumber = Integer.parseInt("23456");
        keyGenPair = new KeyGenPair();
        try (
                Socket aliceSocket = new Socket(hostName, portNumber);

                ObjectOutputStream out = new ObjectOutputStream(aliceSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(aliceSocket.getInputStream());

        ) {

            Object fromBob,fromAlice;

            int aliceNonce = Helper.getNonce();
            System.out.println("(GENERATED) Alice's Nonce: "+aliceNonce);



            //Output line is encrypted
            fromAlice = keyGenPair.getPublicKey();

            out.writeObject(fromAlice);

            while ((fromBob = in.readObject()) != null) {

                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM BOB: "+Colour.ANSI_RESET);
                if (fromBob instanceof PrivateKey){
                    bobPublicKey = (PublicKey) fromBob;
                    System.out.println("Public Key received");

                    //Alice sends her Nonce
                    fromAlice = new NonceID(aliceNonce,"Alice");
                }

                else if (fromBob instanceof Message){

                    //DECRYPTION PROCESS
                    System.out.println("Nonce from Bob: "+((Message) fromBob).getNonce());
                    //The initial encrypted Object
                    System.out.println(Colour.ANSI_RED+"-ENCRYPTED OUTER-"+Colour.ANSI_RESET);
                    System.out.println( ((Message) fromBob).getMsg());

                    //outer object decrypted
                    Message outerBob = Helper.decrypt(keyGenPair.getPrivateKey(),((Message) fromBob).getMsg());
                    System.out.println(Colour.ANSI_CYAN+"-DECRYPTED OUTER-\n"+ Colour.ANSI_RESET+outerBob);

                    //Now decrypt that inner object
                    Message innerBob = Helper.decrypt(keyGenPair.getPrivateKey(), outerBob.getMsg());
                    System.out.println(Colour.ANSI_CYAN+"\t\t-DECRYPTED INNER-\n"+ Colour.ANSI_RESET+innerBob);

                    //Now I send back her encrypted shit and change parameters
                    
                    //Step 1 - Encrypt private key and nonce
                    //Object containing the Private Key & Alice's Nonce
                    Message innerPrKN = new Message(keyGenPair.getPrivateKey(),((Message) fromBob).getNonce());
                    //This string is the encrypted version of innerPrKN
                    String encryptedPrKN = Helper.encrypt(bobPublicKey,innerPrKN).getMsg();

                    //Step 2 - Encrypt the Public key with the inner encrypted obj
                    //Object containing the necessary information
                    Message outerPuK = new Message(bobPublicKey,encryptedPrKN);
                    //Now I encrypt outerPuK, package it in a Message and add a Nonce and send it off
                    fromAlice = Helper.encrypt(bobPublicKey,outerPuK);

                    out.writeObject(fromAlice);
                    break;
                }

                out.writeObject(fromAlice);


            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        } catch (ClassNotFoundException e) {
           e.printStackTrace();
        }
    }

}
