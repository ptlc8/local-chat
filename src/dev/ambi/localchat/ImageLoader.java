package dev.ambi.localchat;
import java.awt.FileDialog;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class ImageLoader {
	
	public static ImageIcon prompt() {
		FileDialog imageChooser = new FileDialog(new JFrame());
		imageChooser.setFilenameFilter((dir, name) -> {
			return name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg") || name.endsWith(".gif");
		});
		imageChooser.setVisible(true);
		return get(imageChooser.getDirectory() + imageChooser.getFile());
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
