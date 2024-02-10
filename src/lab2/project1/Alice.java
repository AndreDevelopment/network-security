package lab2.project1;



import lab2.Colour;

import javax.crypto.*;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;


/*
* Alice is the Client
* */
public class Alice {


    private static final SecretKey key = KeyGen.getInstance().getKey();
    public static void main(String[] args) throws IOException {


        String hostName = "localhost";
        int portNumber = Integer.parseInt("23456");

        try (
                Socket aliceSocket = new Socket(hostName, portNumber);

                ObjectOutputStream out = new ObjectOutputStream(aliceSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(aliceSocket.getInputStream());

        ) {

            Object fromBob,fromAlice;

            int aliceNonce = AES.generateNonce();
            System.out.println("(GENERATED) Alice's Nonce: "+aliceNonce);

            //Output line is encrypted
            fromAlice =  new NonceID(aliceNonce,"Alice") ;

            out.writeObject(fromAlice);

            if ((fromBob = in.readObject()) != null) {

                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM BOB: "+Colour.ANSI_RESET);
                if (fromBob instanceof String){

                    System.out.println(Colour.ANSI_RED+"-ENCRYPTED-"+Colour.ANSI_RESET);
                    System.out.println(fromBob);

                    int nonceFromBob = Integer.parseInt(fromBob.toString().substring(0,6));

                    fromBob = fromBob.toString().substring(6);
                    //fromBob will now be a decrypted Message Object
                    NonceID enBobFinal = AES.decrypt(key, (String) fromBob);
                    System.out.println(Colour.ANSI_CYAN+"-DECRYPTED-"+ Colour.ANSI_RESET);
                    System.out.println("Nonce from Bob: "+  nonceFromBob);
                    System.out.println(enBobFinal);

                    //Now Alice will send Message back
                    fromAlice = AES.encrypt(key,new NonceID(nonceFromBob,"Alice"));

                }


                out.writeObject(fromAlice); ;

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
