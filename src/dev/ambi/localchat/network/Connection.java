package dev.ambi.localchat.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Connection {
	
	private Socket socket;
	private Thread thread;
	private PrintWriter writer;
	private BufferedReader reader;
	
	private ArrayList<Consumer<String>> messageListeners = new ArrayList<>();
	private ArrayList<Runnable> closeListeners = new ArrayList<>();
	
	public Connection(Socket socket) throws IOException {
		this.socket = socket;
		writer = new PrintWriter(socket.getOutputStream());
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		thread = new Thread(() -> {
			try {
				String line;
				while (!socket.isClosed() && (line = reader.readLine()) != null) {
					final String message = line;
					for (int i = 0; i < messageListeners.size(); i++)
						messageListeners.get(i).accept(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			close();
		});
		thread.start();
	}
	
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
		closeListeners.forEach(l -> l.run());
	}
	
	public void addMessageListener(Consumer<String> listener) {
		messageListeners.add(listener);
	}
	
	public void addCloseListener(Runnable listener) {
		closeListeners.add(listener);
	}
	
}
