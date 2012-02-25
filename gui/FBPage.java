package facebreak.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

import facebreak.common.Post;
import facebreak.common.Profile;
import facebreak.common.Post.RegionType;
import facebreak.common.Region;
import facebreak.networking.FBClient;
import facebreak.networking.Error;

public class FBPage extends JPanel implements ActionListener, MouseListener{

	//permanent elements
	private JPanel topnav;
	private JLabel logo = new JLabel("FaceBreak");
	public JLabel logout = new JLabel("Log out");
	private JPanel content;
	private JScrollPane prof_scroller;
	private JPanel profile;
	private JScrollPane wall_scroller;
	private JPanel wall;
	private JTextArea comment_box;
	private JButton comment_button;
	
	//IDs
	private int myUserID; //user who is logged in
	private String myUserName;
	private int curr_profile; //user whose profile is being looked at
	private int curr_region; //region of profile being looked at
	
	//sizing
	private int width;
	private int height;
	private int prof_width;
	private int wall_width;
	
	// client
	FBClient myClient;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// create USER page
	public FBPage(FBClient client, int userID, int regionID){
		myClient = client;
		
		myUserID = userID;
		curr_profile = userID;
		curr_region = regionID;
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		//create and add topnav
		width = 900;
		height = 500;
		prof_width = width*250/900;
		wall_width = width*650/900;
		create_topnav();
		this.add(topnav);
		
		//create content
		content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.LINE_AXIS));
		content.setMaximumSize(new Dimension(width, height-50));
		content.setBackground(Color.white);
		
		//add profile scroller to content
		create_profile();
		prof_scroller = new JScrollPane(profile);
		prof_scroller.setMinimumSize(new Dimension(prof_width,height-50));
		prof_scroller.setMaximumSize(new Dimension(prof_width,height-50));
		prof_scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		content.add(prof_scroller);
		
		//add wall scroller to content
		create_wall();
		wall_scroller = new JScrollPane(wall);
		wall_scroller.setMinimumSize(new Dimension(wall_width,height-50));
		wall_scroller.setMaximumSize(new Dimension(wall_width,height-50));
		wall_scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		wall_scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

