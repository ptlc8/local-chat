package dev.ambi.localchat.data;

public class TextMessage implements Message {
	
	private User sender;
	private String content;
	
	public TextMessage(User sender, String content) {
		this.sender = sender;
		this.content = content;
	}
	
	@Override
	public User getSender() {
		return sender;
	}
	
	public String getContent() {
		return content;
	}
	
}
