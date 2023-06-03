package dev.ambi.localchat.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import javax.swing.ImageIcon;

import dev.ambi.localchat.network.Connection;

public class User {
	
	private Connection connection;
	private User selfUser;
	private String name;
	private ArrayList<Message> messages = new ArrayList<>();
	
	private ArrayList<Consumer<Message>> messageListeners = new ArrayList<>();
	private ArrayList<Runnable> identifyListeners = new ArrayList<>();
	
	public User(User selfUser, Connection connection) {
		this.selfUser = selfUser;
		this.connection = connection;
		connection.addDataListener(this::onData);
	}
	
	public User(String name) {
		this.selfUser = this;
		this.name = name;
		this.connection = null;
	}
	
	private void onData(Object data) {
		System.out.println(data);
		if (Request.IDENTIFY.equals(data)) {
			connection.send("username " + selfUser.getName());
			return;
		}
		if (name == null) {
			if (data instanceof String && ((String) data).startsWith("username ")) {
				name = ((String) data).replaceFirst("username ", "");
				identifyListeners.forEach(l -> l.run());
			}
			return;
		}
		if (data instanceof String && ((String) data).startsWith("message "))
			addMessage(new TextMessage(this, ((String) data).replaceFirst("message ", "")));
		else if (data instanceof ImageIcon)
			addMessage(new ImageMessage(this, (ImageIcon) data));
	}
	
	private void addMessage(Message message) {
		messages.add(message);
		messageListeners.forEach(l -> l.accept(message));
	}
	
	public void identify() {
		new Thread(() -> {
			try {
				while (!connection.isClosed() && name == null) {
					connection.send(Request.IDENTIFY);
						Thread.sleep(100);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "Waiting identify thread").run();
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
		if (connection.send("message " + content)) {
			addMessage(new TextMessage(selfUser, content));
			return true;
		}
		return false;
	}

	public boolean sendImage(ImageIcon image) {
		if (connection.send(image)) {
			addMessage(new ImageMessage(selfUser, image));
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
