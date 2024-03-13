package lab4.chat;

import java.io.Serializable;
import java.security.PublicKey;

public class DigitalSignature implements Serializable {
	
	private PublicKey publicKey;
	private byte[] digitalSignature;
	private String message;
	private long timeStamp;
	
	public DigitalSignature(PublicKey publicKey, byte[] digitalSignature, String message, long timeStamp) {
		this.publicKey = publicKey;
		this.digitalSignature = digitalSignature;
		this.message = message;
		this.timeStamp = timeStamp;
	}

	public PublicKey getKey() {
		return this.publicKey;
	}
	
	public byte[] getSignature() {
		return this.digitalSignature;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public long getTimeStamp() {
		return this.timeStamp;
	}
	
}
