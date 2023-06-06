package dev.ambi.localchat.data;

public class FileMessage implements Message {
	
	private User sender;
	private String filename;
	private boolean downloadable;
	
	public FileMessage(User sender, String filename, boolean downloadable) {
		this.sender = sender;
		this.filename = filename;
		this.downloadable = downloadable;
	}
	
	@Override
	public User getSender() {
		return sender;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public boolean isDownloadable() {
		return downloadable;
	}
	
	public boolean acceptFileOffer() {
		return sender.acceptFileOffer(filename);
	}
	
}
