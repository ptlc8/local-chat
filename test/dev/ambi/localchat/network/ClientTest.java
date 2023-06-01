package dev.ambi.localchat.network;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.ambi.localchat.data.Client;

class ClientTest {
	
	final ArrayList<Client> clients = new ArrayList<>();
	final int count = 6;
	
	@BeforeEach
	void setUp() throws Exception {
		for (int i = 0; i < count; i++) {
			Client client = new Client(37000);
			clients.add(client);
		}
		Thread.sleep(1000);
	}

	@Test
	void testConnected() {
		for (Client a : clients)
			for (Client b : clients)
				if (!a.getSelfUser().equals(b.getSelfUser()))
					assertNotNull(a.getUser(b.getSelfUser().getId()));
	}
	
	@Test
	void testMessage() throws InterruptedException {
		Client a = clients.get(0);
		Client b = clients.get(1);
		a.sendMessage(a.getUser(b.getSelfUser().getId()), "HEY");
		Thread.sleep(500);
		assertEquals("HEY", b.getUser(a.getSelfUser().getId()).getMessages().get(0).getContent());
	}

}
