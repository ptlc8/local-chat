package dev.ambi.localchat.ui;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import dev.ambi.localchat.data.Client;
import dev.ambi.localchat.data.User;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

public class Window {

	private JFrame frame;
	private Client client;
	private DefaultListModel<User> connectionsModel = new DefaultListModel<>();
	private static final String NAME = "Local chat";

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
		String username = JOptionPane.showInputDialog(new JFrame(), "Choose a username", NAME, JOptionPane.QUESTION_MESSAGE);
		client = new Client(8686, username);
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
		frame.setTitle(NAME + " - " + client.getSelfUser().getName());
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
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
		
		JButton searchUsersBtn = new JButton("Search users");
		searchUsersBtn.addActionListener(e -> {
			client.getP2p().searchPeers();
		});
		horizontalBox.add(searchUsersBtn);
		
		UserPanel userPanel = new UserPanel(null);
		
		connectionsModel.addAll(client.getUsers());
		JList<User> usersList = new JList<>(connectionsModel);
		usersList.setPreferredSize(new Dimension(100, 100));
		usersList.addListSelectionListener(e -> {
			userPanel.setUser(usersList.getSelectedValue());
		});
		usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollPane = new JScrollPane(usersList);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane, BorderLayout.WEST);
		
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
