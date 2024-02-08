package lab2.project1;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {

    private String msg;
    private byte[] information;

    public Message(byte[] information) {

        this.information = information;
    }

    public Message(String msg) {

        this.msg = msg;

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
                '}';
    }
}
