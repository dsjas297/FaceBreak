package gui;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.*;

public class FBPage extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// create USER page
	public FBPage(int userID, int regionID){
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		//create topnav
		JPanel topnav = new JPanel();
		topnav.setMinimumSize(new Dimension(900,50));
		topnav.setPreferredSize(new Dimension(900,50));
		topnav.setMaximumSize(new Dimension(900,50));
		topnav.setBackground(new Color(130, 0, 0));
//		content.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(Color.red),
//                content.getBorder()));
		
		//create content
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.LINE_AXIS));
		content.setMaximumSize(new Dimension(900,450));
		content.setBackground(Color.white);
//		content.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(Color.red),
//                content.getBorder()));
		
		//create profile content
		JPanel profile = new JPanel();
		profile.setLayout(new BoxLayout(profile, BoxLayout.PAGE_AXIS));
		profile.setMinimumSize(new Dimension(250,450));
		profile.setMaximumSize(new Dimension(250,450));
		profile.setBackground(Color.blue);
		
		profile = populate_userprofile(userID, profile);
		JScrollPane prof_scroller = new JScrollPane(profile);
		prof_scroller.setMinimumSize(new Dimension(250,450));
		prof_scroller.setMaximumSize(new Dimension(250,450));
		prof_scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//profile.setBorder(BorderFactory.createCompoundBorder(
        //        BorderFactory.createLineBorder(Color.red),
        //        profile.getBorder()));
		
		//create "wall"
		JPanel wall = new JPanel();
		wall.setLayout(new BoxLayout(wall, BoxLayout.PAGE_AXIS));
		wall.setBackground(Color.white);
		wall.setMinimumSize(new Dimension(650,450));
		//wall.setPreferredSize(new Dimension(650,450));
		wall.setMaximumSize(new Dimension(650,450));
		populate_wall(userID, regionID, wall);
			
		JScrollPane wall_scroller = new JScrollPane(wall);
		wall_scroller.setMinimumSize(new Dimension(650,450));
		//wall_scroller.setPreferredSize(new Dimension(650,450));
		wall_scroller.setMaximumSize(new Dimension(650,450));
		wall_scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		wall_scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		//add profile and wall to content.
		content.add(prof_scroller);
		content.add(wall_scroller);
		
		//add topnav and content to this.
		this.add(topnav);
		this.add(content);
	}
	
	//adds profile picture, information, to user profile.
	public JPanel populate_userprofile(int userID, JPanel profile){
		//get user picture from ID
		String prof_pic = "C:/users/Boiar/workspace/Facebreak/src/gui/mc.jpg";
		JLabel prof_pic_label = new JLabel(new ImageIcon(prof_pic));
		prof_pic_label.setHorizontalAlignment(JLabel.CENTER);
		//get user info
		JLabel username = new JLabel("Username");
		username.setAlignmentX((float) 0.0);
		JTextArea user_info = new JTextArea("Title: \nFamily: \n");
		user_info.setAlignmentX((float) 0.0);
		user_info.setLineWrap(true);
		user_info.setWrapStyleWord(true);
		
		//add to profile
		profile.add(prof_pic_label);
		profile.add(username);
		profile.add(user_info);
		
		//need a new label for each region
		for (int i=0; i<25; i++){
			JLabel region = new JLabel("Region " + i);
			region.setAlignmentX((float) 0.0);
			profile.add(region);
		}
		
		return profile;
	}
	//adds posts to user wall.
	public JPanel populate_wall(int userID, int regionID, JPanel wall){
		//TODO: get the file for this user's region
		//TODO: get 10 serialized posts
		
		//get first 10 posts by time
		for (int i=0; i<10; i++){
			//for each post, get:
			String poster_name = "Username";
			String message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut volutpat lacus ac justo fermentum rutrum. Nullam porttitor scelerisque ipsum ac feugiat.";
			String time = "8:00pm February 18, 2012";
				
			//for each post
			JPanel post = new JPanel();
			post.setLayout(new BoxLayout(post, BoxLayout.PAGE_AXIS));
//			post.setMinimumSize(new Dimension(500,200));
//			post.setPreferredSize(new Dimension(500,200));
//			post.setMaximumSize(new Dimension(500,200));
			post.setBackground(Color.white);
			post.setBorder(BorderFactory.createCompoundBorder(
			                BorderFactory.createLineBorder(new Color(130, 0, 0)),
			                post.getBorder()));
			
			//
			
			JLabel poster = new JLabel(poster_name);
			poster.setAlignmentX((float) 0.0);
			JTextArea msg = new JTextArea(message + "\n" + time);
			msg.setAlignmentX((float) 0.0);
			msg.setColumns(10);
			msg.setLineWrap(true);
			msg.setWrapStyleWord(true);
			
			//post content
			//JPanel post_content = view_post(userID, regionID);
			post.add(poster);
			post.add(msg);
			
			wall.add(post);
			wall.add(Box.createRigidArea(new Dimension(0,5)));
		}
		return wall;
	}
	
	//view post information
	public JPanel view_post(int ownerID, int regionID){
		//get 
		//, int posterID, String msg, String timestamp
		
		
//		JLabel poster = new JLabel("Username");//TODO: get username from posterID
//		JLabel message = new JLabel(msg);
//		JLabel time = new JLabel(timestamp);
		JPanel post_content = new JPanel();
//		post_content.add(poster);
//		post_content.add(message);
//		post_content.add(time);
		return post_content;
	}

}
