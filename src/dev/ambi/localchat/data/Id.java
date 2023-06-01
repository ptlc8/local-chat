package dev.ambi.localchat.data;

import java.util.Random;

public class Id {
	
	private int value;
	
	public Id() {
		value = new Random().nextInt();
	}
	
	public Id(String hex) {
		value = Integer.parseInt(hex, 16);
	}
	
	@Override
	public String toString() {
		return String.format("%8X", value);
	}

}
