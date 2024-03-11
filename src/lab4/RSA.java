package lab4;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;
import java.util.stream.Collectors;

public class RSA {

    public static int generateNonce(){

        return new Random().nextInt(900000) + 100000;
    }

    public static String generateMasterKeyString() {

        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        int length = 24;

        //Using Streams API to generate a master key string
        return random.ints(length, 0, alphabet.length())
                .mapToObj(alphabet::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
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

    }//end of byte conversion

    public static String convertToString(byte[]arr)  {

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


    public static String encrypt(Key key, String msg,String transformation){

        //"RSA/ECB/PKCS1Padding"
        //"AES"
        try {

            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE,key);

            //This message will contain an encrypted byte array
            return encode(cipher.doFinal(RSA.convertToByteArray(msg)));
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException |
                 NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "Bad Encrypt";
    }


    public static String decrypt(Key key, String encryptedBytes,String transformation){

        //"RSA/ECB/PKCS1Padding"
        //"AES"

        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte [] objDecrypt = cipher.doFinal(decode(encryptedBytes));

            return RSA.convertToString(objDecrypt);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException |
                 NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "Bad Decrypt";
    }




    public static String encryptLongString(Key key,String msg,String transformation){

        int mid = msg.length() / 2;

        String firstHalf = RSA.encrypt(key,msg.substring(0,mid),transformation);
        String secondHalf = RSA.encrypt(key,msg.substring(mid),transformation);

        return firstHalf+secondHalf;

    }

    public static String decryptLongString(Key key,String msg,String transformation){
        int mid = msg.length() / 2;

        String firstHalf = RSA.decrypt(key,msg.substring(0,mid),transformation);
        String secondHalf = RSA.decrypt(key,msg.substring(mid),transformation);

        return firstHalf+secondHalf;
    }

    private static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
    private static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }


}
