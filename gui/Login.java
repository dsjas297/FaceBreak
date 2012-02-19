package gui;

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
	private JLabel useridPrompt = new JLabel("User ID: ");
	JTextField useridEntry= new JTextField(8);
	private JLabel pwdPrompt = new JLabel("Password: ");
	protected JTextField pwdEntry= new JTextField(8);
	JButton loginButton = new JButton("Log in");
	JLabel loginFailed = new JLabel("Login failed");

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
		loginPanel.setBackground(Color.cyan);
		loginPanel.setAlignmentX((float) 0.5);
		//loginPanel.setSize(300,100);
		loginPanel.setMaximumSize(new Dimension(200,100));
		loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.PAGE_AXIS));
		loginPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
	
		//userid panel
		JPanel useridPanel = new JPanel();
		useridPanel.setAlignmentX((float) 0.5);
		useridPanel.setMaximumSize(new Dimension(200,30));
		useridPanel.setLayout(new BoxLayout(useridPanel, BoxLayout.LINE_AXIS));
		useridEntry.setMaximumSize(new Dimension(150,20));
		useridEntry.requestFocus();
		useridEntry.setDocument(new LimitedText(10));
		useridPanel.add(useridPrompt);
		useridPanel.add(Box.createHorizontalGlue());
		useridPanel.add(useridEntry);
		
		//pwd panel
		JPanel pwdPanel = new JPanel();
		pwdPanel.setAlignmentX((float) 0.5);
		pwdPanel.setMaximumSize(new Dimension(200,30));
		pwdPanel.setLayout(new BoxLayout(pwdPanel, BoxLayout.LINE_AXIS));
		pwdEntry.setMaximumSize(new Dimension(150,20));
		pwdEntry.setDocument(new LimitedText(10));
		pwdPanel.add(pwdPrompt);
		pwdPanel.add(Box.createHorizontalGlue());
		pwdPanel.add(pwdEntry);
		
		loginPanel.add(useridPanel);
		pwdPanel.add(Box.createVerticalGlue());
		loginPanel.add(pwdPanel);
		
		
		//Create and initialize the buttons.
        //loginButton.addActionListener(this);
		
		//Lay out the buttons from left to right.
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBackground(Color.cyan);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPanel.setMaximumSize(new Dimension(200,100));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(loginButton);
		
		//this.add(Box.createHorizontalGlue());
		//this.add(bgPanel);
		this.setBorder(BorderFactory.createEmptyBorder(50,150,100,150));
		this.add(facebreak);
		this.add(welcome);
		this.add(loginFailed);
		loginFailed.setVisible(false);
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
