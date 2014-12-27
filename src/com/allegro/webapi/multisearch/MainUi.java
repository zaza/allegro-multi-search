package com.allegro.webapi.multisearch;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.xml.rpc.ServiceException;

@SuppressWarnings("serial")
public class MainUi extends JFrame {

	private JPanel contentPane;
	private JTextField loginField;
	private JPasswordField passwordField;
	private JTextField keyField;
	private JTextField queryField;
	private JTextArea textArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainUi frame = new MainUi();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainUi() {
		setTitle("Allegro Multi Search");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblLogin = new JLabel("login");
		GridBagConstraints gbc_lblLogin = new GridBagConstraints();
		gbc_lblLogin.insets = new Insets(0, 0, 5, 5);
		gbc_lblLogin.anchor = GridBagConstraints.EAST;
		gbc_lblLogin.gridx = 0;
		gbc_lblLogin.gridy = 0;
		contentPane.add(lblLogin, gbc_lblLogin);
		
		loginField = new JTextField();
		GridBagConstraints gbc_loginField = new GridBagConstraints();
		gbc_loginField.insets = new Insets(0, 0, 5, 0);
		gbc_loginField.fill = GridBagConstraints.HORIZONTAL;
		gbc_loginField.gridx = 1;
		gbc_loginField.gridy = 0;
		contentPane.add(loginField, gbc_loginField);
		loginField.setColumns(10);
		
		JLabel lblHaslo = new JLabel("hasło");
		GridBagConstraints gbc_lblHaslo = new GridBagConstraints();
		gbc_lblHaslo.anchor = GridBagConstraints.EAST;
		gbc_lblHaslo.insets = new Insets(0, 0, 5, 5);
		gbc_lblHaslo.gridx = 0;
		gbc_lblHaslo.gridy = 1;
		contentPane.add(lblHaslo, gbc_lblHaslo);
		
		passwordField = new JPasswordField();
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(0, 0, 5, 0);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 1;
		gbc_passwordField.gridy = 1;
		contentPane.add(passwordField, gbc_passwordField);
		
		JLabel lblKluczWebapi = new JLabel("klucz WebAPI");
		GridBagConstraints gbc_lblKluczWebapi = new GridBagConstraints();
		gbc_lblKluczWebapi.anchor = GridBagConstraints.EAST;
		gbc_lblKluczWebapi.insets = new Insets(0, 0, 5, 5);
		gbc_lblKluczWebapi.gridx = 0;
		gbc_lblKluczWebapi.gridy = 2;
		contentPane.add(lblKluczWebapi, gbc_lblKluczWebapi);
		
		keyField = new JTextField();
		GridBagConstraints gbc_keyField = new GridBagConstraints();
		gbc_keyField.insets = new Insets(0, 0, 5, 0);
		gbc_keyField.fill = GridBagConstraints.HORIZONTAL;
		gbc_keyField.gridx = 1;
		gbc_keyField.gridy = 2;
		contentPane.add(keyField, gbc_keyField);
		keyField.setColumns(10);
		
		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator.insets = new Insets(0, 0, 5, 0);
		gbc_separator.gridwidth = 2;
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 3;
		contentPane.add(separator, gbc_separator);
		
		JLabel lblCzegoSzukasz = new JLabel("czego szukasz?");
		GridBagConstraints gbc_lblCzegoSzukasz = new GridBagConstraints();
		gbc_lblCzegoSzukasz.anchor = GridBagConstraints.EAST;
		gbc_lblCzegoSzukasz.insets = new Insets(0, 0, 5, 5);
		gbc_lblCzegoSzukasz.gridx = 0;
		gbc_lblCzegoSzukasz.gridy = 4;
		contentPane.add(lblCzegoSzukasz, gbc_lblCzegoSzukasz);
		
		queryField = new JTextField();
		GridBagConstraints gbc_queryField = new GridBagConstraints();
		gbc_queryField.insets = new Insets(0, 0, 5, 0);
		gbc_queryField.fill = GridBagConstraints.HORIZONTAL;
		gbc_queryField.gridx = 1;
		gbc_queryField.gridy = 4;
		contentPane.add(queryField, gbc_queryField);
		queryField.setColumns(10);
		
		JButton searchButton = new JButton("szukaj");
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					PrintStream out = new PrintStream(new TextAreaOutputStream(textArea));
					Main.search(loginField.getText(), new String(passwordField.getPassword()), keyField.getText(), queryField.getText(), out);
				} catch (RemoteException | NoSuchAlgorithmException	| UnsupportedEncodingException | ServiceException e) {
					throw new RuntimeException(e);
				}
			}
		});
		GridBagConstraints gbc_searchButton = new GridBagConstraints();
		gbc_searchButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_searchButton.insets = new Insets(0, 0, 5, 0);
		gbc_searchButton.gridwidth = 2;
		gbc_searchButton.gridx = 0;
		gbc_searchButton.gridy = 5;
		contentPane.add(searchButton, gbc_searchButton);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.gridwidth = 2;
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 0;
		gbc_textArea.gridy = 6;
		contentPane.add(textArea, gbc_textArea);
	}

}
