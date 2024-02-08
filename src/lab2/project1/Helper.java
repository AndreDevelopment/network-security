package lab2.project1;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Helper {

    public static int getNonce(){

        return new Random().nextInt(900000) + 100000;
    }

    public static SecretKey getKey() throws NoSuchAlgorithmException {

        return KeyGenerator.getInstance("DES").generateKey();
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

    public static EMessage convertToEMessage(byte[]arr)  {

        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return (EMessage) in.readObject();
        } catch (IOException  | ClassNotFoundException e) {
            e.printStackTrace();
        }


        return null;
    }
}
