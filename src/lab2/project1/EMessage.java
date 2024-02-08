package lab2.project1;

import javax.crypto.SecretKey;
import java.io.Serializable;

public class EMessage implements Serializable {

    private SecretKey secretKey;
    private NonceID nonceID;

    public EMessage(SecretKey secretKey, NonceID nonceID) {
        this.secretKey = secretKey;
        this.nonceID = nonceID;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public NonceID getNonceID() {
        return nonceID;
    }

    public void setNonceID(NonceID nonceID) {
        this.nonceID = nonceID;
    }

    @Override
    public String toString() {
        return "EMessage{" +
                "secretKey=" + secretKey.toString() +
                ", nonceID=" + nonceID +
                '}';
    }
}
