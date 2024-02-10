package lab2.project1;


import javax.crypto.*;
import java.io.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

public class AES {

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

    public static NonceID convertToNonceID(byte[]arr)  {

        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        ObjectInput in;
        try {
            in = new ObjectInputStream(bis);
            return (NonceID) in.readObject();
        } catch (IOException  | ClassNotFoundException e) {
            e.printStackTrace();
        }


        return null;
    }

    public static String encrypt(SecretKey key,NonceID nonceID){

        try {

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE,key);

            //This message will contain an encrypted byte array
            return encode(cipher.doFinal(AES.convertToByteArray(nonceID)));
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException |
                 NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "Bad Encrypt";
    }


    public static NonceID decrypt(SecretKey key,String encryptedBytes){

        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte [] objDecrypt = cipher.doFinal(decode(encryptedBytes));

            return convertToNonceID(objDecrypt);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException |
                 NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new NonceID(0,"Bad Nonce");
    }

    private static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
    private static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
}
