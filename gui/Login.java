package facebreak.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Login extends JPanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Fields and labels for UserId and Password
	private JLabel usernamePrompt = new JLabel("User ID: ");
	JTextField usernameEntry= new JTextField(8);
	private JLabel pwdPrompt = new JLabel("Password: ");
	protected JTextField pwdEntry= new JTextField(8);
	JButton loginButton = new JButton("Log in");
	JButton signupButton = new JButton("Sign up!");
	JLabel loginFailed = new JLabel("Login failed");
	JLabel loggedOut = new JLabel("Thank you for using FaceBreak");

	public Login(){
		// Create welcome panel
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		//this.setBackground(Color.white);
	//	this.setOpaque(false);
		//this.setBackground(new Color(0,0,0, 64) );
		//this = new ImagePanel("gangsters.jpg");
		//ImagePanel bgPanel = new ImagePanel("gangsters.jpg");

		//welcome text
		JLabel facebreak = new JLabel("FaceBreak", JLabel.CENTER);
		Font facebreakFont = facebreak.getFont();
		facebreak.setFont(new Font(facebreakFont.getFontName(), facebreakFont.getStyle(), 36));
		facebreak.setAlignmentX((float) 0.5);
		
		JLabel welcome = new JLabel("Welcome to the Family", JLabel.CENTER);
		Font welcomeFont = welcome.getFont();
		welcome.setFont(new Font(welcomeFont.getFontName(), welcomeFont.getStyle(), 22));
		welcome.setAlignmentX((float) 0.5);
		//welcome.setSize(new Dimension(200,100));
		
		//login panel
		JPanel loginPanel = new JPanel();
		loginPanel.setBackground(new Color(130, 0, 0));
		loginPanel.setAlignmentX((float) 0.5);
		//loginPanel.setSize(300,100);
		loginPanel.setMaximumSize(new Dimension(200,100));
		loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.PAGE_AXIS));
		loginPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
	
		//username panel
		JPanel usernamePanel = new JPanel();
		usernamePanel.setAlignmentX((float) 0.5);
		usernamePanel.setMaximumSize(new Dimension(200,30));
		usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.LINE_AXIS));
		usernamePanel.setBackground(new Color(130, 0, 0));
		usernameEntry.setMaximumSize(new Dimension(150,20));
		usernameEntry.requestFocus();
		usernameEntry.setDocument(new LimitedText(10));
		usernamePrompt.setForeground(Color.white);
		usernamePanel.add(usernamePrompt);
		usernamePanel.add(Box.createHorizontalGlue());
		usernamePanel.add(usernameEntry);
		
		//pwd panel
		JPanel pwdPanel = new JPanel();
		pwdPanel.setAlignmentX((float) 0.5);
		pwdPanel.setMaximumSize(new Dimension(200,30));
		pwdPanel.setLayout(new BoxLayout(pwdPanel, BoxLayout.LINE_AXIS));
		pwdPanel.setBackground(new Color(130, 0, 0));
		pwdEntry.setMaximumSize(new Dimension(150,20));
		pwdEntry.setDocument(new LimitedText(10));
		pwdPrompt.setForeground(Color.white);
		pwdPanel.add(pwdPrompt);
		pwdPanel.add(Box.createHorizontalGlue());
		pwdPanel.add(pwdEntry);
		
		loginPanel.add(usernamePanel);
		pwdPanel.add(Box.createVerticalGlue());
		loginPanel.add(pwdPanel);
		
		
		//Create and initialize the buttons.
        //loginButton.addActionListener(this);
		
		//Lay out the buttons from left to right.
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBackground(new Color(130, 0, 0));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPanel.setMaximumSize(new Dimension(200,100));
		buttonPanel.add(signupButton);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(loginButton);
		
		//this.add(Box.createHorizontalGlue());
		//this.add(bgPanel);
		this.setBorder(BorderFactory.createEmptyBorder(50,150,100,150));
		this.add(facebreak);
		this.add(welcome);
		this.add(loginFailed);
		loginFailed.setAlignmentX((float) 0.5);
		loginFailed.setVisible(false);
		this.add(loggedOut);
		loggedOut.setAlignmentX((float) 0.5);
		loggedOut.setVisible(false);
		this.add(Box.createVerticalGlue());
		this.add(loginPanel);
		this.add(buttonPanel);
		this.add(Box.createVerticalGlue());
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//blank		
	}
	
	public void paintComponent(Graphics g) {
	super.paintComponent(g);
	g.drawImage(new ImageIcon("gangsters.jpg").getImage(), 0, 0, this);
	}
			
}
