// class that handles signature and timestamp [replay attack]
package lab2.project3;
import java.io.Serializable;

public class MessageWithSignature implements Serializable {
    private String message;
    private byte[] signature;
    private long timestamp;

    public MessageWithSignature(String message, byte[] signature, long timestamp) {
        this.message = message;
        this.signature = signature;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public byte[] getSignature() {
        return signature;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
