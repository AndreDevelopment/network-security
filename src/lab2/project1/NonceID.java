package lab2.project1;


import java.io.Serializable;

public class NonceID implements Serializable {

    private int nonce;
    private String id;

    public NonceID(int nonce, String id) {
        this.nonce = nonce;
        this.id = id;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "NonceID{" +
                "nonce=" + nonce +
                ", id='" + id + '\'' +
                '}';
    }
}
