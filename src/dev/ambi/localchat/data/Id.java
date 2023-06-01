package dev.ambi.localchat.data;

import java.io.Serializable;
import java.util.Random;

public class Id implements Serializable {
	
	private static final long serialVersionUID = 1L;
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
	
	@Override
	public int hashCode() {
		return value;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj || (obj instanceof Id && ((Id)obj).value == value);
	}
	
}
