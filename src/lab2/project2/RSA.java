package lab2.project2;


import javax.crypto.*;
import java.io.*;

import java.security.*;
import java.util.Base64;
import java.util.Random;

public class RSA {

    public static int generateNonce(){

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

    public static String convertToNonce(byte[]arr)  {

        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        ObjectInput in;
        try {
            in = new ObjectInputStream(bis);
            return (String) in.readObject();
        } catch (IOException  | ClassNotFoundException e) {
            e.printStackTrace();
        }


        return "Bad Convert";
    }

    public static String encrypt(Key key, String msg){

        try {

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE,key);

            //This message will contain an encrypted byte array
            return encode(cipher.doFinal(RSA.convertToByteArray(msg)));
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException |
                 NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "Bad Encrypt";
    }


    public static String decrypt(Key key, String encryptedBytes){

        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte [] objDecrypt = cipher.doFinal(decode(encryptedBytes));

            return RSA.convertToNonce(objDecrypt);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException |
                 NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "Bad Decrypt";
    }

    private static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
    private static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
}
