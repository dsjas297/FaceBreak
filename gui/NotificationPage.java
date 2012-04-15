package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import networking.FBClient;

import common.Profile;

public class NotificationPage extends JPanel{
	private static final long serialVersionUID = 1L;
	
	/**NotificationPage takes the list of notification messages, and displays one at a time 
	with options for each.
	**/
	public NotificationPage(FBClient myClient, int wall_width, String[][] notifications){
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setMinimumSize(new Dimension(wall_width,300));
		setMaximumSize(new Dimension(wall_width,300));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		setAlignmentX((float) 0.0);
		
		JLabel title = new JLabel("Notifications:");
		this.add(title);
		
		//get the list of notification objects
		//each object has type, other user, new title (optional)
		
		//for each one, check the type
		Profile notProfile;
		for (int i=0; i<notifications.length; i++){
			System.out.print(i);
			JPanel notif = new JPanel();
			notif.setLayout(new BoxLayout(notif, BoxLayout.PAGE_AXIS));
			notif.setBackground(Color.white);
			notif.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(new Color(130, 0, 0)),
					notif.getBorder()));
			JTextArea notif_msg = new JTextArea();
			JPanel options = new JPanel();
			options.setLayout(new BoxLayout(options, BoxLayout.LINE_AXIS));
			options.setBackground(Color.white);
			
			if (notifications[i][0].equals("title")){
				try {
					notProfile = new Profile(notifications[i][1]);
					myClient.viewProfile(notProfile);
					//set text
					notif_msg = new JTextArea(notProfile.getFname() + " " + notProfile.getLname() + " wishes to change their title from "
							+ notProfile.getTitle() + " to " + notifications[i][2]);
					notif_msg.setEditable(false);
					notif_msg.setAlignmentX((float) 0.0);
					notif_msg.setColumns(10);
					notif_msg.setLineWrap(true);
					notif_msg.setWrapStyleWord(true);
					//add buttons "Accept/Deny"
					options.add(new JButton("Accept"));
					options.add(new JButton("Deny"));
					
					notif.add(notif_msg);
					notif.add(options);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
			}
			else if (notifications[i][0].equals("friend")){
				try {
					notProfile = new Profile(notifications[i][1]);
					myClient.viewProfile(notProfile);
					//set text
					notif_msg = new JTextArea(notProfile.getFname() + " " + notProfile.getLname()
							+ " (" + notProfile.getFamily() + " " + notProfile.getTitle()
							+ ") has added you as a friend. Would you like to add them back?");
					notif_msg.setEditable(false);
					notif_msg.setColumns(10);
					notif_msg.setLineWrap(true);
					notif_msg.setWrapStyleWord(true);
					//add buttons "Add Friend/Ignore"
					options.add(new JButton("Add Friend"));
					options.add(new JButton("Ignore"));
					
					notif.add(notif_msg);
					notif.add(options);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			//add to notif
			
			this.add(notif);
		}
		
	}
		

		 
}