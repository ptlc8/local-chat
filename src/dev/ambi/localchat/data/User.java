package dev.ambi.localchat.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.ImageIcon;

import dev.ambi.localchat.network.Connection;

public class User {
	
	private Connection connection;
	private Username selfUsername;
	private Username username;
	private ArrayList<Message> messages = new ArrayList<>();
	
	private ArrayList<Consumer<Message>> messageListeners = new ArrayList<>();
	
	public User(Username selfUsername, Connection connection) {
		this.selfUsername = selfUsername;
		this.connection = connection;
		connection.addDataListener(this::onData);
		connection.send(selfUsername);
	}
	
	private void onData(Object data) {
		System.out.println(data.getClass().getName());
		if (username == null) {
			if (data instanceof Username)
				username = (Username) data;
			else
				return;
		}
		if (data instanceof String)
			addMessage(new TextMessage(username, (String)data));
		else if (data instanceof ImageIcon)
			addMessage(new ImageMessage(username, (ImageIcon)data));
	}
	
	public void disconnect() {
		connection.close();
	}
	
	public Username getUsername() {
		return username;
	}
	
	void setUsername(Username username) {
		this.username = username;
	}
	
	void addMessage(Message message) {
		messages.add(message);
		messageListeners.forEach(l -> l.accept(message));
	}
	
	public List<Message> getMessages() {
		return Collections.unmodifiableList(messages);
	}
	
	public boolean sendMessage(String content) {
		if (connection.send(content)) {
			addMessage(new TextMessage(selfUsername, content));
			return true;
		}
		return false;
	}

	public boolean sendImage(ImageIcon image) {
		if (connection.send(image)) {
			addMessage(new ImageMessage(selfUsername, image));
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
	
	@Override
	public String toString() {
		return username == null ? "Unknown user" : username.getText();
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj || (obj instanceof User && ((User)obj).username.equals(username));
	}
	
}
