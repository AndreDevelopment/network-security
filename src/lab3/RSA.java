package lab3;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import java.util.Random;

public class RSA {

    public static int generateNonce(){

        return new Random().nextInt(900000) + 100000;
    }

    public static String encrypt(Key key, String msg){

        try {

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE,key);

            //This message will contain an encrypted byte array
            return encode(cipher.doFinal(lab2.project2.RSA.convertToByteArray(msg)));
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

            return lab2.project2.RSA.convertToNonce(objDecrypt);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException |
                 NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "Bad Decrypt";
    }

    private static String encode(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);

    }
    private static byte[] decode(String data) {
        return data.getBytes(StandardCharsets.UTF_8);
    }
}
