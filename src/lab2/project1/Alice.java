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

    private static final String id = "Alice";
    private static final SecretKey key = KeyGen.getInstance().getKey();
    public static void main(String[] args) throws IOException {

//        if (args.length != 2) {
//            System.err.println(
//                    "Usage: java EchoClient <host name> <port number>");
//            System.exit(1);
//        }

        String hostName = "localhost";
        int portNumber = Integer.parseInt("23456");

        try (
                Socket aliceSocket = new Socket(hostName, portNumber);

                ObjectOutputStream out = new ObjectOutputStream(aliceSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(aliceSocket.getInputStream());

        ) {

            Object fromBob,fromAlice;

            int aliceNonce = Helper.getNonce();
            System.out.println("(GENERATED) Alice's Nonce: "+aliceNonce);

            //Output line is encrypted
            fromAlice =  new NonceID(aliceNonce,id) ;

            out.writeObject(fromAlice);

            if ((fromBob = in.readObject()) != null) {

                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM BOB: "+Colour.ANSI_RESET);
                if (fromBob instanceof Message){

                    System.out.println("Nonce from Bob: "+((Message) fromBob).getNonce());
                    System.out.println(Colour.ANSI_RED+"-ENCRYPTED-"+Colour.ANSI_RESET);
                    System.out.println( ((Message) fromBob).getMsg());


                    //fromBob will now be a decrypted Message Object
                    Message enBobFinal = Helper.decrypt(key,((Message) fromBob).getMsg());
                    System.out.println(Colour.ANSI_CYAN+"-DECRYPTED-\n"+ Colour.ANSI_RESET+enBobFinal);

                    //Now Alice will send Message back
                    fromAlice = Helper.encrypt(key,new NonceID(((Message) fromBob).getNonce(),id));

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
