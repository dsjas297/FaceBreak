package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JPasswordField;

import networking.FBClient;

import common.FBClientUser;
import common.Profile;
import common.Title;

public class FBWindow extends JFrame implements ActionListener, MouseListener {
	// this is defined for JFrame's uses
	private static final long serialVersionUID = 1L;

	FBClient client;
	FBClientUser myuser;
	Login login = new Login();
	//private boolean logged_in = false;
	FBPage fbpage;

	/**
	 * @param args
	 */
	public FBWindow() {
		// create window
		setTitle("FaceBreak: A Social Network You Can't Refuse");
		// quit java after closing window
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 500); // set size in pixels
		setVisible(true); // show the window

		setContentPane(login);
		login.loginButton.addActionListener(this);
		login.signupButton.addActionListener(this);

		try {
			client = new FBClient();
			System.out.println("Created client socket");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		// if login is successful:
		// while (logged_in){
		// user can click on:
		// homepage
		// another user
		// search
		// region
		// make a post
		// edit personal info
		// friend a user
		// trust a user
		// logout
		// }
		// show login again with "thank you" message
	}

	public void login_protocol(){
		try {
			common.Error login_error = client.login(login.usernameEntry.getText(), new String(login.pwdEntry.getPassword()));
			//reset password field
			login.clearPwd();
			
			System.out.println(login.usernameEntry.getText());
			System.out.println(new String(login.pwdEntry.getPassword()));
			//logged_in = true;
			if (login_error == common.Error.SUCCESS){
				login.setVisible(false);
				fbpage = new FBPage(client, login.usernameEntry.getText());
				setContentPane(fbpage);
				fbpage.logout.addMouseListener(this);
			}
			else {
				login.loginFailed.setVisible(true);
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		login.clearWarnings();
		if (e.getSource() == login.loginButton) {
			//make sure both fields are filled in
			login.strip_whitespace();			
			if (login.is_valid()){
				//client.setCurrentUser(login.usernameEntry.getText(),
				//		new String(login.pwdEntry.getPassword()));
				login_protocol();	
			}
		} else if (e.getSource() == login.signupButton) {
			//make sure both fields are filled in
			login.strip_whitespace();
			if (login.is_valid()){
					//!login.usernameEntry.getText().equals("")&& !(new String(login.pwdEntry.getPassword())).equals("")&&login.is_alphanum()){
				try {
					common.Error signup_error = client.createUser(login.usernameEntry.getText(),
							new String(login.pwdEntry.getPassword()));
					//client.getSocket().close();
					if (signup_error==common.Error.DUPLICATE_USER){
						//display an error
						login.duplicateUser.setVisible(true);
					}
					//else{
						//auto-login
						//login_protocol();		
					//}
						
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getSource() == fbpage.logout) {
			// logging out!
			//logged_in = false;
			getContentPane().remove(fbpage);
			login.setVisible(true);

			try {
				client.logout();
				setContentPane(login);
				login.loggedOut.setVisible(true);
				repaint();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		// search
		// else if ()
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		//
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		//
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		//
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		//
	}

}
