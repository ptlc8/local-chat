package dev.ambi.localchat.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

import dev.ambi.localchat.data.Client;
import dev.ambi.localchat.data.User;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class Window {

	private JFrame frame;
	private final JCheckBox listeningCheckbox = new JCheckBox("Listening");
	private JTextField messageField;
	private Client client;
	private DefaultListModel<User> connectionsModel = new DefaultListModel<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window window = new Window();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
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
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Local chat - " + client.getSelfUser());
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Box horizontalBox = Box.createHorizontalBox();
		frame.getContentPane().add(horizontalBox, BorderLayout.NORTH);
		listeningCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (listeningCheckbox.isSelected())
					listeningCheckbox.setSelected(client.getP2p().startListening());
				else
					client.getP2p().stopListening();
			}
		});
		listeningCheckbox.setSelected(client.getP2p().isListening());
		horizontalBox.add(listeningCheckbox);
		
		JButton searchPeersBtn = new JButton("Search peers");
		searchPeersBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.getP2p().searchPeers();
			}
		});
		horizontalBox.add(searchPeersBtn);
		
		Box verticalBox = Box.createVerticalBox();
		frame.getContentPane().add(verticalBox, BorderLayout.WEST);
		
		UserPanel userPanel = new UserPanel(null);
		
		connectionsModel.addAll(client.getUsers());
		JList<User> usersList = new JList<>(connectionsModel);
		usersList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				userPanel.setUser(usersList.getSelectedValue());
			}
		});
		usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		verticalBox.add(usersList);
		
		userPanel.setUser(usersList.getSelectedValue());
		frame.getContentPane().add(userPanel, BorderLayout.CENTER);
		
		messageField = new JTextField();
		messageField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (client.sendMessage(usersList.getSelectedValue(), messageField.getText())) {
					messageField.setText("");
				}
			}
		});
		frame.getContentPane().add(messageField, BorderLayout.SOUTH);
		messageField.setColumns(10);
	}
	
	private void onJoin(User user) {
		connectionsModel.addElement(user);
	}
	
	private void onLeave(User user) {
		connectionsModel.removeElement(user);
	}

}
