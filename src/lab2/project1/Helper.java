package lab2.project1;


import javax.crypto.*;
import java.io.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Helper {

    public static int getNonce(){

        return new Random().nextInt(900000) + 100000;
    }


    public static byte[] convertToByteArray(Object obj){

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            byte[] yourBytes = bos.toByteArray();

            // Always close streams
            out.close();
            bos.close();

            return yourBytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static Message convertToMessage(byte[]arr)  {

        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        ObjectInput in;
        try {
            in = new ObjectInputStream(bis);
            return (Message) in.readObject();
        } catch (IOException  | ClassNotFoundException e) {
            e.printStackTrace();
        }


        return null;
    }

    public static Message encrypt(SecretKey key,NonceID nonceID){

        try {
            Message message =  new Message(key,nonceID);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE,key);

            //This message will contain an encrypted byte array
            return new Message(cipher.doFinal(Helper.convertToByteArray(message)));
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException |
                 NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new Message("Bad Enc");
    }


    public static Message decrypt(SecretKey key,byte[] encryptedBytes){

        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte [] objDecrypt = cipher.doFinal(encryptedBytes);
            System.out.println("The decrypted object is: ");
            for (byte b: objDecrypt){
                System.out.print(b+" ");
            }
            System.out.println();
            return Helper.convertToMessage(objDecrypt);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException |
                 NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new Message("Bad Dec");
    }
}
