package lab2.project2;


import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;


public class Message implements Serializable {

    private String msg;
    private int nonce;
    private PublicKey publicKey;
    private PrivateKey privateKey;


    public Message(PublicKey publicKey,String msg) {
        this.publicKey = publicKey;
        this.msg = msg;
    }

    public Message(PrivateKey privateKey,int nonce) {
        this.privateKey = privateKey;
        this.nonce = nonce;
    }



    public Message(String msg) {

        this.msg = msg;

    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
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

    //TODO: Change for when certain values are null
    @Override
    public String toString() {
        return "Message{" +
                "publicKey=" + publicKey +
                ", nonce=" + nonce +
                '}';
    }
}
