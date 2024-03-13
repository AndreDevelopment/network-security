package lab3.chatroom;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private  String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything();
        }
    }

    public void sendMessage() {

        try {
            bw.write(username);
            bw.newLine();
            bw.flush();

            Scanner scan = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scan.nextLine();
                bw.write(messageToSend);
                bw.newLine();
                bw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything();
        }

    }

    public void listenForMessage() {

        new Thread(() -> {

            try {
                String messageFromGC;
                while (socket.isConnected()) {
                    messageFromGC = br.readLine();
                    if (messageFromGC == null) {
                        break;
                    }
                    System.out.println(messageFromGC);
                }//end of while
            } catch (IOException e) {
                e.printStackTrace();
                closeEverything();
            }
        }).start();


    }

    public void closeEverything() {
        try {
            if (bw != null)
                bw.close();
            if (br != null)
                br.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }//end of closeEverything

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String newUser = scanner.nextLine();

        Socket socket = new Socket("localhost",23456);
        Client client = new Client(socket,newUser);
        client.listenForMessage();
        client.sendMessage();
    }
}