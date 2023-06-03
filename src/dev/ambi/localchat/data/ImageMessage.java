package dev.ambi.localchat.data;

import javax.swing.ImageIcon;

public class ImageMessage implements Message {
	
	private User sender;
	private ImageIcon image;

	public ImageMessage(User sender, ImageIcon image) {
		this.sender = sender;
		this.image = image;
	}
	
	@Override
	public User getSender() {
		return sender;
	}
	
	public ImageIcon getImage() {
		return image;
	}

}
