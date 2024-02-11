// Bob
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


                    System.out.println(Colour.ANSI_PURPLE + "ALice --> Bob: Received Message: " + message + Colour.ANSI_RESET);
                    System.out.println(Colour.ANSI_GREEN + "Alice --> Bob: Received Signature: " + bytesToHex(signature) + Colour.ANSI_RESET);

                    // Verifying the signature using Alice's public key
                    if (verifySignature(message, signature, alicePublicKey)) {
                        //System.out.println("Bob: Signature verification successful.");
                        System.out.println(Colour.ANSI_GREEN + "Alice --> Bob: Signature verification successful." + Colour.ANSI_RESET);
                        System.out.println(Colour.ANSI_GREEN + "Alice --> Bob: Received Message Verified: " + message + Colour.ANSI_RESET);
                    } else {
                        System.out.println("Bob: Signature verification failed.");
                    }
                    break;
                }
            }
        }
    }

    // Signature verification
    private static boolean verifySignature(String message, byte[] signature, PublicKey publicKey) {
        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(publicKey);
            sign.update(message.getBytes());
            return sign.verify(signature);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // convert bytes to hexadecimal string
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }
}
