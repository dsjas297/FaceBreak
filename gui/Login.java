package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.util.regex.*;

public class Login extends JPanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Fields and labels for Username and Password
	private JLabel usernamePrompt = new JLabel("User ID: ");
	JTextField usernameEntry= new JTextField(8);
	private JLabel pwdPrompt = new JLabel("Password: ");
	protected JPasswordField pwdEntry = new JPasswordField(8);
	JButton loginButton = new JButton("Log in");
	JButton signupButton = new JButton("Sign up!");
	int max_len = 20; //max length of username and pwd
	
	//Fields/labels for fname, lname, and family
	private JLabel fnamePrompt = new JLabel("First name: ");
	JTextField fnameEntry= new JTextField(8);
	private JLabel lnamePrompt = new JLabel("Last name: ");
	JTextField lnameEntry= new JTextField(8);
	private JLabel famPrompt = new JLabel("Family: ");
	JTextField famEntry= new JTextField(8);
	
	//WARNINGS
	JLabel loginFailed = new JLabel("Login failed; username or password incorrect");
	JLabel loginsExceeded = new JLabel("Too many incorrect password attempts. Please try again later.");
	JLabel duplicateUser = new JLabel("This user already exists. Please sign up under a different username.");
	JLabel loggedOut = new JLabel("Thank you for using FaceBreak");
	JLabel alphanum = new JLabel("Username and password must consist only of characters a-z, A-Z, 0-9");
	JLabel alphanum2 = new JLabel("Password must contain at least one character from each group a-z, A-Z, 0-9");
	JLabel pwd_lengthreq = new JLabel("Password must be between 6-20 characters in length.");
	JLabel personalFields = new JLabel("First name, last name, and family name required for sign up.");

	public Login(){
		// Create welcome panel
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBackground(Color.white);

		//welcome text
		JLabel facebreak = new JLabel("FaceBreak", JLabel.CENTER);
		Font facebreakFont = facebreak.getFont();
		facebreak.setFont(new Font(facebreakFont.getFontName(), facebreakFont.getStyle(), 36));
		facebreak.setAlignmentX((float) 0.5);
		
		JLabel welcome = new JLabel("Welcome to the Family", JLabel.CENTER);
		Font welcomeFont = welcome.getFont();
		welcome.setFont(new Font(welcomeFont.getFontName(), welcomeFont.getStyle(), 22));
		welcome.setAlignmentX((float) 0.5);
		
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
		usernameEntry.setDocument(new LimitedText(max_len));
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
		pwdEntry.setDocument(new LimitedText(max_len));
		pwdPrompt.setForeground(Color.white);
		pwdPanel.add(pwdPrompt);
		pwdPanel.add(Box.createHorizontalGlue());
		pwdPanel.add(pwdEntry);
		
		loginPanel.add(usernamePanel);
		pwdPanel.add(Box.createVerticalGlue());
		loginPanel.add(pwdPanel);
		
		//fname entry
		JPanel fnamePanel = new JPanel();
		fnamePanel.setAlignmentX((float) 0.5);
		fnamePanel.setMaximumSize(new Dimension(200,30));
		fnamePanel.setLayout(new BoxLayout(fnamePanel, BoxLayout.LINE_AXIS));
		fnamePanel.setBackground(new Color(130, 0, 0));
		fnameEntry.setMaximumSize(new Dimension(150,20));
		fnameEntry.setDocument(new LimitedText(max_len));
		fnamePrompt.setForeground(Color.white);
		fnamePanel.add(fnamePrompt);
		fnamePanel.add(Box.createHorizontalGlue());
		fnamePanel.add(fnameEntry);
		
		loginPanel.add(Box.createRigidArea(new Dimension(0,5)));
		loginPanel.add(fnamePanel);
		//lname entry
		JPanel lnamePanel = new JPanel();
		lnamePanel.setAlignmentX((float) 0.5);
		lnamePanel.setMaximumSize(new Dimension(200,30));
		lnamePanel.setLayout(new BoxLayout(lnamePanel, BoxLayout.LINE_AXIS));
		lnamePanel.setBackground(new Color(130, 0, 0));
		lnameEntry.setMaximumSize(new Dimension(150,20));
		lnameEntry.setDocument(new LimitedText(max_len));
		lnamePrompt.setForeground(Color.white);
		lnamePanel.add(lnamePrompt);
		lnamePanel.add(Box.createHorizontalGlue());
		lnamePanel.add(lnameEntry);
		
		lnamePanel.add(Box.createVerticalGlue());
		loginPanel.add(lnamePanel);
		//family entry
		JPanel famPanel = new JPanel();
		famPanel.setAlignmentX((float) 0.5);
		famPanel.setMaximumSize(new Dimension(200,30));
		famPanel.setLayout(new BoxLayout(famPanel, BoxLayout.LINE_AXIS));
		famPanel.setBackground(new Color(130, 0, 0));
		famEntry.setMaximumSize(new Dimension(150,20));
		famEntry.setDocument(new LimitedText(max_len));
		famPrompt.setForeground(Color.white);
		famPanel.add(famPrompt);
		famPanel.add(Box.createHorizontalGlue());
		famPanel.add(famEntry);
		
		famPanel.add(Box.createVerticalGlue());
		loginPanel.add(famPanel);
		
		//Create and initialize the buttons.
		
		//Lay out the buttons from left to right.
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBackground(new Color(130, 0, 0));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPanel.setMaximumSize(new Dimension(200,100));
		buttonPanel.add(signupButton);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(loginButton);
		
		this.setBorder(BorderFactory.createEmptyBorder(50,150,100,150));
		this.add(facebreak);
		this.add(welcome);
		this.add(loginFailed);
		loginFailed.setAlignmentX((float) 0.5);
		loginFailed.setVisible(false);
		this.add(loginsExceeded);
		loginsExceeded.setAlignmentX((float) 0.5);
		loginsExceeded.setVisible(false);
		this.add(duplicateUser);
		duplicateUser.setAlignmentX((float) 0.5);
		duplicateUser.setVisible(false);
		this.add(alphanum);
		alphanum.setAlignmentX((float) 0.5);
		alphanum.setVisible(false);
		this.add(alphanum2);
		alphanum2.setAlignmentX((float) 0.5);
		alphanum2.setVisible(false);
		this.add(pwd_lengthreq);
		pwd_lengthreq.setAlignmentX((float) 0.5);
		pwd_lengthreq.setVisible(false);
		this.add(personalFields);
		personalFields.setAlignmentX((float) 0.5);
		personalFields.setVisible(false);
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
	
	public void clearPwd(){
		pwdEntry.setText("");
	}
	
	public void strip_whitespace(){
		usernameEntry.setText(usernameEntry.getText().replaceAll("\\s+", ""));
		pwdEntry.setText((new String(pwdEntry.getPassword())).replaceAll("\\s+", ""));
	}
	public boolean is_alphanum(){
		//make sure that username and password consist only of alphanumeric chars.
		boolean user_alnum = Pattern.matches("[a-zA-Z0-9]+", usernameEntry.getText());
		boolean pwd_alnum = Pattern.matches("[a-zA-Z0-9]+", new String(pwdEntry.getPassword()));
		if (!(user_alnum&&pwd_alnum)){
			alphanum.setVisible(true);
		}
		return user_alnum&&pwd_alnum;
	}
	public boolean has_3groups(){
		//make sure that password consist of at least one char from a-z, A-Z, 0-9
		boolean pwd_group1 = Pattern.matches(".*[a-z]+.*", new String(pwdEntry.getPassword()));
		boolean pwd_group2 = Pattern.matches(".*[A-Z]+.*", new String(pwdEntry.getPassword()));
		boolean pwd_group3 = Pattern.matches(".*[0-9]+.*", new String(pwdEntry.getPassword()));
		System.out.print(pwd_group1);
		System.out.print(pwd_group2);
		System.out.print(pwd_group3);
		if (pwd_group1 && pwd_group2 && pwd_group3){
			return true;
		}
		else{
			alphanum2.setVisible(true);
			return false;
		}
	}
	public boolean check_lengthreq(){
		if (pwdEntry.getPassword().length >= 6 && pwdEntry.getPassword().length <= 20){
			return true;
		}
		else{
			pwd_lengthreq.setVisible(true);
			return false;
		}
	}
	//check validity of password
	public boolean is_valid(){
		boolean pwd_is_an = is_alphanum();
		System.out.print(pwd_is_an);
		boolean pwd_has_3groups = has_3groups();
		System.out.print(pwd_has_3groups);
		boolean pwd_correct_length = check_lengthreq();
		System.out.print(pwd_correct_length);
		return pwd_is_an && pwd_has_3groups && pwd_correct_length;
	}
	//clear all displayed warnings
	public void clearWarnings(){
		loginFailed.setVisible(false);
		loginsExceeded.setVisible(false);
		duplicateUser.setVisible(false);
		loggedOut.setVisible(false);
		alphanum.setVisible(false);
		alphanum2.setVisible(false);
		pwd_lengthreq.setVisible(false);
		personalFields.setVisible(false);
	}
	//check if personal fields are filled in
	public boolean personal_filled(){
		//fname
		fnameEntry.setText(fnameEntry.getText().replaceAll("\\s+", ""));
		//lname
		lnameEntry.setText(lnameEntry.getText().replaceAll("\\s+", ""));
		//family
		famEntry.setText(famEntry.getText().replaceAll("\\s+", ""));
		
		if (fnameEntry.getText().equals("")||lnameEntry.getText().equals("")||famEntry.getText().equals("")){
			personalFields.setVisible(true);
			return false;
		}
		else{
			return true;
		}
	}
}
