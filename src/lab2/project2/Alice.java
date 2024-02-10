package lab2.project2;



import lab2.Colour;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PublicKey;


/*
* Alice is the Client
* */
public class Alice {

    private static KeyGenPair keyGenPair;
    private static PublicKey bobPublicKey;
    public static void main(String[] args) throws IOException {


        String hostName = "localhost";
        int portNumber = Integer.parseInt("23456");
        keyGenPair = new KeyGenPair();
        try (
                Socket aliceSocket = new Socket(hostName, portNumber);

                ObjectOutputStream out = new ObjectOutputStream(aliceSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(aliceSocket.getInputStream());

        ) {

            Object fromBob,fromAlice;

            int aliceNonce = RSA.generateNonce();
            System.out.println("(GENERATED) Alice's Nonce: "+aliceNonce);



            //Output line is encrypted
            fromAlice = keyGenPair.getPublicKey();

            out.writeObject(fromAlice);

            while ((fromBob = in.readObject()) != null) {

                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM BOB: "+Colour.ANSI_RESET);
                if (fromBob instanceof PublicKey){
                    bobPublicKey = (PublicKey) fromBob;
                    System.out.println("Public Key received");

                    //Alice sends her Nonce
                    fromAlice = new NonceID(aliceNonce,"Alice");
                }

                else if (fromBob instanceof String){

                    //DECRYPTION PROCESS
                    System.out.println(Colour.ANSI_RED+"-ENCRYPTED-"+Colour.ANSI_RESET);
                    System.out.println(fromBob);

                    int nonceFromBob = Integer.parseInt(fromBob.toString().substring(0,6));

                    fromBob = fromBob.toString().substring(6);

                    //fromBob will now be a decrypted Message Object
                    String decryptPub = RSA.decryptLongString(keyGenPair.getPrivateKey(),(String)fromBob);
                    String decryptPrv = RSA.decrypt(bobPublicKey, decryptPub);
                    System.out.println(Colour.ANSI_CYAN+"-DECRYPTED-"+ Colour.ANSI_RESET);
                    System.out.println("Nonce from Bob: "+  nonceFromBob);
                    System.out.println("My Decrypted Nonce: "+decryptPrv);

                    //Now Alice must encrypt
                    String prvEncrypt = RSA.encrypt(keyGenPair.getPrivateKey(),String.valueOf(nonceFromBob));
                    fromAlice = RSA.encryptLongString(bobPublicKey,prvEncrypt);


                    out.writeObject(fromAlice);
                    break;
                }

                out.writeObject(fromAlice);


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
