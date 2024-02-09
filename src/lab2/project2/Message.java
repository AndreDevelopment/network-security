package lab2.project2;

import javax.crypto.SecretKey;
import java.io.Serializable;


public class Message implements Serializable {

    private String msg;

    private int nonce;

    private SecretKey secretKey;
    private NonceID nonceID;

    public Message(SecretKey secretKey, NonceID nonceID) {
        this.secretKey = secretKey;
        this.nonceID = nonceID;
    }


    public Message(String msg) {

        this.msg = msg;

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

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Message{" +
                "secretKey=" + secretKey +
                ", nonceID=" + nonceID +
                '}';
    }
}
