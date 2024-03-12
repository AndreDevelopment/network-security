package lab4.kdc;

import lab4.Colour;
import lab4.KeyGenPair;
import lab4.RSA;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.Scanner;

public class Client {

    private static KeyGenPair keys;
    private static PublicKey serverPublicKey;
    


    public static void main(String[] args) throws IOException {

        keys =  new KeyGenPair();

        String hostName = "localhost";
        int portNumber = Integer.parseInt("23456");

        try (
                Socket aliceSocket = new Socket(hostName, portNumber);

                ObjectOutputStream out = new ObjectOutputStream(aliceSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(aliceSocket.getInputStream());

        ) {
            Scanner input = new Scanner(System.in);
            Object fromKDCServer,fromClient;
            System.out.println("Enter the Client ID: ");
            String clientID = input.nextLine();
            
            
            SecretKey masterKey = null;
            //Sending the Client public key
            fromClient = keys.getPublicKey();
            out.writeObject(fromClient);


            //Receiving Public key of KDC & Sending my ID
            if ((fromKDCServer = in.readObject()) != null) {
                serverPublicKey = (PublicKey) fromKDCServer;
                fromClient = clientID;
                System.out.println("<-Sending ID...");
                out.writeObject(fromClient);
            }//Sent the ID

            if ((fromKDCServer = in.readObject()) != null) {
                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM SERVER: "+Colour.ANSI_RESET);
                System.out.println(Colour.ANSI_RED+"[ENCRYPTED]"+ Colour.ANSI_RESET);
                System.out.println("->"+fromKDCServer);

                //This should be decrypted
                fromKDCServer = RSA.decrypt(keys.getPrivateKey(),(String)fromKDCServer,"RSA/ECB/PKCS1Padding");

                //Let's extract the information from message
                String[] parts =  ((String) fromKDCServer).split(",");
                String kdcID = parts[0];
                String kdcNonce = parts[1];

                System.out.println(Colour.ANSI_CYAN+"[DECRYPTED]"+ Colour.ANSI_RESET);
                System.out.println("->KDC ID: "+kdcID+" KDC Nonce: "+kdcNonce);

                //Reply with Nonce of Client & Nonce of KDC
                int clientNonce = RSA.generateNonce();
                System.out.println("[GENERATED] Client Nonce: "+clientNonce);
                fromClient =  RSA.encrypt(serverPublicKey, clientNonce+","+kdcNonce,"RSA/ECB/PKCS1Padding");
                System.out.println("<-Sending encrypted Client Nonce & KDC Nonce...");
                out.writeObject(fromClient);
            }//Sent the Nonce of Client & Nonce of KDC


            //Received the server Nonce
            if ((fromKDCServer = in.readObject()) != null) {
                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM SERVER: "+Colour.ANSI_RESET);
                System.out.println(Colour.ANSI_RED+"[ENCRYPTED]"+ Colour.ANSI_RESET);
                System.out.println("->"+fromKDCServer);
                //This should be just the server Nonce
                fromKDCServer = RSA.decrypt(keys.getPrivateKey(),(String)fromKDCServer,"RSA/ECB/PKCS1Padding");
                System.out.println(Colour.ANSI_CYAN+"[DECRYPTED]\n"+ Colour.ANSI_RESET+"->KDC Nonce: "+fromKDCServer);

                out.writeObject("Confirming message...");
            }//Sent the Nonce of ClientA & Nonce of KDC

            //Now we should receive a double encrypted message from Server
            if ((fromKDCServer = in.readObject()) != null) {
                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM SERVER: "+Colour.ANSI_RESET);
                System.out.println(Colour.ANSI_RED+"[ENCRYPTEDx2]"+ Colour.ANSI_RESET);
                System.out.println("->"+fromKDCServer);

                //Now will double decrypt
                String longDecrypt = RSA.decryptLongString(keys.getPrivateKey(),(String)fromKDCServer,"RSA/ECB/PKCS1Padding");
                masterKey = KeyGenPair.createMasterKey(RSA.decrypt(serverPublicKey,longDecrypt,"RSA/ECB/PKCS1Padding"));
                System.out.println(Colour.ANSI_CYAN+"[DECRYPTEDx2]"+ Colour.ANSI_RESET);
                System.out.println("->Master Key: "+masterKey);


                System.out.println(Colour.ANSI_PURPLE+"\nBEGIN PHASE 2\n"+Colour.ANSI_RESET);

                // Now ClientB sends its ID to KDCServerThread
                System.out.println("<-Sending IDs to KDCServerThread...");
                out.writeObject(clientID);



            }//Completion of Phase 1

            //Starting Phase 2
            if ((fromKDCServer = in.readObject()) != null) {
                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM SERVER: "+Colour.ANSI_RESET);
                System.out.println(Colour.ANSI_RED+"[ENCRYPTED]"+ Colour.ANSI_RESET);
                System.out.println("->"+fromKDCServer);

                //This should be a key and ID
                fromKDCServer = RSA.decrypt(masterKey,(String)fromKDCServer,"AES");
                String[] parts =  ((String) fromKDCServer).split(",");
                String sharedKey = parts[0];
                String otherClientID = parts[1];

                SecretKey actualSharedKey = KeyGenPair.createSharedKey(sharedKey);

                System.out.println("->Client ID: "+otherClientID);
                System.out.println("->Shared Key: "+actualSharedKey);
                System.out.println(" ");

            }//Phase 2 complete


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
    }// end of main



}

