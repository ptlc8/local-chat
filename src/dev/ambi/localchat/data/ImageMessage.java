package dev.ambi.localchat.data;

import javax.swing.ImageIcon;

public class ImageMessage extends Message {
	
	private ImageIcon image;

	public ImageMessage(User sender, ImageIcon image) {
		super(sender, image.toString());
		this.image = image;
	}
	
	public ImageIcon getImage() {
		return image;
	}

}
