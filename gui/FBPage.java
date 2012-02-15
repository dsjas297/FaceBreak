package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class FBPage extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// create USER page
	public FBPage(int userID){
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
		JScrollPane prof_scroller = new JScrollPane(profile);
		prof_scroller.setMinimumSize(new Dimension(250,450));
		prof_scroller.setMaximumSize(new Dimension(250,450));
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
		populate_userwall(userID, wall);
			
		JScrollPane wall_scroller = new JScrollPane(wall);
		wall_scroller.setMinimumSize(new Dimension(650,450));
		//wall_scroller.setPreferredSize(new Dimension(650,450));
		wall_scroller.setMaximumSize(new Dimension(650,450));
		wall_scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		//add profile and wall to content.
		content.add(prof_scroller);
		content.add(wall_scroller);
		
		//add topnav and content to this.
		this.add(topnav);
		this.add(content);
	}
	// create REGION page
	public FBPage(int userID, int regionID){
		
		
	}
	
	//adds profile picture, information, to user profile.
	public JPanel populate_userprofile(int userID, JPanel profile){
		//get user picture
		//get user info
		
		//get list of user regions
		return profile;
	}
	//adds posts to user wall.
	public JPanel populate_userwall(int userID, JPanel wall){
		//get first 10 posts by time
		for (int i=0; i<10; i++){
			//for each post
			JPanel post = new JPanel();
			post.setMinimumSize(new Dimension(500,200));
			post.setPreferredSize(new Dimension(500,200));
			post.setMaximumSize(new Dimension(500,200));
			post.setBackground(Color.white);
			post.setBorder(BorderFactory.createCompoundBorder(
			                BorderFactory.createLineBorder(new Color(130, 0, 0)),
			                post.getBorder()));
			
			//post content
			JLabel post_content = new JLabel("POST CONTENT HERE");
			post.add(post_content);
			
			wall.add(post);
			wall.add(Box.createRigidArea(new Dimension(0,5)));
		}
		return wall;
	}
	//adds profile picture, information, to region profile.
	public JPanel populate_regprofile(int userID, int regionID, JPanel profile){
		//get user picture
		//get region info
		
		//get list of allowed users
		return profile;
	}
	//adds posts to region wall.
	public JPanel populate_regwall(int userID, int regionID, JPanel wall){
		//get first 10 posts by time
		for (int i=0; i<10; i++){
			//for each post
			JPanel post = new JPanel();
			post.setMinimumSize(new Dimension(500,200));
			post.setPreferredSize(new Dimension(500,200));
			post.setMaximumSize(new Dimension(500,200));
			post.setBackground(Color.white);
			post.setBorder(BorderFactory.createCompoundBorder(
			                BorderFactory.createLineBorder(new Color(130, 0, 0)),
			                post.getBorder()));
			
			//post content
			JLabel post_content = new JLabel("POST CONTENT HERE");
			post.add(post_content);
			
			wall.add(post);
			wall.add(Box.createRigidArea(new Dimension(0,5)));
		}
		return wall;
	}
}
