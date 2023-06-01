package dev.ambi.localchat.network;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientTest {
	
	final ArrayList<Client> clients = new ArrayList<>();
	final int count = 3;
	
	@BeforeEach
	void setUp() throws InterruptedException {
		for (int i = 0; i < count; i++) {
			Client client = new Client(8686);
			client.startListening();
			client.searchPeers();
			clients.add(client);
		}
		Thread.sleep(1000);
	}

	@Test
	void testCount() throws InterruptedException {
		for (Client c : clients)
			assertEquals(count - 1, c.getConnections().size());
	}

}
