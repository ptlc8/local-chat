package dev.ambi.localchat.data;

public class TextMessage implements Message {
	
	private Username sender;
	private String content;
	
	public TextMessage(Username sender, String content) {
		this.sender = sender;
		this.content = content;
	}
	
	@Override
	public Username getSender() {
		return sender;
	}
	
	public String getContent() {
		return content;
	}
	
}
