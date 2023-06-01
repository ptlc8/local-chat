package dev.ambi.localchat.network;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import dev.ambi.localchat.data.Id;

public class ClientP2p {
	
	private Id id;
	private final int mainPort;
	private final int maxPort;
	private ListeningServer listeningServer = null;
	private HashMap<Id, IdentifiedConnection> connections = new HashMap<>();
	
	private ArrayList<Consumer<Id>> joinListeners = new ArrayList<>();
	private ArrayList<Consumer<Id>> leaveListeners = new ArrayList<>();
	private HashMap<Class<? extends Serializable>, ArrayList<BiConsumer<Id, Object>>> dataListeners = new HashMap<>();
	
	public ClientP2p(int port) {
		this.id = new Id();
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
			IdentifiedConnection connection = new IdentifiedConnection(socket, id);
			connection.addOpenListener(() -> onJoin(connection));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void onJoin(IdentifiedConnection connection) {
		if (connection.getId().equals(id)) {
			connection.close();
			System.out.println("[" + id + "] Can't connect to itself");
			return;
		}
		if (connections.containsKey(connection.getId())) {
			connection.close();
			System.out.println("[" + id + "] Connection already etablished");
			return;
		}
		connections.put(connection.getId(), connection);
		System.out.println("[" + id + "] " + connection.getId() + " joins");
		connection.addDataListener(d -> onData(connection, d));
		connection.addCloseListener(() -> onLeave(connection));
		joinListeners.forEach(l -> l.accept(connection.getId()));
	}
	
	private void onData(IdentifiedConnection connection, Object data) {
		ArrayList<BiConsumer<Id, Object>> listeners = dataListeners.get(data.getClass());
		if (listeners == null) return;
		listeners.forEach(l -> l.accept(connection.getId(), data));
	}
	
	private void onLeave(IdentifiedConnection connection) {
		connections.remove(connection.getId());
		System.out.println("[" + id + "] " + connection.getId() + " leaves");
		leaveListeners.forEach(l -> l.accept(connection.getId()));
	}
	
	
	public Id getId() {
		return id;
	}
	
	public Set<Id> getUsers() {
		return connections.keySet();
	}
	
	public void addJoinListener(Consumer<Id> listener) {
		joinListeners.add(listener);
	}
	
	public void addLeaveListener(Consumer<Id> listener) {
		leaveListeners.add(listener);
	}
	
	public <T extends Serializable> void addDataListener(Class<T> clazz, BiConsumer<Id, Object> listener) {
		if (!dataListeners.containsKey(clazz))
			dataListeners.put(clazz, new ArrayList<>());
		dataListeners.get(clazz).add(listener);
	}
	
	public boolean send(Id id, Serializable data) {
		if (!connections.containsKey(id))
			return false;
		try {
			connections.get(id).send(data);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
