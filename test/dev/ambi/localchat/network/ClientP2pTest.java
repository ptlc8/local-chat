package dev.ambi.localchat.network;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientP2pTest {
	
	final ArrayList<ClientP2p> clientP2ps = new ArrayList<>();
	final int count = 3;
	
	@BeforeEach
	void setUp() throws InterruptedException {
		for (int i = 0; i < count; i++) {
			ClientP2p clientP2p = new ClientP2p(37000);
			clientP2p.startListening();
			clientP2p.searchPeers();
			clientP2ps.add(clientP2p);
		}
		Thread.sleep(1000);
	}

	@Test
	void testCount() throws InterruptedException {
		for (ClientP2p c : clientP2ps)
			assertEquals(count - 1, c.getUsers().size());
	}

}
