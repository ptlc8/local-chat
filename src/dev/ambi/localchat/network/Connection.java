package dev.ambi.localchat.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Connection {

	private static byte ONLY_TAG = 0;
	private static byte OBJECT = 1;
	private static byte DATA = 2;
	
	private Socket socket;
	private Thread thread;
	private ObjectOutputStream writer;

	private ArrayList<BiConsumer<Byte, Object>> objectListeners = new ArrayList<>();
	private Function<Byte, OutputStream> dataListener = b -> OutputStream.nullOutputStream();
	private ArrayList<Runnable> closeListeners = new ArrayList<>();
	
	public Connection(Socket socket) throws IOException {
		this.socket = socket;
		writer = new ObjectOutputStream(socket.getOutputStream());
		thread = new Thread(() -> {
			try {
				ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
				while (!socket.isClosed()) {
					byte tag = (byte) reader.read();
					int type = reader.read();
					if (type == OBJECT) {
						final Object data = reader.readObject();
						for (int i = 0; i < objectListeners.size(); i++)
							objectListeners.get(i).accept(tag, data);
					} else if (type == DATA) {
						OutputStream dataOutput = dataListener.apply(tag);
						byte[] chunk = new byte[256 * 256];
						while (true) {
							int size = reader.readShort();
							if (size <= 0) break;
							int count;
							for (int l = 0; l < size && (count = reader.read(chunk, 0, size - l)) != -1; l += count) {
								dataOutput.write(chunk, 0, count);
							}
						}
					} else if (type == ONLY_TAG) {
						for (int i = 0; i < objectListeners.size(); i++)
							objectListeners.get(i).accept(tag, null);
					} else if (type == -1)
						break;
				}
			} catch (EOFException | OptionalDataException e) {
				close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				close();
			}
			closeListeners.forEach(l -> l.run());
		}, "Connection reader thread");
		thread.start();
	}
	
	public <T extends Serializable> boolean sendObject(byte tag, T data) {
		if (data == null) return false;
		if (socket.isClosed())
			return false;
		try {
			synchronized (writer) {
				writer.write(tag);
				writer.write(OBJECT);
				writer.writeObject(data);
				writer.flush();
				return true;
			}
		} catch (SocketException e) {
			close();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean sendData(byte tag, InputStream data) {
		if (socket.isClosed())
			return false;
		synchronized (writer) {
			try {
				byte[] chunk = new byte[256 * 256];
				int count;
				writer.write(tag);
				writer.write(DATA);
				while (true) {
					count = data.read(chunk);
					if (count == 0) continue;
					writer.writeShort(count < 0 ? 0 : count);
					if (count < 0) break;
					writer.write(chunk, 0, count);
					writer.flush();
				}
				writer.flush();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	
	public boolean send(byte tag) {
		if (socket.isClosed())
			return false;
		synchronized (writer) {
			try {
				writer.write(tag);
				writer.write(ONLY_TAG);
				writer.flush();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addObjectListener(BiConsumer<Byte, Object> listener) {
		objectListeners.add(listener);
	}
	
	public void setDataListener(Function<Byte, OutputStream> listener) {
		dataListener = listener;
	}

	public void addCloseListener(Runnable listener) {
		closeListeners.add(listener);
	}
	
	public boolean isClosed() {
		return socket.isClosed();
	}
	
}
