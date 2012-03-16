package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import networking.FBClient;

import common.Profile;

public class FriendsPage extends JPanel{
	private static final long serialVersionUID = 1L;
	
	/**FriendsPage takes a list of usernames and calls client to get profile information 
	for each friend, which is displayed on page.
	Displays a message if the friends list is empty.
	**/
	public FriendsPage(MouseListener parent, FBClient myClient, int wall_width, String[] friendsList){
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setMinimumSize(new Dimension(wall_width,300));
		setMaximumSize(new Dimension(wall_width,300));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		setAlignmentX((float) 0.0);
		
		JLabel fList = new JLabel("Friends:");
		this.add(fList);
		this.add(Box.createRigidArea(new Dimension(0, 10)));
		if (friendsList == null || friendsList.length == 0){
			JLabel noFriends = new JLabel("Sorry, you have no friends!");
			this.add(noFriends);
		}
		else {
			//for each friend
			for (int i=0; i<friendsList.length; i++){
				try {
					//get profile
					Profile myProfile = new Profile(friendsList[i]);
					myClient.viewProfile(myProfile);
					
					//put information into a box
					Userlink name = new Userlink(myProfile.getFname() + " " + myProfile.getLname(), friendsList[i]);
					name.setForeground(new Color(130,0,0));
					name.addMouseListener(parent);
					JLabel fam = new JLabel(myProfile.getTitle() + " of " + myProfile.getFamily());
					
					//add box to FriendsPage
					this.add(name);
					this.add(fam);
					this.add(Box.createRigidArea(new Dimension(0,10)));
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
