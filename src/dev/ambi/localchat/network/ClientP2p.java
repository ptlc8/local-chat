package dev.ambi.localchat.network;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;

import dev.ambi.localchat.data.Id;

public class ClientP2p {
	
	private Id id;
	private final int mainPort;
	private final int maxPort;
	private ListeningServer listeningServer = null;
	private HashMap<Id, IdentifiedConnection> connections = new HashMap<>();
	
	private ArrayList<Consumer<IdentifiedConnection>> joinListeners = new ArrayList<>();
	private ArrayList<Consumer<IdentifiedConnection>> leaveListeners = new ArrayList<>();
	
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
		connection.addCloseListener(() -> onLeave(connection));
		joinListeners.forEach(l -> l.accept(connection));
	}
	
	private void onLeave(IdentifiedConnection connection) {
		connections.remove(connection.getId());
		System.out.println("[" + id + "] " + connection.getId() + " leaves");
		leaveListeners.forEach(l -> l.accept(connection));
	}
	
	
	public Id getId() {
		return id;
	}
	
	public Set<Id> getIds() {
		return connections.keySet();
	}
	
	public void addJoinListener(Consumer<IdentifiedConnection> listener) {
		joinListeners.add(listener);
	}
	
	public void addLeaveListener(Consumer<IdentifiedConnection> listener) {
		leaveListeners.add(listener);
	}
	
	public boolean send(Id id, Serializable data) {
		if (!connections.containsKey(id))
			return false;
		return connections.get(id).send(data);
	}
	
}
