package dev.ambi.localchat.ui;

import javax.swing.JPanel;

import dev.ambi.localchat.data.Message;
import dev.ambi.localchat.data.User;
import dev.ambi.localchat.files.Files;
import dev.ambi.localchat.files.Images;

import java.util.function.Consumer;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.border.LineBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.BoxLayout;

public class UserPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel messagesPanel;
	private User user = null;
	private Consumer<Message> onMessage;
	private JTextField messageField;
	private JButton imageBtn, fileBtn;

	/**
	 * Create the panel.
	 */
	public UserPanel(User user) {
		setLayout(new BorderLayout(0, 0));
		
		messagesPanel = new JPanel();
		messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
		messagesPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		JScrollPane scrollPane = new JScrollPane(messagesPanel);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane);
		
		Box horizontalBox = Box.createHorizontalBox();
		add(horizontalBox, BorderLayout.SOUTH);
		
		messageField = new JTextField();
		horizontalBox.add(messageField);
		messageField.addActionListener(e -> {
			if (this.user != null && this.user.sendMessage(messageField.getText())) {
				messageField.setText("");
			}
		});
		messageField.setColumns(10);
		
		imageBtn = new JButton("Image");
		imageBtn.addActionListener(e -> {
			ImageIcon image = Images.prompt();
			if (image != null)
				this.user.sendImage(image);
		});
		horizontalBox.add(imageBtn);
		
		fileBtn = new JButton("File");
		fileBtn.addActionListener(e -> {
			String path = Files.promptPath();
			if (path != null)
				this.user.sendFileOffer(path);
		});
		horizontalBox.add(fileBtn);
		
		onMessage = m -> {
			messagesPanel.add(new MessagePanel(m, messagesPanel.getWidth(), messagesPanel.getHeight()));
			validate();
		};
		setUser(user);
	}
	
	public void setUser(User user) {
		if (this.user != null)
			this.user.removeMessageListener(this.onMessage);
		messagesPanel.removeAll();
		
		this.user = user;
		messageField.setEnabled(user != null);
		imageBtn.setEnabled(user != null);
		fileBtn.setEnabled(user != null);
		
		if (user != null) {
			for (Message m : user.getMessages())
				messagesPanel.add(new MessagePanel(m, messagesPanel.getWidth(), messagesPanel.getHeight()));
			user.addMessageListener(this.onMessage);
		}
		
		validate();
	}
}
