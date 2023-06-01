package dev.ambi.localchat.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Client {
	
	private int port;
	private ListeningServer listeningServer = null;
	private HashMap<String, Connection> connections = new HashMap<>();
	
	private ArrayList<Consumer<Connection>> joinListeners = new ArrayList<>();
	private ArrayList<Consumer<Connection>> leaveListeners = new ArrayList<>();
	private ArrayList<BiConsumer<Connection, String>> messageListeners = new ArrayList<>();
	
	public Client(int port) {
		this.port = port;
	}
	
	public boolean startListening() {
		try {
			if (listeningServer != null && !listeningServer.isClosed()) {
				if (listeningServer.getLocalPort() == port)
					return true;
				listeningServer.close();
			}
			listeningServer = new ListeningServer(port) {
				@Override
				public void onConnection(Socket socket) {
					onJoin(socket);
				}
			};
			System.out.println("Listening on port " + port);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
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
		System.out.println("Stop listening");
	}
	
	public void searchPeers() {
		System.out.println("Searching peers...");
		try {
			byte[] ip = InetAddress.getLocalHost().getAddress();
			for (short i = 1; i <= 254; i++) {
				ip[3] = (byte) i;
				final InetAddress address = InetAddress.getByAddress(ip);
				new Thread(() -> {
					try {
						if (address.isReachable(5000)) {
							connectTo(address);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}).start();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public boolean connectTo(InetAddress address) {
		System.out.println("Trying to connect to " + address + "...");
		try {
			Socket socket = new Socket(address, port);
			onJoin(socket);
			return true;
		} catch (IOException e) {
			return false;
		}
		
	}
	
	private void onJoin(Socket socket) {
		try {
			Connection connection = new Connection(socket, getId()) {
				@Override
				public void onMessage(String message) {
					Client.this.onMessage(this, message);
				}
				@Override
				public void onClose() {
					Client.this.onLeave(this);
				}
			};
			if (connections.containsKey(connection.getId())) {
				System.out.println("Connection already etablished");
				return;
			}
			connections.put(connection.getId(), connection);
			System.out.println(connection + " joins");
			joinListeners.forEach(l -> l.accept(connection));
		} catch (IOException | LocalchatException e) {
			e.printStackTrace();
		}
	}
	
	private void onMessage(Connection connection, String message) {
		
		messageListeners.forEach(l -> l.accept(connection, message));
	}
	
	private void onLeave(Connection connection) {
		connections.remove(connection.getId());
		System.out.println(connection + " leaves");
		leaveListeners.forEach(l -> l.accept(connection));
	}
	
	public String getId() {
		return Integer.toHexString(hashCode());
	}
	
	public Collection<Connection> getConnections() {
		return connections.values();
	}
	
	public void addJoinListener(Consumer<Connection> listener) {
		joinListeners.add(listener);
	}
	
	public void addLeaveListener(Consumer<Connection> listener) {
		leaveListeners.add(listener);
	}
	
	public void addMessageListener(BiConsumer<Connection, String> listener) {
		messageListeners.add(listener);
	}
	
}
