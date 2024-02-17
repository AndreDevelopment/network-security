package lab3.phase1;




import lab3.Colour;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class KDCServer {

    public static void main(String[] args) throws IOException {

        int portNumber = Integer.parseInt("23456");

        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

        ) {

            Object inputLine, outputLine="No object";



            //BEGIN SERVER WHILE
            while ((inputLine = in.readObject()) != null) {

                System.out.println(Colour.ANSI_GREEN+"RECEIVED FROM USER: "+Colour.ANSI_RESET);



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
