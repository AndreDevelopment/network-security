package lab3;


import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
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

    public static SecretKey createMasterKey(String masterKey){
        return  new SecretKeySpec(masterKey.getBytes(), "AES");
    }

    public static SecretKey createSharedKey(String sharedKey){
        //"thisismysecretkey24bytes"
        return  new SecretKeySpec(sharedKey.getBytes(), "AES");
    }
}