//		//focus on comment_box
//		comment_box.requestFocus();
//		comment_box.setCaretPosition(0);
		content.add(wall_scroller);
		content.add(Box.createVerticalGlue());
		
		//add content to this.
		this.add(content);
	}
	
	//adds profile picture, information, to user profile.
	public void populate_userprofile() throws ClassNotFoundException{
		Profile myProfile = new Profile("godfather");
		myClient.viewProfile(myProfile);
		
		//get user picture from ID
		String prof_pic;
		if (curr_profile==0){
			prof_pic = "C:/Users/Boiar/workspace/Facebreak/src/gui/mc.jpg";
		}
		else {
			prof_pic = "C:/Users/Boiar/workspace/Facebreak/src/gui/gangsters.jpg";
		}
		//System.out.println(prof_pic);
		JLabel prof_pic_label = new JLabel(new ImageIcon(prof_pic));
		prof_pic_label.setHorizontalAlignment(JLabel.CENTER);
		//TODO: get user info
		JLabel username = new JLabel(myProfile.getFname() + " " + myProfile.getLname());
		username.setAlignmentX((float) 0.0);
		JTextArea user_info = new JTextArea("Title: "
				+ myProfile.getTitle().toString() + "\nFamily: "
				+ myProfile.getFamily());
		user_info.setAlignmentX((float) 0.0);
		user_info.setLineWrap(true);
		user_info.setWrapStyleWord(true);
		
		//add to profile
		profile.add(prof_pic_label);
		profile.add(username);
		profile.add(user_info);
		
		//need a new label for each region
		//TODO: get user's regions
		for (int i=0; i<25; i++){
			Regionlink region = new Regionlink("Region " + i, i, "Username", curr_profile);
			region.addMouseListener(this);
			region.setAlignmentX((float) 0.0);
			profile.add(region);
		}
	}
	//adds posts to user wall.
	public void populate_wall(){
		//TODO: given ownerID, regionID, get all posts for this region, with name, post, timestamp
		//Content wall_posts = new Content();
		//wall_posts.getPost();
		//while there are still posts
		
		Region board = new Region("godfather");
		try {
			myClient.viewBoard(board);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Post[] postArray = board.getPosts();
		
		for(int i = postArray.length - 1; i >= 0; i--) {
			//for each post, get:
			String poster_name = postArray[i].getWriterName();
			int poster_id = 1;
			String message = postArray[i].getText();
			String time = "8:00pm February 18, 2012";
				
			//for each post
			JPanel post = new JPanel();
			post.setLayout(new BoxLayout(post, BoxLayout.PAGE_AXIS));
			post.setBackground(Color.white);
			post.setBorder(BorderFactory.createCompoundBorder(
			                BorderFactory.createLineBorder(new Color(130, 0, 0)),
			                post.getBorder()));			
			//poster name
			Userlink poster = new Userlink(poster_name, poster_id);
			poster.addMouseListener(this);
			poster.setAlignmentX((float) 0.0);
			//post+timestamp
			JTextArea msg = new JTextArea(message + "\n" + time);
			msg.setEditable(false);
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
	}
	
	public void create_topnav(){
		topnav = new JPanel();
		topnav.setLayout(new BoxLayout(topnav, BoxLayout.LINE_AXIS));
		topnav.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		topnav.setMinimumSize(new Dimension(width,50));
		topnav.setPreferredSize(new Dimension(width,50));
		topnav.setMaximumSize(new Dimension(width,50));
		topnav.setBackground(new Color(130, 0, 0));
		
		//add logo
		logo.addMouseListener(this);
		logo.setForeground(Color.white);
		Font logoFont = logo.getFont();
		logo.setFont(new Font(logoFont.getFontName(), logoFont.getStyle(), 36));
		logo.setMinimumSize(new Dimension(prof_width, 50));
		logo.setPreferredSize(new Dimension(prof_width, 50));
		logo.setMaximumSize(new Dimension(prof_width, 50));
		
		//TODO: add search bar
		JLabel searchbar = new JLabel("[searchbar]");
		//add logout
		logout.setForeground(Color.white);

		topnav.add(logo);
		topnav.add(searchbar);
		topnav.add(Box.createHorizontalGlue());
		topnav.add(logout);
	}
	
	public JPanel create_profile(){
		profile = new JPanel();
		profile.setLayout(new BoxLayout(profile, BoxLayout.PAGE_AXIS));
		profile.setMinimumSize(new Dimension(prof_width,height-50));
		profile.setMaximumSize(new Dimension(prof_width,height-50));
		profile.setBackground(Color.blue);
		
		try {
			populate_userprofile();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return profile;
	}
	
	public void create_wall(){
		//create "wall"
		wall = new JPanel();
		wall.setLayout(new BoxLayout(wall, BoxLayout.PAGE_AXIS));
		wall.setBackground(Color.white);
		wall.setMinimumSize(new Dimension(wall_width,height-50));
		//wall.setPreferredSize(new Dimension(650,450));
		wall.setMaximumSize(new Dimension(wall_width,height-50));
		
		//create comment box
		create_commentbox();
		
		//populate posts
		populate_wall();
		
	}
	
	public void create_commentbox(){
		//add containing JPanel
		JPanel comment = new JPanel();
		comment.setLayout(new BoxLayout(comment, BoxLayout.PAGE_AXIS));
		comment.setMinimumSize(new Dimension(wall_width,150));
		comment.setMaximumSize(new Dimension(wall_width,150));
		comment.setBackground(Color.white);
		comment.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		comment.setAlignmentX((float) 0.0);

		//add label: "Leave a comment:"
		JLabel leave_comm = new JLabel("Post to " + curr_region + ":");
		leave_comm.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		
		//add text box
		//TODO: implement character limit
		comment_box = new JTextArea(3, 0);
		comment_box.setEditable(true);
		comment_box.setLineWrap(true);
		comment_box.setWrapStyleWord(true);
		comment_box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(130, 0, 0)),
                comment.getBorder()));

		//add button panel
		JPanel commPanel = new JPanel();
		commPanel.setLayout(new BoxLayout(commPanel, BoxLayout.LINE_AXIS));
		commPanel.setBackground(Color.white);
		commPanel.setMaximumSize(new Dimension(wall_width-20,20));
		//add comment button
		comment_button = new JButton("Post");
		comment_button.addActionListener(this);
		commPanel.add(Box.createHorizontalGlue());
		commPanel.add(comment_button);
		
		wall.add(leave_comm);
		comment.add(comment_box);
		comment.add(commPanel);
		wall.add(comment);
		wall.add(Box.createRigidArea(new Dimension(0,5)));
	}
	
	public void change_profile(int userID){
		curr_profile = userID;
		curr_region = 0;
		create_profile();
		prof_scroller.setViewportView(profile);
		prof_scroller.revalidate();
		change_wall(userID, 0);
	}
	public void change_wall(int userID, int regionID){
		curr_profile = userID;
		curr_region = regionID;
		create_wall();
		wall_scroller.setViewportView(wall);
		wall_scroller.revalidate();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource()==comment_button){
			//if post is not all whitespace
			String comm = comment_box.getText();
			String stripped_comment = comm.replaceAll("\\s+", "");

			if (!stripped_comment.equals("")){

				Post post1 = new Post();
				post1.setRegion(RegionType.PUBLIC);
				post1.setText(comm);
				try {
					Error e = myClient.post(post1);
					if(e == Error.SUCCESS)
						change_wall(curr_profile, curr_region);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getSource()==logo){
			System.out.println(arg0.getSource().getClass().getName());
			//change wall to user's own wall.
			change_profile(myUserID);
			repaint();
		}
		//else if link clicked is of type username:
		else if (arg0.getSource() instanceof Userlink){
			//System.out.println(((Userlink)arg0.getSource()).get_username());
			int new_user = ((Userlink)arg0.getSource()).get_userid();
			change_profile(new_user);
		}
		//else if link clicked is of type region:
		else if (arg0.getSource() instanceof Regionlink){
			//System.out.println(((Regionlink)arg0.getSource()).get_regionname());
			int new_region = ((Regionlink)arg0.getSource()).get_regionid();
			int same_user = ((Regionlink)arg0.getSource()).get_userid();
			change_wall(same_user, new_region);
		}
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
