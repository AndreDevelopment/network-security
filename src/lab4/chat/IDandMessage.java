package lab4.chat;

import java.io.Serializable;

public class IDandMessage implements Serializable {

	private String id;
	private String message;
	
	public IDandMessage(String id, String nonce) {
		this.message = nonce;
		this.id = id;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getId() {
		return id;
	}
}
