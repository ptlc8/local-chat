package dev.ambi.localchat.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Consumer;

import dev.ambi.localchat.network.ClientP2p;

public class Client {
	
	private ClientP2p p2p;
	
	private User selfUser;
	private HashMap<Id, User> users = new HashMap<>();
	
	private ArrayList<Consumer<User>> joinListeners = new ArrayList<>();
	private ArrayList<Consumer<User>> leaveListeners = new ArrayList<>();
	
	public Client(int port) {
		p2p = new ClientP2p(port);
		p2p.addJoinListener(this::onJoin);
		p2p.addLeaveListener(this::onLeave);
		p2p.addDataListener(String.class, this::onMessage);
		p2p.startListening();
		p2p.searchPeers();
		selfUser = new User(p2p.getId());
	}
	
	public ClientP2p getP2p() {
		return p2p;
	}
	
	public User getSelfUser() {
		return selfUser;
	}
	
	public Collection<User> getUsers() {
		return Collections.unmodifiableCollection(users.values());
	}
	
	public User getUser(Id id) {
		return users.get(id);
	}
	
	public boolean sendMessage(User user, String message) {
		if (user == null) return false;
		if (p2p.send(user.getId(), message)) {
			user.addMessage(new Message(selfUser, message));
			return true;
		} else return false;
	}
	
	private void onJoin(Id id) {
		User user = new User(id);
		users.put(id, user);
		joinListeners.forEach(l -> l.accept(user));
	}
	
	private void onMessage(Id id, Object data) {
		User user = users.get(id);
		user.addMessage(new Message(user, (String)data));
	}
	
	private void onLeave(Id id) {
		User user = users.get(id);
		users.remove(id);
		leaveListeners.forEach(l -> l.accept(user));
	}
	
	public void addJoinListener(Consumer<User> listener) {
		joinListeners.add(listener);
	}
	
	public void addLeaveListener(Consumer<User> listener) {
		leaveListeners.add(listener);
	}
	

}
