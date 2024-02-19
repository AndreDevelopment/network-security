package lab3.phase2;

import lab3.Colour;
import lab3.KeyGenPair;
import lab3.RSA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;

public class KDCServerThread extends Thread {
    private static KeyGenPair keys;
    private static PublicKey clientPublicKey;

    private Socket clientSocket = null;

    public KDCServerThread(Socket socket) {
        clientSocket = socket;
    }

    @Override
    public void run() {
        keys = new KeyGenPair();

        try (
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        ) {

            Object inputLine, outputLine;
            String masterKey = "";

            // Key exchange
            if ((inputLine = in.readObject()) != null) {
                clientPublicKey = (PublicKey) inputLine;
                outputLine = keys.getPublicKey();
                out.writeObject(outputLine);
            }

            // Receive client ID and send KDC Nonce & KDC ID
            if ((inputLine = in.readObject()) != null) {
                System.out.println(Colour.ANSI_GREEN + "RECEIVED FROM USER: " + Colour.ANSI_RESET);
                System.out.println("->Client ID: " + inputLine);

                int kdcNonce = RSA.generateNonce();
                System.out.println("[GENERATED] KDC Nonce: " + kdcNonce);
                String message = "KDCServer," + kdcNonce;
                outputLine = RSA.encrypt(clientPublicKey, message, "RSA/ECB/PKCS1Padding");
                System.out.println("<-Sending encrypted ID & Nonce...");
                out.writeObject(outputLine);
            }

            // Receive Nonce of Client & Nonce of Server, reply with KDC Nonce
            if ((inputLine = in.readObject()) != null) {
                System.out.println(Colour.ANSI_GREEN + "RECEIVED FROM USER: " + Colour.ANSI_RESET);
                System.out.println(Colour.ANSI_RED + "[ENCRYPTED]" + Colour.ANSI_RESET);
                System.out.println("->" + inputLine);

                inputLine = RSA.decrypt(keys.getPrivateKey(), (String) inputLine, "RSA/ECB/PKCS1Padding");

                String[] parts = ((String) inputLine).split(",");
                String clientNonce = parts[0];
                String kdcNonce = parts[1];

                System.out.println(Colour.ANSI_CYAN + "[DECRYPTED]" + Colour.ANSI_RESET);
                System.out.println("->Client Nonce: " + clientNonce + " KDC Nonce: " + kdcNonce);

                outputLine = RSA.encrypt(clientPublicKey, kdcNonce, "RSA/ECB/PKCS1Padding");
                out.writeObject(outputLine);
                System.out.println("<-Sending encrypted KDC Nonce...");
            }

            // Receive invisible confirm, send the master key
            if ((inputLine = in.readObject()) != null) {
                masterKey = RSA.generateMasterKeyString();
                String encryptMasterKey = RSA.encrypt(keys.getPrivateKey(), masterKey, "RSA/ECB/PKCS1Padding");
                outputLine = RSA.encryptLongString(clientPublicKey, encryptMasterKey, "RSA/ECB/PKCS1Padding");
                out.writeObject(outputLine);
                System.out.println("<-Sending the Master Key...");
            }

            // Receive ID from ClientB, forward it to ClientA
            if ((inputLine = in.readObject()) != null) {
                System.out.println(Colour.ANSI_GREEN + "RECEIVED FROM CLIENTB: " + Colour.ANSI_RESET);

                String clientGeneralID = (String) inputLine;

                System.out.println("->ClientA ID: " + "Alice");
                System.out.println("->ClientB ID: " + "Bob");

                String keyAB = "thisismysecretkey24bytes";
                String msg = keyAB + "," + clientGeneralID;
                inputLine = RSA.encrypt(KeyGenPair.createMasterKey(masterKey), msg, "AES");

                out.writeObject(inputLine);
                System.out.println("<-Sending Encrypted Keys...");
                System.out.println(Colour.ANSI_BLUE + "-------------------------------------------" + Colour.ANSI_RESET);
            }

            clientSocket.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
