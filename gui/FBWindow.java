package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.UnknownHostException;

import javax.swing.JFrame;

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

	int curr_user; // profile you are looking at
	int curr_region = 0; // region you are looking at

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
			client.login(login.usernameEntry.getText(),
					login.pwdEntry.getText());
			

			Profile myProfile = new Profile("godfather", "Vito", "Corleone");
			myProfile.setFamily("Notorious BJG");
			myProfile.setTitle(Title.BOSS);
			client.editProfile(myProfile);

			System.out.println(login.usernameEntry.getText());
			//logged_in = true;
			login.setVisible(false);
			curr_user = 0;
			fbpage = new FBPage(client, curr_user, login.usernameEntry.getText(), curr_region);
			setContentPane(fbpage);
			fbpage.logout.addMouseListener(this);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == login.loginButton) {
			login_protocol();
		} else if (e.getSource() == login.signupButton) {
			try {
				client.createUser(login.usernameEntry.getText(),
						login.pwdEntry.getText());
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
			//auto-login
			login_protocol();
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
