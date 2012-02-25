package facebreak.gui;

import javax.swing.*;

import facebreak.common.FBClientUser;
import facebreak.common.Profile;
import facebreak.common.Title;
import facebreak.networking.Error;
import facebreak.networking.FBClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.UnknownHostException;

public class FBWindow extends JFrame implements ActionListener, MouseListener{
	//this is defined for JFrame's uses
	private static final long serialVersionUID = 1L;
	
	FBClient client;
	FBClientUser myuser;
	Login login = new Login();
	private boolean logged_in = false;
	FBPage fbpage;
	
	int curr_user; //profile you are looking at
	int curr_region=0; //region you are looking at
	
	/**
	 * @param args
	 */
	public FBWindow(){
		//create window
		setTitle("FaceBreak: A Social Network You Can't Refuse");
		//quit java after closing window
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 500); //set size in pixels
		setVisible(true); //show the window
		
		
		setContentPane(login);
		login.loginButton.addActionListener(this);
		login.signupButton.addActionListener(this);
		
		try {
			client = new FBClient();
			System.out.println("Created client socket");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//if login is successful:
//		while (logged_in){
			//user can click on:
				//homepage
				//another user
				//search
				//region
				//make a post
				//edit personal info
				//friend a user
				//trust a user
				//logout
//		}
		//show login again with "thank you" message
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==login.loginButton){
			try {
				client.login(login.usernameEntry.getText(), login.pwdEntry.getText());
				
				Profile myProfile = new Profile("godfather", "Vito", "Corleone");
				myProfile.setFamily("Notorious BJG");
				myProfile.setTitle(Title.BOSS);	
				
				client.editProfile(myProfile);
			
//			try{
//				client = new FBClient();
//				Error login_success = client.login(user);
//				if (login_success==Error.SUCCESS){
//					int userID = user.getId();
//					//transition to user's FBPage with user's ID
//					logged_in = true;
//					login.setVisible(false);
//					setContentPane(new FBPage(userID,0));
//				}
//				else {
//					System.out.println("Login failed");
//					//TODO: change this to a function with 3 strikes/15 minutes policy
//					login.loginFailed.setVisible(true);
//				}
//				
//			}
//			catch (UnknownHostException exp){
//				System.out.println(exp);
//			}
			System.out.println(login.usernameEntry.getText());
			logged_in = true;
			login.setVisible(false);
			curr_user = 0;
			fbpage = new FBPage(client, curr_user,curr_region);
			setContentPane(fbpage);
			fbpage.logout.addMouseListener(this);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else if (e.getSource()==login.signupButton){
			//TODO: signup protocol
		}
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getSource()==fbpage.logout){
			//TODO: client.logout(user)
			
			//logging out!
			logged_in = false;
			getContentPane().remove(fbpage);
			login.setVisible(true);
			
			try {
				client.logout();
				setContentPane(login);
				login.loggedOut.setVisible(true);
				repaint();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//search
		//else if ()
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
