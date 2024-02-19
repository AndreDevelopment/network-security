package lab3.phase2;

import lab3.Colour;
import lab3.KeyGenPair;
import lab3.RSA;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.Scanner;

public class ClientA {

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

            Object fromKDCServer,fromClientA;
            Scanner scan = new Scanner(System.in);
            System.out.println("Enter the ClientA ID: ");
            String ClientAID = scan.nextLine();

            //Sending the ClientA public key
            fromClientA = keys.getPublicKey();
            out.writeObject(fromClientA);


            //Receiving Public key of KDC & Sending my ID
            if ((fromKDCServer = in.readObject()) != null) {
//                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM SERVER: "+Colour.ANSI_RESET);
//                System.out.println("Got the key:  "+fromKDCServer);

                serverPublicKey = (PublicKey) fromKDCServer;
                fromClientA = ClientAID;
                System.out.println("<-Sending ID...");
                out.writeObject(fromClientA);
            }//Sent the ID

            if ((fromKDCServer = in.readObject()) != null) {
                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM SERVER: "+Colour.ANSI_RESET);
                System.out.println(lab2.Colour.ANSI_RED+"[ENCRYPTED]"+ Colour.ANSI_RESET);
                System.out.println("->"+fromKDCServer);

                //This should be decrypted
                fromKDCServer = RSA.decrypt(keys.getPrivateKey(),(String)fromKDCServer);

                //Let's extract the information from message
                String[] parts =  ((String) fromKDCServer).split(",");
                String kdcID = parts[0];
                String kdcNonce = parts[1];

                System.out.println(lab2.Colour.ANSI_CYAN+"[DECRYPTED]"+ Colour.ANSI_RESET);
                System.out.println("->KDC ID: "+kdcID+" KDC Nonce: "+kdcNonce);

                //Reply back with Nonce of ClientA & Nonce of KDC
                int ClientANonce = RSA.generateNonce();
                System.out.println("[GENERATED] ClientA Nonce: "+ClientANonce);
                fromClientA =  RSA.encrypt(serverPublicKey, ClientANonce+","+kdcNonce);
                System.out.println("<-Sending encrypted ClientA Nonce & KDC Nonce...");
                out.writeObject(fromClientA);
            }//Sent the Nonce of ClientA & Nonce of KDC


            //Received the server Nonce
            if ((fromKDCServer = in.readObject()) != null) {
                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM SERVER: "+Colour.ANSI_RESET);
                System.out.println(lab2.Colour.ANSI_RED+"[ENCRYPTED]"+ lab2.Colour.ANSI_RESET);
                System.out.println("->"+fromKDCServer);
                //This should be just the server Nonce
                fromKDCServer = RSA.decrypt(keys.getPrivateKey(),(String)fromKDCServer);
                System.out.println(lab2.Colour.ANSI_CYAN+"[DECRYPTED]\n"+ Colour.ANSI_RESET+"->KDC Nonce: "+fromKDCServer);

                out.writeObject("Confirming message...");
            }//Sent the Nonce of ClientA & Nonce of KDC

            //Now we should receive a double encrypted message from Server
            if ((fromKDCServer = in.readObject()) != null) {
                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM SERVER: "+Colour.ANSI_RESET);
                System.out.println(lab2.Colour.ANSI_RED+"[ENCRYPTEDx2]"+ Colour.ANSI_RESET);
                System.out.println("->"+fromKDCServer);

                //Now will double decrypt
                String longDecrypt = RSA.decryptLongString(keys.getPrivateKey(),(String)fromKDCServer);
                SecretKey masterKey = KeyGenPair.createMasterKey(RSA.decrypt(serverPublicKey,longDecrypt));
                System.out.println(lab2.Colour.ANSI_CYAN+"[DECRYPTEDx2]"+ lab2.Colour.ANSI_RESET);
                System.out.println("->Master Key: "+masterKey);


            }//Completion of Phase 1

            // Receiving the ClientB ID from KDCServerThread
            if ((fromKDCServer = in.readObject()) != null) {
                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM KDCServerThread: "+Colour.ANSI_RESET);
                System.out.println("->ClientB ID: "+fromKDCServer);
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

