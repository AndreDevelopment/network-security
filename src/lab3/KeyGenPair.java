package lab3;


import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;


public class KeyGenPair {

    private PrivateKey privateKey;
    private PublicKey publicKey;



    public KeyGenPair()  {

        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(512);
            KeyPair pair = generator.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();

       }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public static SecretKey createMasterKey(){

        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
