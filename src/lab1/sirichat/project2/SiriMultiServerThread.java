package lab1.sirichat.project2;



import lab1.sirichat.Cipher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SiriMultiServerThread extends Thread{

    private Socket socket = null;

    public SiriMultiServerThread(Socket socket) {
        super("SiriMultiServerThread");
        this.socket = socket;
    }

    public void run() {

        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        ) {
            String inputLine, outputLine;
            SiriProtocol siriP = new SiriProtocol();
            outputLine = Cipher.encryption(siriP.processInput(null));
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) {
                //Decryption Server Side
                System.out.println("(ENCRYPT) Client: "+inputLine);
                inputLine = Cipher.decryption(inputLine);
                System.out.println("(DECRYPT) Client: "+inputLine);
                System.out.println("\t\t-----------------------------");
                outputLine = siriP.processInput(inputLine);
                out.println(Cipher.encryption(outputLine));
                if (outputLine.equals("Bye"))
                    break;
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
