package lab4.chat;

import java.io.Serializable;

public class EncMsgAndDs implements Serializable {
	
	private byte[] encryptedMessage;
	private DigitalSignature digitalSignature;
	private String username;
	
	public EncMsgAndDs(byte[] encryptedMessage, DigitalSignature digitalSignature, String username) {
		this.encryptedMessage = encryptedMessage;
		this.digitalSignature = digitalSignature;
		this.username = username;
	}
	
	public byte[] getEncryptedMessage() {
		return encryptedMessage;
	}
	
	public DigitalSignature getDigitalSignature() {
		return digitalSignature;
	}
	
	public String getUsername() {
		return username;
	}

}
