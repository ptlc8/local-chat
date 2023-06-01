package dev.ambi.localchat.network;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import dev.ambi.localchat.data.Id;

public class IdentifiedConnection extends Connection {
	
	private Id id = null;
	private ArrayList<Runnable> openListeners = new ArrayList<>();

	public IdentifiedConnection(Socket socket, Id idToSend) throws IOException {
		super(socket);
		Consumer<Object> listener = data -> {
			if (id != null) return;
			if (data instanceof Id) {
				IdentifiedConnection.this.id = (Id) data;
				openListeners.forEach(l -> l.run());
			} else {
				close();
				System.out.println("Not a localchat socket");
				return;
			}
		};
		addDataListener(listener);
		send(idToSend);
	}

	public Id getId() {
		return id;
	}
	
	public void addOpenListener(Runnable listener) {
		openListeners.add(listener);
	}

}
