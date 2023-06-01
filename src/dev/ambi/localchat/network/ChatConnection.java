package dev.ambi.localchat.network;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import dev.ambi.localchat.data.Id;

public class ChatConnection extends Connection {
	
	private Id id = null;
	private ArrayList<Runnable> openListeners = new ArrayList<>();

	public ChatConnection(Socket socket, Id idToSend) throws IOException {
		super(socket);
		addMessageListener(m -> {
			if (!m.startsWith("#id#")) {
				close();
				System.out.println("Not a localchat socket");
				return;
			}
			ChatConnection.this.id = new Id(m.substring(4));
			openListeners.forEach(l -> l.run());
		});
		send("#id#" + idToSend);
	}

	public Id getId() {
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
