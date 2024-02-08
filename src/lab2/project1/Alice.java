package lab2.project1;



import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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


                //PrintWriter out = new PrintWriter(aliceSocket.getOutputStream(), true);
                ObjectOutputStream out = new ObjectOutputStream(aliceSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(aliceSocket.getInputStream());
//                BufferedReader in = new BufferedReader(
//                        new InputStreamReader(aliceSocket.getInputStream()));
        ) {
//            BufferedReader stdIn =
//                    new BufferedReader(new InputStreamReader(System.in));
            Object fromBob;
            Object fromAlice;

            System.out.println("The Key for Alice: "+key.toString());
            int aliceNonce = Helper.getNonce();
            //System.out.println("Alice Nonce: "+aliceNonce);

            //Output line is encrypted
            fromAlice =  new NonceID(aliceNonce,id) ;

            out.writeObject(fromAlice);

            while ((fromBob = in.readObject()) != null) {
                //Decrypted message from the Server
//                System.out.println("(ENCRYPT) Bob: " + fromBob);
//                fromBob = Cipher.decryption(fromBob);



                if (fromBob instanceof Message){
                    Cipher cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.DECRYPT_MODE, key);
                    byte[] objBytes = cipher.doFinal(((Message) fromBob).getInformation());

                    fromBob = Helper.convertToEMessage(objBytes);



                }

                System.out.println("(RECV) Bob: " + fromBob);
//                if (fromBob.equalsIgnoreCase("Bye."))
//                    break;

                out.writeObject(fromAlice); ;

            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        } catch (ClassNotFoundException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
           e.printStackTrace();
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

}
