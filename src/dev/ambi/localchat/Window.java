package dev.ambi.localchat;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import dev.ambi.localchat.network.Client;
import dev.ambi.localchat.network.Connection;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

public class Window {

	private JFrame frmLocalChat;
	private final JCheckBox chckbxListening = new JCheckBox("Listening");
	private JTextField textField;
	private Client client;
	private DefaultListModel<Connection> connectionsModel = new DefaultListModel<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window window = new Window();
					window.frmLocalChat.setVisible(true);
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
		client.startListening();
		client.searchPeers();
		initialize();
		client.addJoinListener(c -> connectionsModel.addElement(c));
		client.addLeaveListener(c -> connectionsModel.removeElement(c));
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmLocalChat = new JFrame();
		frmLocalChat.setTitle("Local chat - " + client.getId());
		frmLocalChat.setBounds(100, 100, 450, 300);
		frmLocalChat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Box horizontalBox = Box.createHorizontalBox();
		frmLocalChat.getContentPane().add(horizontalBox, BorderLayout.NORTH);
		chckbxListening.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxListening.isSelected())
					chckbxListening.setSelected(client.startListening());
				else
					client.stopListening();
			}
		});
		chckbxListening.setSelected(client.isListening());
		horizontalBox.add(chckbxListening);
		
		JButton btnSearchPeers = new JButton("Search peers");
		btnSearchPeers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.searchPeers();
			}
		});
		horizontalBox.add(btnSearchPeers);
		
		Box verticalBox = Box.createVerticalBox();
		frmLocalChat.getContentPane().add(verticalBox, BorderLayout.WEST);
		
		connectionsModel.addAll(client.getConnections());
		JList<Connection> list = new JList<>(connectionsModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		verticalBox.add(list);
		
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		frmLocalChat.getContentPane().add(textArea, BorderLayout.CENTER);
		
		textField = new JTextField();
		frmLocalChat.getContentPane().add(textField, BorderLayout.SOUTH);
		textField.setColumns(10);
	}

}
