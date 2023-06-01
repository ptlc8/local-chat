package dev.ambi.localchat.network;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ChatConnection extends Connection {
	
	private String id = null;
	private ArrayList<Runnable> openListeners = new ArrayList<>();

	public ChatConnection(Socket socket, String idToSend) throws IOException {
		super(socket);
		addMessageListener(m -> {
			if (!m.startsWith("#id#")) {
				close();
				System.out.println("Not a localchat socket");
				return;
			}
			ChatConnection.this.id = m.substring(4);
			openListeners.forEach(l -> l.run());
		});
		send("#id#" + idToSend);
	}

	public String getId() {
		return id;
	}
	
	public void addOpenListener(Runnable listener) {
		openListeners.add(listener);
	}
	
	@Override
	public String toString() {
		return "Chat " + id;
	}

}
