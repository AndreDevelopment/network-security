package lab2.project1;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class KeyGen {
    private static KeyGen instance = null;
    private SecretKey key;

    private KeyGen() {
        String secretString = "thisismysecretkey24bytes";
        key = new SecretKeySpec(secretString.getBytes(), "AES");
    }

    public static KeyGen getInstance() {
        if (instance == null)
            instance = new KeyGen();
        return instance;
    }

    public SecretKey getKey() {
        return this.key;
    }


}
