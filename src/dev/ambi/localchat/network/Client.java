package dev.ambi.localchat.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Client {
	
	private String id;
	private final int mainPort;
	private final int maxPort;
	private ListeningServer listeningServer = null;
	private HashMap<String, ChatConnection> connections = new HashMap<>();
	
	private ArrayList<Consumer<ChatConnection>> joinListeners = new ArrayList<>();
	private ArrayList<Consumer<ChatConnection>> leaveListeners = new ArrayList<>();
	private ArrayList<BiConsumer<ChatConnection, String>> messageListeners = new ArrayList<>();
	
	public Client(int port) {
		this.id = generateRandomId(8);
		this.mainPort = port;
		this.maxPort = port + 10;
	}
	
	public boolean startListening() {
		for (int port = mainPort; port <= maxPort; port++)
			if (startListening(port))
				return true;
		return false;
	}
	
	public boolean startListening(int port) {
		try {
			if (listeningServer != null && !listeningServer.isClosed()) {
				if (listeningServer.getLocalPort() == port)
					return true;
				listeningServer.close();
			}
			listeningServer = new ListeningServer(port) {
				@Override
				public void onConnection(Socket socket) {
					onAcceptSocket(socket);
				}
			};
			System.out.println("[" + id + "] Listening on port " + port);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public void stopListening() {
		try {
			listeningServer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		listeningServer = null;
		System.out.println("[" + id + "] Stop listening");
	}
	
	public boolean isListening() {
		return listeningServer != null;
	}
	
	public void searchPeers() {
		System.out.println("[" + id + "] Searching peers...");
		try {
			byte[] ip = InetAddress.getLocalHost().getAddress();
			for (short i = 1; i <= 254; i++) {
				ip[3] = (byte) i;
				final InetAddress address = InetAddress.getByAddress(ip);
				new Thread(() -> {
					try {
						if (address.isReachable(5000))
							searchPeers(address);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}).start();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void searchPeers(InetAddress address) {
		for (int p = mainPort; p <= maxPort; p++)
			connectTo(address, p);
	}
	
	public boolean connectTo(int port) {
		try {
			return connectTo(InetAddress.getLocalHost(), port);
		} catch (UnknownHostException e) {
			return false;
		}
	}
	
	public boolean connectTo(InetAddress address, int port) {
		try {
			Socket socket = new Socket(address, port);
			onAcceptSocket(socket);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	private void onAcceptSocket(Socket socket) {
		try {
			ChatConnection connection = new ChatConnection(socket, getId());
			connection.addOpenListener(() -> onJoin(connection));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void onJoin(ChatConnection connection) {
		if (connection.getId().equals(getId())) {
			connection.close();
			System.out.println("[" + id + "] Can connect to itself");
			return;
		}
		if (connections.containsKey(connection.getId())) {
			connection.close();
			System.out.println("[" + id + "] Connection already etablished");
			return;
		}
		connections.put(connection.getId(), connection);
		System.out.println("[" + id + "] " + connection.getId() + " joins");
		connection.addMessageListener(m -> onMessage(connection, m));
		connection.addCloseListener(() -> onLeave(connection));
		joinListeners.forEach(l -> l.accept(connection));
	}
	
	private void onMessage(ChatConnection connection, String message) {
		messageListeners.forEach(l -> l.accept(connection, message));
	}
	
	private void onLeave(ChatConnection connection) {
		connections.remove(connection.getId());
		System.out.println("[" + id + "] " + connection.getId() + " leaves");
		leaveListeners.forEach(l -> l.accept(connection));
	}
	
	public String getId() {
		return id;
	}
	
	public Collection<ChatConnection> getConnections() {
		return connections.values();
	}
	
	public void addJoinListener(Consumer<ChatConnection> listener) {
		joinListeners.add(listener);
	}
	
	public void addLeaveListener(Consumer<ChatConnection> listener) {
		leaveListeners.add(listener);
	}
	
	public void addMessageListener(BiConsumer<ChatConnection, String> listener) {
		messageListeners.add(listener);
	}
	
	public String generateRandomId(int length) {
		String id = "";
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random rdm = new Random();
		for (int i = 0; i < length; i++) {
			id += characters.charAt(rdm.nextInt(characters.length()));
		}
		return id;
	}
	
}
