package lab2.project2;


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



    public String getId() {
        return id;
    }



    @Override
    public String toString() {
        return "NonceID{" +
                "nonce=" + nonce +
                ", id='" + id + '\'' +
                '}';
    }
}
