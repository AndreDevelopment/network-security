package lab3.phase1;


import lab2.project2.RSA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PublicKey;

public class Client {

    public static void main(String[] args) throws IOException {


        String hostName = "localhost";
        int portNumber = Integer.parseInt("23456");

        try (
                Socket aliceSocket = new Socket(hostName, portNumber);

                ObjectOutputStream out = new ObjectOutputStream(aliceSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(aliceSocket.getInputStream());

        ) {

            Object fromBob,fromAlice="";



            while ((fromBob = in.readObject()) != null) {




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
