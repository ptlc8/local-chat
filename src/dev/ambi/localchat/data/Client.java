package dev.ambi.localchat.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Consumer;

import dev.ambi.localchat.network.ClientP2p;
import dev.ambi.localchat.network.Connection;

public class Client {
	
	private ClientP2p p2p;
	
	private Username selfUserName;
	private HashMap<Connection, User> users = new HashMap<>();
	
	private ArrayList<Consumer<User>> joinListeners = new ArrayList<>();
	private ArrayList<Consumer<User>> leaveListeners = new ArrayList<>();
	
	public Client(int port, String username) {
		this.selfUserName = new Username(username);
		p2p = new ClientP2p(port);
		p2p.addJoinListener(this::onJoin);
		p2p.addLeaveListener(this::onLeave);
		p2p.startListening();
		p2p.searchPeers();
	}
	
	public ClientP2p getP2p() {
		return p2p;
	}
	
	public Username getSelfUsername() {
		return selfUserName;
	}
	
	public Collection<User> getUsers() {
		return Collections.unmodifiableCollection(users.values());
	}
	
	public User getUser(Connection connection) {
		return users.get(connection);
	}
	
	private void onJoin(Connection connection) {
		User user = new User(selfUserName, connection);
		users.put(connection, user);
		joinListeners.forEach(l -> l.accept(user));
	}
	
	private void onLeave(Connection connection) {
		User user = users.get(connection);
		users.remove(connection);
		leaveListeners.forEach(l -> l.accept(user));
	}
	
	public void addJoinListener(Consumer<User> listener) {
		joinListeners.add(listener);
	}
	
	public void addLeaveListener(Consumer<User> listener) {
		leaveListeners.add(listener);
	}

}
