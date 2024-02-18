package lab3.phase1;



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

            Object fromBob,fromAlice="No object";


            //Sending the Client public key
            fromAlice = keys.getPublicKey();
            out.writeObject(fromAlice);


            //Receiving Public key of KDC & Sending my ID
            if ((fromBob = in.readObject()) != null) {
                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM SERVER: "+Colour.ANSI_RESET);
                System.out.println("Got the key:  "+fromBob);

                serverPublicKey = (PublicKey) fromBob;

                fromAlice = "Ishan";
                out.writeObject(fromAlice);
            }//Sent the ID

            if ((fromBob = in.readObject()) != null) {
                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM SERVER: "+Colour.ANSI_RESET);
                System.out.println("Received encrpyted message: "+fromBob);

                //This should be decrypted
                fromBob = RSA.decrypt(keys.getPrivateKey(),(String)fromBob);
                System.out.println("Decrypted Message from Server: "+fromBob);

                //Let's extract the information from message
                String[] parts =  ((String) fromBob).split(",");
                String kdcNonce = parts[1];

                //Reply back with Nonce of client & Nonce of KDC
                int clientNonce = RSA.generateNonce();
                fromAlice =  RSA.encrypt(serverPublicKey, clientNonce+","+kdcNonce);

                out.writeObject(fromAlice);
            }//Sent the Nonce of client & Nonce of KDC


            //Received the server Nonce
            if ((fromBob = in.readObject()) != null) {
                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM SERVER: "+Colour.ANSI_RESET);
                System.out.println("Received encrpyted message: "+fromBob);

                //This should be just the server Nonce
                fromBob = RSA.decrypt(keys.getPrivateKey(),(String)fromBob);
                System.out.println("Decrypted Message from Server Nonce: "+fromBob);

                out.writeObject("Confirming message...");
            }//Sent the Nonce of client & Nonce of KDC

            //Now we should receive a double encrypted message from Server
            if ((fromBob = in.readObject()) != null) {
                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM SERVER: "+Colour.ANSI_RESET);
                System.out.println("Received double encrpyted message: "+fromBob);

                //Now will double decrypt
                String longDecrypt = RSA.decryptLongString(keys.getPrivateKey(),(String)fromBob);
                SecretKey masterKey = KeyGenPair.createMasterKey(RSA.decrypt(serverPublicKey,longDecrypt));

                System.out.println("Got the master key: ");
                System.out.println(masterKey);

            }//Completion of Phase 1


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
