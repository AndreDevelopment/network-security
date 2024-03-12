package lab4.kdc;

import lab4.Colour;
import lab4.KeyGenPair;
import lab4.RSA;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class KDCServerThread extends Thread {
    private KeyGenPair keys;
    private  PublicKey clientPublicKey;
    private Socket clientSocket;
    private String clientID;

    private static CountDownLatch latch;

    private static List<KDCServerThread> clientList;

    public KDCServerThread(Socket socket) {
        clientSocket = socket;
        if (clientList==null)
            clientList = new ArrayList<>();
        if (latch==null)
            latch = new CountDownLatch(2);
        clientList.add(this);
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
                clientID = (String)inputLine;
                //Add to the list and notify other threads of the update
//                synchronized (userIds) {
//                    notifyAll(); // Notify all waiting threads (consumer)
//                }


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
            }// Receive ID from ClientB, forward it to ClientA

            //Need to wait until both users are done phase 1 to begin phase 2
            latch.countDown();
            latch.await();

            System.out.println(Colour.ANSI_PURPLE+"\nBEGIN PHASE 2\n"+ Colour.ANSI_RESET);


            if ((inputLine = in.readObject()) != null) {
                System.out.println(Colour.ANSI_GREEN + "RECEIVED FROM CLIENT: " + Colour.ANSI_RESET);

                String otherClientID="rand",clientGeneralID = (String) inputLine;

                //This code will search for the opposing users ID provided we only have 2 users

                System.out.println("->ClientA ID: " + clientGeneralID);
                for (KDCServerThread client: clientList) {
                    if (!client.getClientId().equals(clientGeneralID))
                        otherClientID=client.getClientId();
                }
                System.out.println("->ClientB ID: " + otherClientID);


                //Setting up Ks
                String keyS = "thisismysecretkey24bytes";
                String msg = keyS + "," + otherClientID;
                inputLine = RSA.encrypt(KeyGenPair.createMasterKey(masterKey), msg, "AES");

                out.writeObject(inputLine);
                System.out.println("<-Sending Encrypted Keys...");
                System.out.println(Colour.ANSI_BLUE + "-------------------------------------------" + Colour.ANSI_RESET);
            }



            clientSocket.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }// end of main
    public String getClientId(){
        return clientID;
    }

}
