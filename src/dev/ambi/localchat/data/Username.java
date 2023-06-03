package dev.ambi.localchat.data;

import java.io.Serializable;

public class Username implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String text;
	
	public Username(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	public int getColor() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String toString() {
		return text;
	}

}
