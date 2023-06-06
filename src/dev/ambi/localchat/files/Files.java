package dev.ambi.localchat.files;

import java.awt.FileDialog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JFrame;

public class Files {
	
	public static String promptPath() {
		return promptPath((d, f) -> true);
	}
	
	public static String promptPath(FilenameFilter fileNameFilter) {
		FileDialog fileChooser = new FileDialog(new JFrame());
		fileChooser.setFilenameFilter(fileNameFilter);
		fileChooser.setVisible(true);
		if (fileChooser.getFile() == null)
			return null;
		return fileChooser.getDirectory() + fileChooser.getFile();
	}
	
	public static InputStream fromFile(String filename) {
		File file = new File(filename);
		if (!file.exists())
			return null;
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return InputStream.nullInputStream();
		}
	}
	
	public static OutputStream toFile(String filename) {
		File file = new File(filename);
		try {
			if (!file.exists())
				file.createNewFile();
			return new FileOutputStream(file);
		} catch (IOException e) {
			e.printStackTrace();
			return OutputStream.nullOutputStream();
		}
	}
	
}
