// This class handles Sign(M)
package lab2.project3;
import java.io.Serializable;

public class MessageWithSignature implements Serializable {
    private String message;
    private byte[] signature;

    public MessageWithSignature(String message, byte[] signature) {
        this.message = message;
        this.signature = signature;
    }

    public String getMessage() {
        return message;
    }

    public byte[] getSignature() {
        return signature;
    }
}