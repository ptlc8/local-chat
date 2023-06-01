package dev.ambi.localchat.ui;

import javax.swing.JPanel;
import dev.ambi.localchat.data.Message;
import dev.ambi.localchat.data.User;

import javax.swing.JList;

import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JScrollPane;

public class UserPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private DefaultListModel<Message> messagesModel = new DefaultListModel<>();
	private User user = null;
	private Consumer<Message> onMessage;

	/**
	 * Create the panel.
	 */
	public UserPanel(User user) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JList<Message> list = new JList<>(messagesModel);
		list.setBorder(new LineBorder(new Color(0, 0, 0)));
		list.setCellRenderer(new MessageRenderer());
		
		JScrollPane scrollPane = new JScrollPane(list);
		add(scrollPane);
		
		onMessage = m -> messagesModel.addElement(m);
		setUser(user);
	}
	
	public void setUser(User user) {
		if (this.user != null)
			this.user.removeMessageListener(this.onMessage);
		messagesModel.clear();
		
		this.user = user;
		if (user == null) return;
		
		messagesModel.addAll(user.getMessages());
		user.addMessageListener(this.onMessage);
	}
	
}