package lab3.chatroom;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private String username;

    public ClientHandler(Socket socket) throws IOException {

        try {
            this.socket = socket;
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = br.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: "+username + " has entered the chat!");
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything();
        }

    }

    @Override
    public void run() {
        String message;
        while (socket.isConnected()) {
            try {
                message = br.readLine();

                broadcastMessage(username+": "+ message);

            } catch (IOException e) {
                e.printStackTrace();

                closeEverything();
                break;

            }
        }



    }

    public void closeEverything() {
        removeClientHandler();
        try {
            if(bw!=null)
                bw.close();
            if(br!=null)
                br.close();
            if(socket!=null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void broadcastMessage(String message) {
        for (ClientHandler client : clientHandlers) {
            try {
                if (!client.username.equals(username)) {
                    client.bw.write(message);
                    client.bw.newLine();
                    client.bw.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                closeEverything();

            }
        }//end of for
    }//end of broadcast

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + username+" has left the chat!");
    }
}