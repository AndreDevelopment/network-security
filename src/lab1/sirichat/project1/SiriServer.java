package lab1.sirichat.project1;



import lab1.sirichat.Cipher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SiriServer {
    public static void main(String[] args) throws IOException {

//        if (args.length != 1) {
//            System.err.println("Usage: java KnockKnockServer <port number>");
//            System.exit(1);
//        }

        int portNumber = Integer.parseInt("23456");

        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
        ) {

            String inputLine, outputLine;

            // Initiate conversation with client
            SiriProtocol siriP = new SiriProtocol();
            //Output line is encrypted
            outputLine =  Cipher.encryption(siriP.processInput(null)) ;

            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) {

                //Decrypt Server side
                System.out.println("(ENCRYPT) Client: "+inputLine);
                inputLine = Cipher.decryption(inputLine);
                System.out.println("(DECRYPT) Client: "+inputLine);
                System.out.println("\t\t-----------------------------");
                outputLine =  siriP.processInput( inputLine);
                //Encrypt Server side
                out.println(Cipher.encryption(outputLine));
                if (outputLine.equalsIgnoreCase("Bye."))
                    break;
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
