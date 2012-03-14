package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import networking.FBClient;

import common.Board;
import common.Error;
import common.Post;
import common.SerializableAvatar;
import common.Title;
import common.Post.RegionType;
import common.Profile;
import common.Region;

public class FBPage extends JPanel implements ActionListener, MouseListener {

	// permanent elements
	//CLIENT
	FBClient myClient;
	//IDs
	private int myUserID; // user who is logged in
	private String myUserName;
	private int curr_profile; // user whose profile is being looked at
	private String curr_username; // user whose profile is being looked at
	private int curr_region; // region of profile being looked at
	//SIZING
	private int width;
	private int height;
	private int prof_width;
	private int wall_width;
	//TOPNAV ELEMENTS
	private JPanel topnav;
	private JLabel logo = new JLabel("FaceBreak");
	private JTextField search_box;
	private JButton search_button;
	private JLabel edit_button = new JLabel("Edit | ");
	public JLabel logout = new JLabel("Log out");
	//PROFILE ELEMENTS
	private JPanel content;
	private JScrollPane prof_scroller;
	private JPanel profile;
	private JButton add_friend = new JButton("Add friend");
	private JButton rem_friend = new JButton("Remove friend");
	private JButton add_trust = new JButton("Trust");
	private JButton rem_trust  = new JButton("Don't trust");
	private JButton add_covert = new JButton("Add covert board");
	//WALL and COMMENT ELEMENTS
	private JScrollPane wall_scroller;
	private JPanel wall;
	private JTextArea comment_box;
	private JButton comment_button;
	//EDITOR ELEMENTS
	private JPanel edit;
	private JButton save_edit = new JButton("Save profile");


	/**
*
*/
	private static final long serialVersionUID = 1L;

