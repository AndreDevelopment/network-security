package lab2.project1;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {

    private String msg;
    private byte[] information;

    private int nonce;

    private SecretKey secretKey;
    private NonceID nonceID;
    public Message(byte[] information,int nonce) {

        this.information = information;
        this.nonce = nonce;
    }
    public Message(SecretKey secretKey, NonceID nonceID) {
        this.secretKey = secretKey;
        this.nonceID = nonceID;
    }

    public Message(byte[] information) {

        this.information = information;

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

    public byte[] getInformation() {
        return information;
    }

    public void setInformation(byte[] information) {
        this.information = information;
    }

    @Override
    public String toString() {
        return "Message{" +
                "msg='" + msg + '\'' +
                ", information=" + Arrays.toString(information) +
                ", nonce=" + nonce +
                ", secretKey=" + secretKey +
                ", nonceID=" + nonceID +
                '}';
    }
}
