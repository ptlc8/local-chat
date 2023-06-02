package dev.ambi.localchat.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import dev.ambi.localchat.data.Client;
import dev.ambi.localchat.data.User;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

public class Window {

	private JFrame frame;
	private Client client;
	private DefaultListModel<User> connectionsModel = new DefaultListModel<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				Window window = new Window();
				window.frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Window() {
		client = new Client(8686);
		initialize();
		client.addJoinListener(this::onJoin);
		client.addLeaveListener(this::onLeave);
	}

	/**
	 * Initialize the contents of the frame.
	 * @wbp.parser.entryPoint
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Local chat - " + client.getSelfUser());
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Box horizontalBox = Box.createHorizontalBox();
		frame.getContentPane().add(horizontalBox, BorderLayout.NORTH);
		JCheckBox listeningCheckbox = new JCheckBox("Listening");
		listeningCheckbox.addActionListener(e -> {
			if (listeningCheckbox.isSelected())
				listeningCheckbox.setSelected(client.getP2p().startListening());
			else
				client.getP2p().stopListening();
		});
		listeningCheckbox.setSelected(client.getP2p().isListening());
		horizontalBox.add(listeningCheckbox);
		
		JButton searchPeersBtn = new JButton("Search peers");
		searchPeersBtn.addActionListener(e -> {
			client.getP2p().searchPeers();
		});
		horizontalBox.add(searchPeersBtn);
		
		Box verticalBox = Box.createVerticalBox();
		frame.getContentPane().add(verticalBox, BorderLayout.WEST);
		
		UserPanel userPanel = new UserPanel(null);
		
		connectionsModel.addAll(client.getUsers());
		JList<User> usersList = new JList<>(connectionsModel);
		usersList.addListSelectionListener(e -> {
			userPanel.setUser(usersList.getSelectedValue());
		});
		usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		verticalBox.add(usersList);
		
		userPanel.setUser(usersList.getSelectedValue());
		frame.getContentPane().add(userPanel, BorderLayout.CENTER);
	}
	
	private void onJoin(User user) {
		connectionsModel.addElement(user);
	}
	
	private void onLeave(User user) {
		connectionsModel.removeElement(user);
	}

}
