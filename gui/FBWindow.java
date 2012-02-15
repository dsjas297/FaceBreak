package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FBWindow extends JFrame implements ActionListener{
	//this is defined for JFrame's uses
	private static final long serialVersionUID = 1L;
	
	Login login = new Login();
	private boolean logged_in = false;
	//FBPage fbpage = new FBPage(0);

	
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
		
		//if login is successful:
		while (logged_in){
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
		}
		//show login again with "thank you" message
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		//TODO: throw this into Login class
		if (e.getSource()==login.loginButton){
			System.out.println("Username: " + login.useridEntry.getText());
			//check that username is correct
			if (login.useridEntry.getText().equals("Boiar")){
			//transition to user's FBPage with user's ID
				logged_in = true;
				login.setVisible(false);
				setContentPane(new FBPage(0));
			}
			else {
				//if not print "Login failed"
				System.out.println("Login failed");
				//TODO: change this to a function;
				login.loginFailed.setVisible(true);
				//TODO: implement 3 strikes/15 minutes policy
			}
		}
	}

	
}
