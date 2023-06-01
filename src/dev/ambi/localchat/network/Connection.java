package dev.ambi.localchat.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Connection {
	
	private Socket socket;
	private Thread thread;
	private ObjectOutputStream writer;
	private ObjectInputStream reader;
	
	private ArrayList<Consumer<Object>> dataListeners = new ArrayList<>();
	private ArrayList<Runnable> closeListeners = new ArrayList<>();
	
	public Connection(Socket socket) throws IOException {
		this.socket = socket;
		writer = new ObjectOutputStream(socket.getOutputStream());
		reader = new ObjectInputStream(socket.getInputStream());
		thread = new Thread(() -> {
			try {
				while (!socket.isClosed()) {
					final Object data = reader.readObject();
					for (int i = 0; i < dataListeners.size(); i++)
						dataListeners.get(i).accept(data);
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			close();
		});
		thread.start();
	}
	
	public void send(Object data) throws IOException {
		writer.writeObject(data);
		writer.flush();
	}
	
	public void close() {
		thread.interrupt();
		try {
			writer.close();
			reader.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		closeListeners.forEach(l -> l.run());
	}
	
	public void addDataListener(Consumer<Object> listener) {
		dataListeners.add(listener);
	}

	public void addCloseListener(Runnable listener) {
		closeListeners.add(listener);
	}
	
}
