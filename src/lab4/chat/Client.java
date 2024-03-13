package lab4.chat;

import lab4.Colour;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class Client {
    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private  String username;
    
    private IDandMessage idMsgObj;
    
    // sending objs 
    private ObjectOutputStream out ;
	private ObjectInputStream in;
    
    // shared key
    private static String key = "IamAHackErYouCaNtFiNd3o6";

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());
            this.username = username;
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything();
        }
    }
    
    private static byte[] encrypt(IDandMessage msgObj, String sharedkey) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msgObj);
        oos.flush();
        
        byte[] msgObjBytes = bos.toByteArray();
        Cipher cipher = Cipher.getInstance("AES");
        SecretKey secretKey = new SecretKeySpec(sharedkey.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedObj = cipher.doFinal(msgObjBytes);

        return encryptedObj;
    }
    
    private static DigitalSignature createSignature (IDandMessage msgObj) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
    	
    	// Generate RSA key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Get private and public keys
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

    	
    	// Create a signature object
        Signature signature = Signature.getInstance("SHA256withRSA");

        // Initialize the signature with the private key
        signature.initSign(privateKey);

        // Set the message to be signed
        signature.update(msgObj.getMessage().getBytes());

        // Generate the digital signature
        byte[] digitalSignature = signature.sign();
        DigitalSignature ds = new DigitalSignature(publicKey,digitalSignature, msgObj.getMessage(), new Date().getTime());
		return ds;
    	
    }

    public static IDandMessage decryptData(byte[] encryptedMsg) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, ClassNotFoundException  {
		
//		System.out.print("Received (encrypted) message: ");
//		for (int i = 0; i < encryptedData.length; i++) {
//		    byte b = encryptedData[i];
//		    System.out.print(b+" ");
//		}
		Cipher cipher = Cipher.getInstance("AES");
		SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedBytes = cipher.doFinal(encryptedMsg);

		ByteArrayInputStream bis = new ByteArrayInputStream(decryptedBytes);
		ObjectInput oi = new ObjectInputStream(bis);
		IDandMessage decryptedMsg = (IDandMessage) oi.readObject();
		

		return decryptedMsg;
	}
    
    public static boolean verifySignature(byte[] digitalSignature, String receivedMessage, long timeStamp, PublicKey key) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		
		
        // Verify the freshness of the message (check if timestamp is within a reasonable time window)
        long currentTime = new Date().getTime();
        long timeDifference = currentTime - timeStamp;
        if (timeDifference < 60000) { // 1 minute time window for freshness
            System.out.println(Colour.ANSI_GREEN+ "[TIMESTAMP VERIFIED]" + Colour.ANSI_RESET);
            
        } else {
            System.out.println("Possible replay attack detected.");
            return false;
        }
            
		Signature verificationSignature = Signature.getInstance("SHA256withRSA");
        verificationSignature.initVerify(key);
        
        verificationSignature.update(receivedMessage.getBytes());
        boolean verified = verificationSignature.verify(digitalSignature);
		return verified;
		
	}

    public void sendMessage() throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, SignatureException {

        try {
            bw.write(username);
            bw.newLine();
            bw.flush();

            Scanner scan = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scan.nextLine();
                
                // create obj [ID, M]
                IDandMessage sendIdMsgObj = new IDandMessage(username,messageToSend );
                // encrypt obj [ID, M] using Key => E (K, [ID, M])
                byte[] encryptedMsg = encrypt(sendIdMsgObj, key);
                // create [E, Ds]
                EncMsgAndDs encDsObj = new EncMsgAndDs(encryptedMsg, createSignature(sendIdMsgObj), username);
               System.out.println("USERNAME : " + encDsObj.getUsername());
                // send [E, Ds] 
                out.writeObject(encDsObj);
                out.flush();
                //System.out.println("Obj in client sent");


            }
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything();
        }

    }

    public void listenForMessage() {
    	
    	final Object[] inputObj = new Object[1];

        new Thread(()-> {
            {

                try {
                    while (socket.isConnected()) {
                    	while ((inputObj[0] = in.readObject()) != null) {
                        	if (inputObj[0] instanceof EncMsgAndDs) {
                    	// Receive the Encrypted message and DS from KDC
                        EncMsgAndDs encMsgDsObj = (EncMsgAndDs) inputObj[0];
                    	// Extract the encrypted obj and Decrypt it
                    	IDandMessage idMsgObj = (IDandMessage) decryptData(encMsgDsObj.getEncryptedMessage());
                    	String messageReceived = idMsgObj.getMessage();
                    	// Extract the Digital signature 
                    	DigitalSignature ds = (DigitalSignature) encMsgDsObj.getDigitalSignature();
                    	boolean verify = verifySignature(ds.getSignature(),ds.getMessage(),ds.getTimeStamp(), ds.getKey());
                        if(verify) {
                         	System.out.println("Message received and authentication successful!");
                         	// Display the digital signature
                             System.out.println(Colour.ANSI_YELLOW+  "[MESSAGE]: "+ messageReceived+ Colour.ANSI_RESET);
                             System.out.println(Colour.ANSI_RED+  "[DIGTIAL SIGNATURE]"+ Colour.ANSI_RESET);
                             System.out.println(Base64.getEncoder().encodeToString(ds.getSignature()));
                        }
                        else {
                        	System.out.println("Authentication Unsuccessful!");
                        }
                         //sts of 2parts. ["username+": "+ encryptedmessage]
//                        if (messageFromGC == null) {
//                            break;
//                        }

                        //decrypt the message here
//                        System.out.println(messageFromGC);
                        		}//end of while
                  }
                    
             }
                } catch (IOException e) {
                    e.printStackTrace();
                    closeEverything();
                } catch (ClassNotFoundException | NoSuchPaddingException | NoSuchAlgorithmException |
                         InvalidKeyException | IllegalBlockSizeException | BadPaddingException | SignatureException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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

    public static void main(String[] args) throws IOException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, SignatureException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String newUser = scanner.nextLine();

        Socket socket = new Socket("localhost",23456);
        Client client = new Client(socket,newUser);
        client.listenForMessage();
        client.sendMessage();
    }
}