package dev.ambi.localchat.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class User {
	
	private Id id;
	private ArrayList<Message> messages = new ArrayList<>();
	
	private ArrayList<Consumer<Message>> messageListeners = new ArrayList<>();
	
	public User(Id id) {
		this.id = id;
	}
	
	public Id getId() {
		return id;
	}
	
	void addMessage(Message message) {
		messages.add(message);
		messageListeners.forEach(l -> l.accept(message));
	}
	
	public List<Message> getMessages() {
		return Collections.unmodifiableList(messages);
	}
	
	public void addMessageListener(Consumer<Message> listener) {
		messageListeners.add(listener);
	}
	
	public void removeMessageListener(Consumer<Message> listener) {
		messageListeners.remove(listener);
	}
	
	@Override
	public String toString() {
		return "User " + id;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj || (obj instanceof User && ((User)obj).id.equals(id));
	}
	
}
