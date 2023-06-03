package dev.ambi.localchat.data;

import javax.swing.ImageIcon;

public class ImageMessage implements Message {
	
	private Username sender;
	private ImageIcon image;

	public ImageMessage(Username sender, ImageIcon image) {
		this.sender = sender;
		this.image = image;
	}
	
	@Override
	public Username getSender() {
		return sender;
	}
	
	public ImageIcon getImage() {
		return image;
	}

}
