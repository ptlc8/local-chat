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
	
	private final User selfUser;
	private HashMap<Connection, User> users = new HashMap<>();
	
	private ArrayList<Consumer<User>> joinListeners = new ArrayList<>();
	private ArrayList<Consumer<User>> leaveListeners = new ArrayList<>();
	
	public Client(int port, String username) {
		this.selfUser = new User(username);
		p2p = new ClientP2p(port);
		p2p.addJoinListener(this::onJoin);
		p2p.addLeaveListener(this::onLeave);
		p2p.startListening();
		p2p.searchPeers();
	}
	
	private void onJoin(Connection connection) {
		User user = new User(selfUser, connection);
		user.addIdentifyListener(() -> {
			if (user.equals(selfUser)) {
				connection.close();
				System.out.println("[" + hashCode() + "] Can't connect to itself");
				return;
			}
			if (users.containsValue(user)) {
				connection.close();
				System.out.println("[" + hashCode() + "] Connection already etablished");
				return;
			}
			users.put(connection, user);
			System.out.println("[" + hashCode() + "] " + user.getName() + " joins");
			joinListeners.forEach(l -> l.accept(user));
		});
		user.identify();
	}
	
	private void onLeave(Connection connection) {
		User user = users.remove(connection);
		if (user != null) {
			System.out.println("[" + hashCode() + "] " + user.getName() + " leaves");
			leaveListeners.forEach(l -> l.accept(user));
		}
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
	
	public User getUser(Connection connection) {
		return users.get(connection);
	}
	
	public void addJoinListener(Consumer<User> listener) {
		joinListeners.add(listener);
	}
	
	public void addLeaveListener(Consumer<User> listener) {
		leaveListeners.add(listener);
	}

}
