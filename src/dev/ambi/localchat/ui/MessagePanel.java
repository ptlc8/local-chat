package dev.ambi.localchat.ui;

import javax.swing.JPanel;

import dev.ambi.localchat.data.Message;
import dev.ambi.localchat.data.FileMessage;
import dev.ambi.localchat.data.ImageMessage;
import dev.ambi.localchat.data.TextMessage;
import dev.ambi.localchat.files.Images;

import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import java.awt.Font;
import java.awt.Color;

public class MessagePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JLabel contentLabel;
	private JLabel senderLabel;
	private JButton button;

	/**
	 * Create the panel.
	 */
	public MessagePanel(Message message, int maxWidth, int maxHeight) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		senderLabel = new JLabel("sender:");
		senderLabel.setText(message.getSender() + ":");
		senderLabel.setForeground(new Color(message.getSender().getColor()));
		add(senderLabel);
		
		contentLabel = new JLabel("content");
		contentLabel.setFont(new Font("Dialog", Font.BOLD, 15));
		add(contentLabel);
		
		button = new JButton("Download file");
		
		if (message instanceof ImageMessage) {
			contentLabel.setIcon(Images.resize(((ImageMessage) message).getImage(), maxWidth, maxHeight));
			contentLabel.setText("");
		} else if (message instanceof TextMessage) {
			contentLabel.setIcon(null);
			contentLabel.setText(((TextMessage) message).getContent());
		} else if (message instanceof FileMessage) {
			contentLabel.setText("<html><u>" + ((FileMessage) message).getFilename() + "</u></html>");
			contentLabel.setForeground(Color.BLUE);
			if (((FileMessage) message).isDownloadable()) {
				add(button);
				button.addActionListener(e -> {
					((FileMessage) message).acceptFileOffer();
				});
			}
		}
	}
}
