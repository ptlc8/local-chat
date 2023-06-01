package dev.ambi.localchat.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class Connection {
	
	private Socket socket;
	private Thread thread;
	private PrintWriter writer;
	private BufferedReader reader;
	private String id = null;
	
	public Connection(Socket socket, String clientId) throws IOException, LocalchatException {
		this.socket = socket;
		writer = new PrintWriter(socket.getOutputStream());
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer.write("#id#" + clientId + "\n");
		writer.flush();
		String firstLine = reader.readLine();
		if (!firstLine.startsWith("#id#"))
			throw new LocalchatException("Not a localchat socket");
		id = firstLine.substring(4);
		thread = new Thread(() -> {
			while (socket.isConnected()) {
				try {
					String line = reader.readLine();
					if (line != null) {
						onMessage(line);
					} else {
						close();
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	public abstract void onMessage(String message);
	
	public abstract void onClose();
	
	public void send(String message) {
		writer.println(message);
		writer.flush();
	}
	
	public void close() {
		thread.interrupt();
		writer.close();
		try {
			reader.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		onClose();
	}
	
	@Override
	public String toString() {
		return Integer.toHexString(hashCode()) + "@" + socket.getInetAddress();
	}
	
	public String getId() {
		return id;
	}
	
}
