package lab2.project2;


import lab2.Colour;



import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;


/*
* Bob will act as the server
* */
public class Bob {
    private static KeyGenPair keyGenPair;
    private static PublicKey alicePublicKey;
    public static void main(String[] args) throws IOException {

        int portNumber = Integer.parseInt("23456");
        keyGenPair = new KeyGenPair();
        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

        ) {

            Object inputLine, outputLine=new Message("No object");

            int nonceBob = Helper.getNonce();
            System.out.println("(GENERATED) Bob's Nonce: "+nonceBob);

            //BEGIN SERVER WHILE
            while ((inputLine = in.readObject()) != null) {

                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM ALICE: "+Colour.ANSI_RESET);

                if (inputLine instanceof PrivateKey){
                    alicePublicKey = (PublicKey) inputLine;
                    System.out.println("Public Key received");

                    outputLine = keyGenPair.getPublicKey();
                }
                else if (inputLine instanceof NonceID){
                    System.out.println(inputLine);

                    //Step 1 - Encrypt private key and nonce
                    //Object containing the Private Key & Alice's Nonce
                    Message innerPrKN = new Message(keyGenPair.getPrivateKey(), ((NonceID) inputLine).getNonce());
                    //This string is the encrypted version of innerPrKN
                    String encryptedPrKN = Helper.encrypt(alicePublicKey,innerPrKN).getMsg();

                    //Step 2 - Encrypt the Public key with the inner encrypted obj
                    //Object containing the necessary information
                    Message outerPuK = new Message(alicePublicKey,encryptedPrKN);
                    //Now I encrypt outerPuK, package it in a Message and add a Nonce and send it off
                    Message temp = Helper.encrypt(alicePublicKey,outerPuK);
                    temp.setNonce(nonceBob);
                    outputLine = temp;


                } else if (inputLine instanceof Message) {
                    //DECRYPTION PROCESS
               
                    //The initial encrypted Object
                    System.out.println(Colour.ANSI_RED+"-ENCRYPTED OUTER-"+Colour.ANSI_RESET);
                    System.out.println( ((Message) inputLine).getMsg());

                    //outer object decrypted
                    Message outerAlice = Helper.decrypt(keyGenPair.getPrivateKey(),((Message) inputLine).getMsg());
                    System.out.println(Colour.ANSI_CYAN+"-DECRYPTED OUTER-\n"+ Colour.ANSI_RESET+outerAlice);

                    //Now decrypt that inner object
                    Message innerAlice = Helper.decrypt(keyGenPair.getPrivateKey(), outerAlice.getMsg());
                    System.out.println(Colour.ANSI_CYAN+"\t\t-DECRYPTED INNER-\n"+ Colour.ANSI_RESET+innerAlice);

                    break;

                }

                out.writeObject(outputLine);

            }//end of Server while
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
           e.printStackTrace();
        }
    }
}
