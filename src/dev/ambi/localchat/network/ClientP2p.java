package dev.ambi.localchat.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;

public class ClientP2p {
	
	private final int mainPort;
	private final int maxPort;
	private ListeningServer listeningServer = null;
	private HashSet<Connection> connections = new HashSet<>();
	
	private ArrayList<Consumer<Connection>> joinListeners = new ArrayList<>();
	private ArrayList<Consumer<Connection>> leaveListeners = new ArrayList<>();
	
	public ClientP2p(int port) {
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
			System.out.println("[" + hashCode() + "] Listening on port " + port);
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
		System.out.println("[" + hashCode() + "] Stop listening");
	}
	
	public boolean isListening() {
		return listeningServer != null;
	}
	
	public void searchPeers() {
		System.out.println("[" + hashCode() + "] Searching peers...");
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
				}, "Search peers thread").start();
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
			Connection connection = new Connection(socket);
			connections.add(connection);
			System.out.println("[" + hashCode() + "] " + connection.hashCode() + " joins");
			connection.addCloseListener(() -> onLeave(connection));
			joinListeners.forEach(l -> l.accept(connection));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void onLeave(Connection connection) {
		connections.remove(connection);
		System.out.println("[" + hashCode() + "] " + connection.hashCode() + " leaves");
		leaveListeners.forEach(l -> l.accept(connection));
	}
	
	public HashSet<Connection> getConnections() {
		return connections;
	}
	
	public void addJoinListener(Consumer<Connection> listener) {
		joinListeners.add(listener);
	}
	
	public void addLeaveListener(Consumer<Connection> listener) {
		leaveListeners.add(listener);
	}
	
}
