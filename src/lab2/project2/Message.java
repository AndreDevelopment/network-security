package lab2.project2;


import java.io.Serializable;


public class Message implements Serializable {

    private String msg;
    private int nonce;


    public Message(String msg) {

        this.msg = msg;

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



}
