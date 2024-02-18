package lab3.phase1;

import lab3.Colour;
import lab3.KeyGenPair;
import lab3.RSA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;

public class KDCServerThread extends  Thread{
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



            //Exchange the public keys privately
            if ((inputLine = in.readObject()) != null) {

//                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM USER: "+Colour.ANSI_RESET);
//                System.out.println("Got: "+inputLine);
                //Client key has been set
                clientPublicKey = (PublicKey) inputLine;
                //We must send the server public key
                outputLine = keys.getPublicKey();
                out.writeObject(outputLine);

            }//End of key exchange


            //Server will receive the client ID
            if ((inputLine = in.readObject()) != null) {

                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM USER: "+Colour.ANSI_RESET);
                System.out.println("->Client ID: "+inputLine);

                //Now reply with KDC Nonce & KDC ID
                int kdcNonce = RSA.generateNonce();
                System.out.println("[GENERATED] KDC Nonce: "+kdcNonce);
                String message = "KDCServer,"+kdcNonce;
                outputLine = RSA.encrypt(clientPublicKey,message);
                System.out.println("<-Sending encrypted ID & Nonce...");

                out.writeObject(outputLine);

            }//end of sending KDC_Id and KDC_Nonce


            //Server will receive the Nonce of Client & Nonce of Server
            if ((inputLine = in.readObject()) != null) {

                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM USER: "+Colour.ANSI_RESET);
                System.out.println(lab2.Colour.ANSI_RED+"[ENCRYPTED]"+ lab2.Colour.ANSI_RESET);
                System.out.println("->"+inputLine);

                //This should be decrypted
                inputLine = RSA.decrypt(keys.getPrivateKey(),(String)inputLine);


                //Let's extract the information from message
                String[] parts =  ((String) inputLine).split(",");
                String clientNonce = parts[0];
                String kdcNonce = parts[1];

                System.out.println(lab2.Colour.ANSI_CYAN+"[DECRYPTED]"+ lab2.Colour.ANSI_RESET);
                System.out.println("->Client Nonce: "+clientNonce+" KDC Nonce: "+kdcNonce);

                //Let's reply back with KDC Nonce
                outputLine = RSA.encrypt(clientPublicKey,kdcNonce);
                out.writeObject(outputLine);
                System.out.println("<-Sending encrypted KDC Nonce...");



            }//end of sending just the Server Nonce

            //Server will receive invisible confirm
            if ((inputLine = in.readObject()) != null) {

//                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM USER: "+Colour.ANSI_RESET);
//                System.out.println("Got the Confirm: "+inputLine);

                //Now we should also send the master key
                String masterKey = RSA.generateMasterKeyString();

                String encryptMasterKey = RSA.encrypt(keys.getPrivateKey(),masterKey);
                outputLine = RSA.encryptLongString(clientPublicKey,encryptMasterKey);

                out.writeObject(outputLine);
                System.out.println("<-Sending the Master Key...");


            }//end sending the master key
            clientSocket.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
