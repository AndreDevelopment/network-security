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
        alicePrivateKey = keyGenPair.getPrivateKey();
        alicePublicKey = keyGenPair.getPublicKey();

        try (Socket aliceSocket = new Socket(hostname, portNumber);
             ObjectOutputStream out = new ObjectOutputStream(aliceSocket.getOutputStream())) {

            // Sending Alice's PUBLIC key to Bob.
            out.writeObject(alicePublicKey);

            // Message M --> will be sent along with signature to Bob.
            String message = "Hey Bob, trying to send a message.!";

            // Signature --> Sign(M)
            byte[] signature = Helper.sign(message, alicePrivateKey);

            // we will send current time in milli-second to bob.
            long timestamp = System.currentTimeMillis();

            System.out.println(Colour.ANSI_PURPLE +"Alice: Sent Message: " + message + Colour.ANSI_RESET);
            System.out.println(Colour.ANSI_GREEN + "Alice: Sent Signature: " + Helper.bytesToHex(signature) + Colour.ANSI_RESET);
            System.out.println(Colour.ANSI_CYAN + "Alice: Sent Timestamp: " + timestamp + Colour.ANSI_RESET);

            // Message With Signature and Timestamp
            MessageWithSignature messageWithSignature = new MessageWithSignature(message, signature, timestamp);
//            try {
//                // Pause for 6 seconds
//                Thread.sleep(6000);
//            } catch (InterruptedException e) {
//                // Handle exception
//                e.printStackTrace();
//            }
            out.writeObject(messageWithSignature);

        } catch (UnknownHostException e) {
            System.err.println("Can't find host.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection.");
            System.exit(1);
        }
    }


}
