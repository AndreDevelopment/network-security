// Bob class
package lab2.project3;

import lab2.Colour;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;

public class Bob {
    private static KeyGenPair keyGenPair;
    private static PublicKey alicePublicKey;

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        int portNumber = 23456;
        keyGenPair = new KeyGenPair();

        try (ServerSocket serverSocket = new ServerSocket(portNumber);
             Socket clientSocket = serverSocket.accept();
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            // Alice's public key
            Object inputObject = in.readObject();
            if (inputObject instanceof PublicKey) {
                alicePublicKey = (PublicKey) inputObject;
                System.out.println(Colour.ANSI_BLUE + "Alice --> Bob: Received Alice's Public Key [Key Info Below]: " + alicePublicKey + Colour.ANSI_RESET);
            } else {
                System.out.println(Colour.ANSI_RED + "Bob: Error receiving Alice's Public Key." + Colour.ANSI_RESET);
                System.exit(1);
            }

            Object receivedObject;

            while ((receivedObject = in.readObject()) != null) {

                // Only true if rcvd object is an instance of MsgWithSig.

                if (receivedObject instanceof MessageWithSignature) {
                    MessageWithSignature receivedMessage = (MessageWithSignature) receivedObject;

                    String message = receivedMessage.getMessage();
                    byte[] signature = receivedMessage.getSignature();
                    long timestamp = receivedMessage.getTimestamp();

                    System.out.println(Colour.ANSI_PURPLE + "Alice --> Bob: Received Message: " + message + Colour.ANSI_RESET);
                    System.out.println(Colour.ANSI_GREEN + "Alice --> Bob: Received Signature: " + Helper.bytesToHex(signature) + Colour.ANSI_RESET);
                    System.out.println(Colour.ANSI_CYAN + "Alice --> Bob: Received Timestamp: " + timestamp + Colour.ANSI_RESET);

                    // max time allowed to be elapsed --> 5 sec
                    if (Helper.verifyTimestamp(timestamp, 5000)) {
                        // Verifying the signature using Alice's public key
                        if (Helper.verifySignature(message, signature, alicePublicKey)) {
                            System.out.println(Colour.ANSI_GREEN + "Alice --> Bob: Signature verification successful." + Colour.ANSI_RESET);
                            System.out.println(Colour.ANSI_GREEN + "Alice --> Bob: Received Message Verified: " + message + Colour.ANSI_RESET);
                            System.out.println(Colour.ANSI_GREEN + "No attack detected, message accepted!" + Colour.ANSI_RESET);
                            break;
                        } else {
                            System.out.println(Colour.ANSI_RED + "Bob: Signature verification failed." + Colour.ANSI_RESET);
                        }

                    } else {
                        System.out.println(Colour.ANSI_RED + "Bob: Replay attack detected. Ignoring the message." + Colour.ANSI_RESET);
                        break;
                    }
                }//end of instance of MessageSig
            }//end of while
        }//end of try
    }//main method


}


