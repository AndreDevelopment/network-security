package lab2.project1;



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

            Object fromBob;
            Object fromAlice;


            int aliceNonce = Helper.getNonce();

            //Output line is encrypted
            fromAlice =  new NonceID(aliceNonce,id) ;

            out.writeObject(fromAlice);

            while ((fromBob = in.readObject()) != null) {

                if (fromBob instanceof Message){

                    System.out.println("Nonce from Bob: "+((Message) fromBob).getNonce());
                    System.out.println("The encrypted object is: ");
                    for (byte b: ((Message) fromBob).getInformation()){
                        System.out.print(b+" ");
                    }
                    System.out.println();


                    //fromBob will now be a decrypted EMessage Object
                    Message enBobFinal = Helper.decrypt(key,((Message) fromBob).getInformation());

                    System.out.println("The actual Object: "+enBobFinal);


                    //Now Alice will send EMessage back
                    fromAlice = Helper.encrypt(key,new NonceID(((Message) fromBob).getNonce(),id));

                }


                out.writeObject(fromAlice); ;
                break;
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
