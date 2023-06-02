package dev.ambi.localchat.ui;

import javax.swing.ListCellRenderer;

import dev.ambi.localchat.data.Message;

import javax.swing.JList;
import java.awt.Component;

public class MessageRenderer implements ListCellRenderer<Message> {

	@Override
	public Component getListCellRendererComponent(JList<? extends Message> list, Message value, int index, boolean isSelected, boolean cellHasFocus) {
		return new MessagePanel(value, list.getWidth(), list.getHeight());
	}
}
