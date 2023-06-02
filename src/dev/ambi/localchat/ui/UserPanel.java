package dev.ambi.localchat.ui;

import javax.swing.JPanel;

import dev.ambi.localchat.ImageLoader;
import dev.ambi.localchat.data.Message;
import dev.ambi.localchat.data.User;

import java.util.function.Consumer;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.border.LineBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.ScrollPaneConstants;

public class UserPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private DefaultListModel<Message> messagesModel = new DefaultListModel<>();
	private User user = null;
	private Consumer<Message> onMessage;
	private JTextField messageField;
	private JButton imageBtn;

	/**
	 * Create the panel.
	 */
	public UserPanel(User user) {
		setLayout(new BorderLayout(0, 0));
		
		JList<Message> list = new JList<>(messagesModel);
		list.setBorder(new LineBorder(new Color(0, 0, 0)));
		list.setCellRenderer(new MessageRenderer());
		
		JScrollPane scrollPane = new JScrollPane(list);
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
			if (this.user != null)
				this.user.sendImage(ImageLoader.prompt());
		});
		horizontalBox.add(imageBtn);
		
		onMessage = m -> messagesModel.addElement(m);
		setUser(user);
	}
	
	public void setUser(User user) {
		if (this.user != null)
			this.user.removeMessageListener(this.onMessage);
		messagesModel.clear();
		
		this.user = user;
		messageField.setEnabled(user != null);
		imageBtn.setEnabled(user != null);
		if (user == null) return;
		
		messagesModel.addAll(user.getMessages());
		user.addMessageListener(this.onMessage);
	}
}
