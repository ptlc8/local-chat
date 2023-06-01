package dev.ambi.localchat;

import java.io.IOException;

import dev.ambi.localchat.network.Client;

public class Main {
	
	public static void main(String[] args) throws IOException {
		Client client = new Client(8686);
		//client.listen();
		client.searchPeers();
	}
	
}
