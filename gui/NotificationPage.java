package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import networking.FBClient;

import common.Error;
import common.Profile;
import common.SerializableAvatar;
import common.Title;

public class NotificationPage extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	FBClient myClient;
	
	/**NotificationPage takes the list of notification messages, and displays one at a time 
	with options for each.
	**/
	public NotificationPage(FBClient myClient, int wall_width, String[][] notifications){
		this.myClient = myClient;
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setMinimumSize(new Dimension(wall_width,300));
		setMaximumSize(new Dimension(wall_width,300));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		setAlignmentX((float) 0.0);
		
		JLabel title = new JLabel("Notifications:");
		title.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		title.setMinimumSize(new Dimension(wall_width,20));
		title.setMaximumSize(new Dimension(wall_width,20));
		add(title);
		add(Box.createRigidArea(new Dimension(0,10)));
		
		//get the list of notification objects
		//each object has type, other user, new title (optional)
		
		//for each one, check the type
		Profile notProfile;
		
		for (int i=0; i<notifications.length; i++){
			System.out.print(i);
			//create box for each notification
			JPanel notif = new JPanel();
			notif.setLayout(new BoxLayout(notif, BoxLayout.PAGE_AXIS));
			notif.setBackground(Color.white);
			notif.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(new Color(130, 0, 0)),
					notif.getBorder()));
			notif.setMinimumSize(new Dimension(wall_width-50, 60));
			notif.setMaximumSize(new Dimension(wall_width-50, 60));
			add(notif);
			add(Box.createRigidArea(new Dimension(0,10)));
			
			//message and option buttons
			JTextArea notif_msg = new JTextArea("");
			JPanel options = new JPanel();
			options.setLayout(new BoxLayout(options, BoxLayout.LINE_AXIS));
			options.setBackground(Color.white);
			
			if (notifications[i][0].equals("title")){
				try {
					notProfile = new Profile(notifications[i][1]);
					myClient.viewProfile(notProfile);
					//set text
					notif_msg = new JTextArea(notProfile.getFname() + " " + notProfile.getLname() + " wishes to change their title from "
							+ notProfile.getTitle() + " to " + notifications[i][2] + ". Approve this request?");
					notif_msg.setEditable(false);
					//notif_msg.setAlignmentX((float) 0.0);
					notif_msg.setColumns(10);
					notif_msg.setLineWrap(true);
					notif_msg.setWrapStyleWord(true);
					//add buttons "Approve/Deny"
					NotifButton approve_b = new NotifButton("Approve", 0, notifications[i][1], notifications[i][2]);
					approve_b.addActionListener(this);
					NotifButton deny_b = new NotifButton("Deny", 0, notifications[i][1]);
					deny_b.addActionListener(this);
					options.add(approve_b);
					options.add(deny_b);
					
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
					NotifButton add_b = new NotifButton("Add Friend", 0, notifications[i][1]);
					add_b.addActionListener(this);
					NotifButton ignore_b = new NotifButton("Ignore", 0, notifications[i][1]);
					ignore_b.addActionListener(this);
					options.add(add_b);
					options.add(ignore_b);
					
					notif.add(notif_msg);
					notif.add(options);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} //end for loop
	} //end method

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() instanceof NotifButton){
			NotifButton nButton = (NotifButton)arg0.getSource();
			//accept a title change
			if (nButton.getText().equals("Approve")){
				try {
					String notifUser = nButton.get_username();
					Profile oldProfile = new Profile(notifUser);
					myClient.viewProfile(oldProfile);
					oldProfile.setTitle(Title.valueOf(nButton.get_newtitle().toUpperCase()));
					//tell client to edit profile
					Error e = myClient.editProfile(oldProfile);
					if (e == Error.SUCCESS)
						System.out.println("Approved title change");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			//add a friend
			else if (nButton.getText().equals("Add Friend")){
				try{
					myClient.addFriend(nButton.get_username());
					System.out.println("Added a friend");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			//deny/ignore a request
			else{
				System.out.println("Ignored/denied a request");
			}
			//remove the notification
			//nButton.get_notif_id();
			
			
			//refresh page
		}	//end notifButton	
	}
		

		 
}