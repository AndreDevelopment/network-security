// Alice is the client
package lab2.project3;
import lab2.Colour;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;

public class Alice {
    private static KeyGenPair keyGenPair;
    private static PublicKey alicePublicKey;
    private static PrivateKey alicePrivateKey;

    public static void main(String[] args) {
        String hostname = "localhost";
        int portNumber = 23456;

        keyGenPair = new KeyGenPair();
        // The public key below will be sent to bob, so that he can verify.
        alicePrivateKey = keyGenPair.getPrivateKey();
        alicePublicKey = keyGenPair.getPublicKey();

        try (Socket aliceSocket = new Socket(hostname, portNumber);
             ObjectOutputStream out = new ObjectOutputStream(aliceSocket.getOutputStream())) {

            // Sending Alice's PUBLIC key to Bob.
            out.writeObject(alicePublicKey);

            // Message M --> will be sent along with signature to Bob.
            String message = "Hey, Nigga Booby!";

            // Signature --> Sign(M)
            // Will be a byte sized array again :(
            byte[] signature = sign(message, alicePrivateKey);

            System.out.println(Colour.ANSI_PURPLE +"Alice: Sent Message: " + message + Colour.ANSI_RESET);
            System.out.println(Colour.ANSI_GREEN + "Alice: Sent Signature: " + bytesToHex(signature) + Colour.ANSI_RESET);

            // Message With Signature
            MessageWithSignature messageWithSignature = new MessageWithSignature(message, signature);
            out.writeObject(messageWithSignature);

        } catch (UnknownHostException e) {
            System.err.println("Can't find host.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection.");
            System.exit(1);
        }
    }

    // signature
    private static byte[] sign(String message, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes());
            return signature.sign();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