	// create USER page
	public FBPage(FBClient client, String curr_username) {
		myClient = client;

		//myUserID = userID;
		myUserName = curr_username;
		this.curr_username = curr_username;
		//curr_profile = userID;
		curr_region = 0;
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		// create and add topnav
		width = 900;
		height = 500;
		prof_width = width * 250 / 900;
		wall_width = width * 650 / 900;
		create_topnav();
		this.add(topnav);

		// create content
		content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.LINE_AXIS));
		content.setMaximumSize(new Dimension(width, height - 50));
		content.setBackground(Color.white);

		// add profile scroller to content
		create_profile();
		prof_scroller = new JScrollPane(profile);
		prof_scroller.setMinimumSize(new Dimension(prof_width, height - 50));
		prof_scroller.setMaximumSize(new Dimension(prof_width, height - 50));
		prof_scroller
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		content.add(prof_scroller);

		// add wall scroller to content
		create_wall();
		wall_scroller = new JScrollPane(wall);
		wall_scroller.setMinimumSize(new Dimension(wall_width, height - 50));
		wall_scroller.setMaximumSize(new Dimension(wall_width, height - 50));
		wall_scroller
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		wall_scroller
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// //focus on comment_box
		// comment_box.requestFocus();
		// comment_box.setCaretPosition(0);
		content.add(wall_scroller);
		content.add(Box.createVerticalGlue());

		// add content to this.
		this.add(content);
	}

	// adds profile picture, information, to user profile.
	public void populate_userprofile() throws ClassNotFoundException {
		Profile myProfile = new Profile(curr_username);
		myClient.viewProfile(myProfile);

		// get user picture from ID
		if (myProfile.getAvatar()!=null){
			BufferedImage prof_pic = SerializableAvatar.serialAvatarToBufImage(myProfile.getAvatar());
			
			// System.out.println(prof_pic);
			JLabel prof_pic_label = new JLabel(new ImageIcon(prof_pic));
			prof_pic_label.setHorizontalAlignment(JLabel.CENTER);
			profile.add(prof_pic_label);
		}
		//get user info
		JLabel username = new JLabel(myProfile.getFname() + " "
				+ myProfile.getLname());
		username.setAlignmentX((float) 0.0);
		username.setPreferredSize(new Dimension(prof_width, 30));
		JTextArea user_info = new JTextArea("Title: "
				+ myProfile.getTitle().toString() + "\nFamily: "
				+ myProfile.getFamily(), 2, 10);
		user_info.setAlignmentX((float) 0.0);
		user_info.setEditable(false);
		user_info.setLineWrap(true);
		user_info.setWrapStyleWord(true);
		user_info.setPreferredSize(new Dimension(prof_width,50));
		user_info.setMinimumSize(new Dimension(prof_width,50));
		user_info.setMaximumSize(new Dimension(prof_width,50));

		// add to profile
		profile.add(username);
		profile.add(user_info);

		//add friend
		add_friend.addActionListener(this);
		profile.add(add_friend);
		add_friend.setVisible(false);
		//remove friend
		rem_friend.addActionListener(this);
		profile.add(rem_friend);
		rem_friend.setVisible(false);
		//don't trust
		rem_trust.addActionListener(this);
		profile.add(rem_trust);
		rem_trust.setVisible(false);
		//trust
		add_trust.addActionListener(this);
		profile.add(add_trust);
		add_trust.setVisible(false);
		
		if (curr_username != myUserName){
			//TODO: if curr_profile is not friends with myUser {
			add_friend.setVisible(true);
			//			}
			//else{ 
			// if curr_profile is friends with myUser
//				rem_friend.setVisible(true);
			//TODO: check trustworthiness
//			}	
		}
		
		// get Board for user, get list of regions
		Board board = new Board(curr_profile);
		myClient.viewBoard(board);
		Integer[] regionList = board.getRegions();
		int numRegions = regionList.length;
		for (int i = 0; i < numRegions; i++) {
			Regionlink region;
			if (i==0){
				region = new Regionlink("Public", regionList[i], curr_username, curr_profile);
			}
			else if (i==1){
				region = new Regionlink("Private", regionList[i], curr_username, curr_profile);
			}
			else {
				region = new Regionlink("Covert " + (regionList[i]-2), regionList[i], curr_username, curr_profile);
			}
			region.addMouseListener(this);
			region.setAlignmentX((float) 0.0);
			region.setForeground(new Color(130, 0, 0));
			profile.add(region);
		}
		//get the max number of regions, based on title
		int maxRegions = 2;
		switch(myProfile.getTitle()){
		case BOSS: maxRegions = 27; break;
		case CAPO: maxRegions = 12; break;
		case SOLDIER: maxRegions = 7; break;
		default: maxRegions = 2; break;
		}
		//add Covert button
		if (numRegions < maxRegions){
			add_covert.addActionListener(this);
			profile.add(add_covert);
		}
		//profile.add(Box.createVerticalGlue());
		profile.add(Box.createRigidArea(new Dimension(0,30)));
	}

	// adds posts to user wall.
	public void populate_wall() {
		Region region = new Region(curr_username, curr_region);
		try {
			myClient.viewRegion(region);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Post[] postArray = region.getPosts();
		if (postArray != null){
			for (int i = postArray.length - 1; i >= 0; i--) {
				// for each post, get:
				String poster_name = postArray[i].getWriterName();
				int poster_id = 1;
				String message = postArray[i].getText();
				String time = postArray[i].getDate();
	
				// for each post
				JPanel post = new JPanel();
				post.setLayout(new BoxLayout(post, BoxLayout.PAGE_AXIS));
				post.setBackground(Color.white);
				post.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(new Color(130, 0, 0)),
						post.getBorder()));
				// poster name
				Profile posterProf = new Profile(poster_name);
				//get user's name
				try {
					myClient.viewProfile(posterProf);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
				
				Userlink poster = new Userlink(posterProf.getFname() + " " + posterProf.getLname(), poster_name, poster_id);
				poster.addMouseListener(this);
				poster.setAlignmentX((float) 0.0);
				// post+timestamp
				JTextArea msg = new JTextArea(message + "\n" + time);
				msg.setEditable(false);
				msg.setAlignmentX((float) 0.0);
				msg.setColumns(10);
				msg.setLineWrap(true);
				msg.setWrapStyleWord(true);
	
				// post content
				// JPanel post_content = view_post(userID, regionID);
				post.add(poster);
				post.add(msg);
	
				wall.add(post);
				wall.add(Box.createRigidArea(new Dimension(0, 5)));
			}
		}
	}

	public void create_topnav() {
		topnav = new JPanel();
		topnav.setLayout(new BoxLayout(topnav, BoxLayout.LINE_AXIS));
		topnav.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		topnav.setMinimumSize(new Dimension(width, 50));
		topnav.setPreferredSize(new Dimension(width, 50));
		topnav.setMaximumSize(new Dimension(width, 50));
		topnav.setBackground(new Color(130, 0, 0));

		// add logo
		logo.addMouseListener(this);
		logo.setForeground(Color.white);
		Font logoFont = logo.getFont();
		logo.setFont(new Font(logoFont.getFontName(), logoFont.getStyle(), 36));
		logo.setMinimumSize(new Dimension(prof_width, 50));
		logo.setPreferredSize(new Dimension(prof_width, 50));
		logo.setMaximumSize(new Dimension(prof_width, 50));

		// add search bar
		search_box = new JTextField(20);
		search_box.setMinimumSize(new Dimension(200, 40));
		search_box.setPreferredSize(new Dimension(200, 40));
		search_box.setMaximumSize(new Dimension(200, 40));
		search_button = new JButton("Search");
		search_button.setMinimumSize(new Dimension(75, 40));
		search_button.setPreferredSize(new Dimension(75, 40));
		search_button.setMaximumSize(new Dimension(75, 40));
		search_button.addActionListener(this);
		topnav.add(search_box);
		topnav.add(search_button);
		
		// add edit button
		edit_button.setForeground(Color.white);
		edit_button.addMouseListener(this);
		// add logout
		logout.setForeground(Color.white);

		topnav.add(logo);
		topnav.add(search_box);
		topnav.add(search_button);
		topnav.add(Box.createHorizontalGlue());
		topnav.add(edit_button);
		topnav.add(logout);
	}

	public JPanel create_profile() {
		profile = new JPanel();
		profile.setLayout(new BoxLayout(profile, BoxLayout.PAGE_AXIS));
		profile.setMinimumSize(new Dimension(prof_width, height - 50));
		profile.setMaximumSize(new Dimension(prof_width, height - 50));
		profile.setBackground(Color.WHITE);

		try {
			populate_userprofile();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return profile;
	}

	public void create_wall() {
		// create "wall"
		wall = new JPanel();
		wall.setLayout(new BoxLayout(wall, BoxLayout.PAGE_AXIS));
		wall.setBackground(Color.white);
		wall.setMinimumSize(new Dimension(wall_width, height - 50));
		// wall.setPreferredSize(new Dimension(650,450));
		wall.setMaximumSize(new Dimension(wall_width, height - 50));

		// create comment box
		create_commentbox();

		// populate posts
		populate_wall();

	}

	public void create_commentbox() {
		// add containing JPanel
		JPanel comment = new JPanel();
		comment.setLayout(new BoxLayout(comment, BoxLayout.PAGE_AXIS));
		comment.setMinimumSize(new Dimension(wall_width, 100));
		comment.setMaximumSize(new Dimension(wall_width, 100));
		comment.setBackground(Color.white);
		comment.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		comment.setAlignmentX((float) 0.0);

		// add label: "Leave a comment:"
		String reg_name;
		switch(curr_region){
		case 0:
			reg_name = "Public Region";
			break;
		case 1:
			reg_name = "Private Region";
			break;
		default:
			reg_name = "Covert Region " + ((Integer)curr_region-2);
			break;
		}
		
		JLabel leave_comm = new JLabel("Post to " + reg_name + ":");
		leave_comm.setAlignmentX(JLabel.LEFT_ALIGNMENT);

		// add text box
		comment_box = new JTextArea(2, 0);
		comment_box.requestFocus();
		comment_box.setDocument(new LimitedText(140));
		comment_box.setEditable(true);
		comment_box.setLineWrap(true);
		comment_box.setWrapStyleWord(true);
		comment_box.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(130, 0, 0)),
				comment.getBorder()));

		// add button panel
		JPanel commPanel = new JPanel();
		commPanel.setLayout(new BoxLayout(commPanel, BoxLayout.LINE_AXIS));
		commPanel.setBackground(Color.white);
		commPanel.setMaximumSize(new Dimension(wall_width - 20, 20));
		// add comment button
		comment_button = new JButton("Post");
		comment_button.addActionListener(this);
		commPanel.add(Box.createHorizontalGlue());
		commPanel.add(comment_button);

		wall.add(leave_comm);
		comment.add(comment_box);
		comment.add(commPanel);
		wall.add(comment);
		wall.add(Box.createRigidArea(new Dimension(0, 5)));
	}

	public void change_profile(int userID) {
		curr_profile = userID;
		curr_region = 0;
		create_profile();
		prof_scroller.setViewportView(profile);
		prof_scroller.revalidate();
		change_wall(userID, 0);
	}
	public void change_profile(String username) {
		curr_username = username;
		curr_region = 0;
		create_profile();
		prof_scroller.setViewportView(profile);
		prof_scroller.revalidate();
		change_wall(username, 0);
	}

	public void change_wall(int userID, int regionID) {
		curr_profile = userID;
		curr_region = regionID;
		create_wall();
		wall_scroller.setViewportView(wall);
		wall_scroller.revalidate();
	}
	public void change_wall(String username, int regionID) {
		curr_username = username;
		curr_region = regionID;
		create_wall();
		wall_scroller.setViewportView(wall);
		wall_scroller.revalidate();
	}

	public void edit_profile() {
		save_edit.addActionListener(this);
		edit = new ProfileEditor(wall_width, save_edit);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		//LEAVE A COMMENT
		if (arg0.getSource() == comment_button) {
			// if post is not all whitespace
			String comm = comment_box.getText();
			String stripped_comment = comm.replaceAll("\\s+", "");

			if (!stripped_comment.equals("")) {
				Post post1 = new Post();
//				if (curr_region == 0){
//					post1.setRegion(RegionType.PUBLIC);
//				} else if (curr_region == 1){
//					post1.setRegion(RegionType.PRIVATE);
//				}
				post1.setRegionId(curr_region);
				post1.setOwnerName(curr_username);
				post1.setText(comm);
				try {
					Error e = myClient.post(post1);
					if (e == Error.SUCCESS)
						change_wall(curr_profile, curr_region);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		//EDIT PROFILE
		} else if (arg0.getSource() == save_edit){
			System.out.println("saving");
			//send new profile info to server
			try {
				String[] fields = ((ProfileEditor) edit).get_fields();
				Profile newProfile = new Profile(myUserName, fields[0], fields[1]); 
				newProfile.setTitle(Title.valueOf(fields[2].toUpperCase()));
				newProfile.setFamily(fields[3]);
				//Avatar
				// Get Image
				if (!fields[4].equals("")){
				    ImageIcon icon = new ImageIcon(fields[4]);
				    Image image = icon.getImage();
				    // Create empty BufferedImage, sized to Image
				    BufferedImage bi = 
				      new BufferedImage(
				          image.getWidth(null), 
				          image.getHeight(null), 
				          BufferedImage.TYPE_INT_ARGB);
				    // Draw Image into BufferedImage
				    Graphics g = bi.getGraphics();
				    g.drawImage(image, 0, 0, null);
					newProfile.setAvatar(SerializableAvatar.bufImageToSerialAvatar(bi));
				}
				//tell client to edit profile
				Error e = myClient.editProfile(newProfile);
				if (e == Error.SUCCESS)
					change_profile(myUserName);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		//SEARCH
		else if (arg0.getSource()==search_button){
			//make sure search bar isn't empty
			String query = search_box.getText();
			String stripped_query = query.replaceAll("\\s+", "");
			if (!stripped_query.equals("")) {
				//get profile of user
				Profile searchProfile = new Profile(stripped_query);
				try {
					common.Error search_error = myClient.viewProfile(searchProfile);
					if (search_error == common.Error.SUCCESS){
						//display profile
						change_profile(stripped_query);
					}
					//else: do nothing
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
			}

		}
		//ADD A FRIEND
		else if (arg0.getSource()==add_friend){
			try{
				myClient.addFriend(curr_username);
				add_friend.setVisible(false);
				rem_trust.setVisible(true);
				rem_friend.setVisible(true);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		//REMOVE A FRIEND
		else if (arg0.getSource()==rem_friend){
			try{
				myClient.deleteFriend(curr_username);
				rem_friend.setVisible(false);
				rem_trust.setVisible(false);
				add_trust.setVisible(false);
				add_friend.setVisible(true);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		//REMOVE TRUST
		else if (arg0.getSource()==rem_trust){
				//TODO: rem trust
			rem_trust.setVisible(false);
			add_trust.setVisible(true);
		}
		//ADD TRUST
		else if (arg0.getSource()==add_trust){
				//TODO: add trust
			rem_trust.setVisible(true);
			add_trust.setVisible(false);
		}
		//CREATE NEW COVERT BOARD
		else if (arg0.getSource()==add_covert){
			//TODO:open a dialog box
			//enter comma-separated usernames
			//create a new board that only those users can view
			//update profile
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getSource() == logo) {
			System.out.println(arg0.getSource().getClass().getName());
			// change wall to user's own wall.
			change_profile(myUserName);
		}
		// else if link clicked is of type username:
		else if (arg0.getSource() instanceof Userlink) {
			// System.out.println(((Userlink)arg0.getSource()).get_username());
			String new_user = ((Userlink) arg0.getSource()).get_username();
			change_profile(new_user);
		}
		// else if link clicked is of type region:
		else if (arg0.getSource() instanceof Regionlink) {
			// System.out.println(((Regionlink)arg0.getSource()).get_regionname());
			int new_region = ((Regionlink) arg0.getSource()).get_regionid();
			String same_user = ((Regionlink) arg0.getSource()).get_username();
			change_wall(same_user, new_region);
		} else if (arg0.getSource() == edit_button) {
			// change profile to user's own
			change_profile(myUserName);
			edit_profile();
			wall_scroller.setViewportView(edit);
			// change wall to edit page
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