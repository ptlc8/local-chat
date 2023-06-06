package dev.ambi.localchat.data;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import javax.swing.ImageIcon;

import dev.ambi.localchat.files.Files;
import dev.ambi.localchat.network.Connection;

public class User {

	private static byte REQUEST_USERNAME = 1;
	private static byte USERNAME = 2;
	private static byte TEXT = 3;
	private static byte IMAGE = 4;
	private static byte FILE_OFFER = 5;
	private static byte ACCEPT_FILE_OFFER = 6;
	private static byte FILE = 7;
	
	private Connection connection;
	private User selfUser;
	private String name;
	private ArrayList<Message> messages = new ArrayList<>();
	private HashMap<String, String> fileOffers = new HashMap<>();
	private ArrayList<String> acceptedFileOffers = new ArrayList<>();
	
	private ArrayList<Consumer<Message>> messageListeners = new ArrayList<>();
	private ArrayList<Runnable> identifyListeners = new ArrayList<>();
	
	public User(User selfUser, Connection connection) {
		this.selfUser = selfUser;
		this.connection = connection;
		connection.addObjectListener(this::onObject);
		connection.setDataListener(this::onData);
	}
	
	public User(String name) {
		this.selfUser = this;
		this.name = name;
		this.connection = null;
	}
	
	private void onObject(byte tag, Object data) {
		if (tag == REQUEST_USERNAME) {
			connection.sendObject(USERNAME, selfUser.getName());
			return;
		}
		if (data == null) return;
		if (name == null) {
			if (tag == USERNAME) {
				name = ((String) data);
				identifyListeners.forEach(l -> l.run());
			}
			return;
		}
		if (tag == TEXT)
			addMessage(new TextMessage(this, (String) data));
		else if (tag == IMAGE)
			addMessage(new ImageMessage(this, (ImageIcon) data));
		else if (tag == FILE_OFFER)
			addMessage(new FileMessage(this, (String) data, true));
		else if (tag == ACCEPT_FILE_OFFER) {
			String path = fileOffers.get(data);
			if (path != null) {
				connection.sendData(FILE, Files.fromFile(path));
			} else {
				System.out.println("[" + hashCode() + "] File offer not found: " + data);
			}
		}
	}

	private OutputStream onData(byte tag) {
		if (tag == FILE) {
			String filename = acceptedFileOffers.remove(0);
			if (filename != null)
				return Files.toFile(filename);
		}
		return OutputStream.nullOutputStream();
	}

	private void addMessage(Message message) {
		messages.add(message);
		messageListeners.forEach(l -> l.accept(message));
	}

	public void identify() {
		new Thread(() -> {
			try {
				while (!connection.isClosed() && name == null) {
					connection.send(REQUEST_USERNAME);
					Thread.sleep(100);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "Waiting identify thread").start();
	}
	
	public void disconnect() {
		connection.close();
	}
	
	public String getName() {
		return name;
	}
	
	public int getColor() {
		Random rdm = new Random(hashCode());
		int r = rdm.nextInt(192);
		int g = rdm.nextInt(192);
		int b = rdm.nextInt(192);
		if (r > g && r > b) r = 191;
		if (r < g && r < b) r = 0;
		if (g > r && g > b) g = 191;
		if (g < r && g < b) g = 0;
		if (b > g && b > r) b = 191;
		if (b < g && b < r) b = 0;
		return ((r << 8) + g << 8) + b;
	}
	
	public List<Message> getMessages() {
		return Collections.unmodifiableList(messages);
	}
	
	public boolean sendMessage(String content) {
		if (connection.sendObject(TEXT, content)) {
			addMessage(new TextMessage(selfUser, content));
			return true;
		}
		return false;
	}

	public boolean sendImage(ImageIcon image) {
		if (connection.sendObject(IMAGE, image)) {
			addMessage(new ImageMessage(selfUser, image));
			return true;
		}
		return false;
	}

	public boolean sendFileOffer(String path) {
		String filename = new File(path).getName();
		fileOffers.put(filename, path);
		if (connection.sendObject(FILE_OFFER, filename)) {
			addMessage(new FileMessage(selfUser, filename, false));
			return true;
		}
		return false;
	}

	public boolean acceptFileOffer(String filename) {
		acceptedFileOffers.add(filename);
		if (connection.sendObject(ACCEPT_FILE_OFFER, filename)) {
			return true;
		}
		return false;
	}

	public void addMessageListener(Consumer<Message> listener) {
		messageListeners.add(listener);
	}
	
	public void removeMessageListener(Consumer<Message> listener) {
		messageListeners.remove(listener);
	}
	
	public void addIdentifyListener(Runnable listener) {
		identifyListeners.add(listener);
	}
	
	@Override
	public String toString() {
		return name == null ? "Unknown user" : name;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj || (obj instanceof User && name != null && name.equals(((User)obj).name));
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
}
