package dev.ambi.localchat.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class ListeningServer extends ServerSocket {
	
	Thread acceptThread = null;
	
	public ListeningServer(int port) throws IOException {
		super(port);
		acceptThread = new Thread(() -> {
			try {
				while (!isClosed()) {
					onConnection(accept());
				}
			} catch (IOException e) {
				// onclose
			}
		}, "Listening sockets thread");
		acceptThread.start();
	}
	
	@Override
	public void close() throws IOException {
		if (acceptThread != null)
			acceptThread.interrupt();
		super.close();
	}
	
	public abstract void onConnection(Socket socket);

}
