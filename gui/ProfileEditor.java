package facebreak.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ProfileEditor extends JPanel{
	
	public ProfileEditor(int wall_width){
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setMinimumSize(new Dimension(wall_width,150));
		setMaximumSize(new Dimension(wall_width,150));
		setBackground(Color.white);
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		setAlignmentX((float) 0.0);
		
		//title
		JLabel title = new JLabel("Edit Profile");
		add(title);

		//fields
		JPanel fields = new JPanel();
		fields.setLayout(new BoxLayout(fields, BoxLayout.PAGE_AXIS));
		JPanel names = new JPanel();
		names.setLayout(new BoxLayout(names, BoxLayout.LINE_AXIS));
		JPanel status = new JPanel();
		status.setLayout(new BoxLayout(status, BoxLayout.LINE_AXIS));
		
		//add first name
		JPanel fname_panel = new JPanel();
		fname_panel.setLayout(new BoxLayout(fname_panel, BoxLayout.PAGE_AXIS));
		JLabel fnamePrompt = new JLabel("First name: ");
		JTextField fnameEntry = new JTextField(8);
		fname_panel.add(fnamePrompt);
		fname_panel.add(fnameEntry);
		fname_panel.add(Box.createHorizontalGlue());
		names.add(fname_panel);
		//add last name
		JPanel lname_panel = new JPanel();
		lname_panel.setLayout(new BoxLayout(lname_panel, BoxLayout.PAGE_AXIS));
		JLabel lnamePrompt = new JLabel("Last name: ");
		JTextField lnameEntry = new JTextField(8);
		lname_panel.add(lnamePrompt);
		lname_panel.add(lnameEntry);
		names.add(lname_panel);
		//add title
//		JPanel title_panel = new JPanel();
//		fname_panel.setLayout(new BoxLayout(fname_panel, BoxLayout.LINE_AXIS));
//		JLabel fnamePrompt = new JLabel("First name: ");
//		JTextField fnameEntry = new JTextField(8);
//		fname_panel.add(fnamePrompt);
//		fname_panel.add(fnameEntry);
		//add family
		JPanel fam_panel = new JPanel();
		fam_panel.setLayout(new BoxLayout(fam_panel, BoxLayout.LINE_AXIS));
		JLabel famPrompt = new JLabel("Family: ");
		JTextField famEntry = new JTextField(8);
		fam_panel.add(famPrompt);
		fam_panel.add(famEntry);
		status.add(fam_panel);
		
		add(names);
		add(status);
		//add profile picture
		JPanel img_panel = new JPanel();
		img_panel.setLayout(new BoxLayout(img_panel, BoxLayout.LINE_AXIS));
		JLabel imgPrompt = new JLabel("Profile picture: ");
		JTextField imgEntry = new JTextField(8);
		img_panel.add(imgPrompt);
		img_panel.add(imgEntry);
		add(img_panel);
		//add save button
		
	}
	
	
	
	
}
