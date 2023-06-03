package dev.ambi.localchat.ui;

import javax.swing.JPanel;
import dev.ambi.localchat.ImageLoader;
import dev.ambi.localchat.data.Message;
import dev.ambi.localchat.data.ImageMessage;
import dev.ambi.localchat.data.TextMessage;

import javax.swing.JLabel;
import javax.swing.BoxLayout;

import java.awt.Font;
import java.awt.Color;

public class MessagePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel contentLabel;
	private JLabel senderLabel;

	/**
	 * Create the panel.
	 */
	public MessagePanel(Message message, int maxWidth, int maxHeight) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		senderLabel = new JLabel("sender");
		senderLabel.setFont(new Font("Dialog", Font.BOLD, 10));
		add(senderLabel);
		
		contentLabel = new JLabel("content");
		add(contentLabel);
		senderLabel.setText(message.getSender().toString());
		senderLabel.setForeground(new Color(message.getSender().getColor()));
		if (message instanceof ImageMessage) {
			contentLabel.setIcon(ImageLoader.resize(((ImageMessage)message).getImage(), maxWidth, maxHeight));
			contentLabel.setText("");
		} else if (message instanceof TextMessage) {
			contentLabel.setIcon(null);
			contentLabel.setText(((TextMessage)message).getContent());
		}
	}
}
