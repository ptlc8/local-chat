package dev.ambi.localchat.files;

import java.awt.Image;
import javax.swing.ImageIcon;

public class Images {
	
	public static ImageIcon prompt() {
		return get(Files.promptPath((dir, name) -> {
			return name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg") || name.endsWith(".gif");
		}));
	}
	
	public static ImageIcon get(String filename) {
		return new ImageIcon(filename);
	}
	
	public static ImageIcon resize(ImageIcon image, int maxWidth, int maxHeight) {
		float scaleW = image.getIconWidth() > maxWidth ? maxWidth * 1f / image.getIconWidth() : 1;
		float scaleH = image.getIconHeight() > maxHeight ? maxHeight * 1f / image.getIconHeight() : 1;
		float scale = Math.min(scaleW, scaleH);
		Image img = image.getImage();
		img = img.getScaledInstance((int) (image.getIconWidth() * scale), (int) (image.getIconHeight() * scale), Image.SCALE_SMOOTH);
		return new ImageIcon(img);
	}
	
}
