package dev.ambi.localchat.ui;

import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import dev.ambi.localchat.data.Message;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.BoxLayout;

import java.awt.Component;
import java.awt.Font;
import java.awt.Color;

public class MessageRenderer extends JPanel implements ListCellRenderer<Message> {
	private static final long serialVersionUID = 1L;
	private JLabel lblMessage;
	private JLabel lblSender;

	/**
	 * Create the panel.
	 */
	public MessageRenderer() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		lblSender = new JLabel("sender");
		lblSender.setFont(new Font("Dialog", Font.BOLD, 10));
		add(lblSender);
		
		lblMessage = new JLabel("content");
		add(lblMessage);

	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Message> list, Message value, int index, boolean isSelected, boolean cellHasFocus) {
		lblMessage.setText(value.getContent());
		lblSender.setText(value.getSender().toString());
		lblSender.setForeground(new Color(value.getSender().getId().hashCode()));
		return this;
	}
}
