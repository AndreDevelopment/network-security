package lab4.chat;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private String username;
 // sending objs 
    private ObjectOutputStream out ;
	private ObjectInputStream in;

    public ClientHandler(Socket socket) throws IOException {

        try {
            this.socket = socket;
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());
            this.username = br.readLine();
            clientHandlers.add(this);
//            broadcastMessage("SERVER: "+username + " has entered the chat!");
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything();
        }

    }

    @Override
    public void run() {
        final Object[] inputObj = new Object[1];
        
        while (socket.isConnected()) {
            try 
//            (
//            		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
//                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
//            		) 
{
            	while ((inputObj[0] = in.readObject()) != null) {
                	if (inputObj[0] instanceof EncMsgAndDs) {
                //this is the area where the client's message is reaching
                //encrypted text should reach here
                		 System.out.println("broadcasting");
//                message = br.readLine();
                // send  the encrypted message as it is.
//                sendDummyMessage();

                // Get the Encrypted Obj from one client 
                EncMsgAndDs encMsgDsObj = (EncMsgAndDs) inputObj[0];
                // Send to other clients 
                broadcastMessage(encMsgDsObj);
//                System.out.println("broadcasting");

            }
            	}
            }catch (IOException e) {
                e.printStackTrace();

                closeEverything();
                break;

            } catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    public void sendDummyMessage() {
        try {
            bw.write("SERVER: This is a dummy message in response to your message.");
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything();
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

    public void broadcastMessage(EncMsgAndDs receiveMessage) {
        for (ClientHandler client : clientHandlers) {
            try {
                if (!client.username.equals(receiveMessage.getUsername())) {
//                    client.bw.write(message);
//                    client.bw.newLine();
//                    client.bw.flush();
                    
                    client.out.writeObject(receiveMessage);
                    client.out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                closeEverything();

            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
//        broadcastMessage("SERVER: " + username+" has left the chat!");
    }
}