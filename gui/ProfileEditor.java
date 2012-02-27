package gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ProfileEditor extends JPanel{
	private JTextField fnameEntry;
	private JTextField lnameEntry;
	private JTextField famEntry;
	private JComboBox titleEntry;
	private JTextField imgEntry;
	JLabel warning = new JLabel("First name and last name are required");
	JButton save_edit;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProfileEditor(int wall_width, JButton s_e){
		save_edit = s_e;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setMinimumSize(new Dimension(wall_width,300));
		setMaximumSize(new Dimension(wall_width,300));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		setAlignmentX((float) 0.0);
		
		//title
		JLabel ep = new JLabel("Edit Profile");
		ep.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		ep.setMinimumSize(new Dimension(wall_width,20));
		ep.setMaximumSize(new Dimension(wall_width,20));
		add(ep);
		add(Box.createRigidArea(new Dimension(0,10)));
		
		//fields
		JPanel fields = new JPanel();
		fields.setLayout(new BoxLayout(fields, BoxLayout.PAGE_AXIS));
		JPanel names = new JPanel();
		names.setBackground(Color.WHITE);
		names.setLayout(new BoxLayout(names, BoxLayout.LINE_AXIS));
		names.setMinimumSize(new Dimension(300, 40));
		names.setMaximumSize(new Dimension(300, 40));
		
		JPanel status = new JPanel();
		status.setLayout(new BoxLayout(status, BoxLayout.LINE_AXIS));
		status.setBackground(Color.WHITE);
		status.setMinimumSize(new Dimension(300, 40));
		status.setMaximumSize(new Dimension(300, 40));
		
		//add first name
		JPanel fname_panel = new JPanel();
		fname_panel.setLayout(new BoxLayout(fname_panel, BoxLayout.PAGE_AXIS));
		fname_panel.setBackground(Color.WHITE);
		JLabel fnamePrompt = new JLabel("First name: ");
		fnameEntry = new JTextField(8);
		fname_panel.setMinimumSize(new Dimension(140, 40));
		fname_panel.setMaximumSize(new Dimension(140, 40));
		fname_panel.add(fnamePrompt);
		fname_panel.add(fnameEntry);
		names.add(fname_panel);
		//add last name
		JPanel lname_panel = new JPanel();
		lname_panel.setLayout(new BoxLayout(lname_panel, BoxLayout.PAGE_AXIS));
		lname_panel.setBackground(Color.WHITE);
		JLabel lnamePrompt = new JLabel("Last name: ");
		lnameEntry = new JTextField(8);
		lname_panel.setMinimumSize(new Dimension(140, 40));
		lname_panel.setMaximumSize(new Dimension(140, 40));
		lname_panel.add(lnamePrompt);
		lname_panel.add(lnameEntry);
		names.add(Box.createRigidArea(new Dimension(20,0)));
		names.add(lname_panel);
		//add title
		JPanel title_panel = new JPanel();
		title_panel.setLayout(new BoxLayout(title_panel, BoxLayout.PAGE_AXIS));
		title_panel.setBackground(Color.WHITE);
		JLabel titlePrompt = new JLabel("Title: ");
		String[] mafiaTitles = { "Boss", "Capo", "Soldier", "Assoc." };
		titleEntry = new JComboBox(mafiaTitles);
		title_panel.setMinimumSize(new Dimension(140, 40));
		title_panel.setMaximumSize(new Dimension(140, 40));
		title_panel.add(titlePrompt);
		title_panel.add(titleEntry);
		status.add(title_panel);
		//add family
		JPanel fam_panel = new JPanel();
		fam_panel.setLayout(new BoxLayout(fam_panel, BoxLayout.PAGE_AXIS));
		fam_panel.setBackground(Color.WHITE);
		JLabel famPrompt = new JLabel("Family: ");
		famEntry = new JTextField(8);
		fam_panel.setMinimumSize(new Dimension(140, 40));
		fam_panel.setMaximumSize(new Dimension(140, 40));
		fam_panel.add(famPrompt);
		fam_panel.add(famEntry);
		status.add(Box.createRigidArea(new Dimension(20,0)));
		status.add(fam_panel);
		
		add(names);
		add(Box.createRigidArea(new Dimension(0,10)));
		add(status);
		//add profile picture
		JPanel img_panel = new JPanel();
		img_panel.setLayout(new BoxLayout(img_panel, BoxLayout.LINE_AXIS));
		img_panel.setBackground(Color.WHITE);
		JLabel imgPrompt = new JLabel("Profile picture: ");
		imgEntry = new JTextField(8);
		img_panel.setMinimumSize(new Dimension(300, 30));
		img_panel.setMaximumSize(new Dimension(300, 30));
		img_panel.add(imgPrompt);
		img_panel.add(imgEntry);
		add(Box.createRigidArea(new Dimension(0,10)));
		add(img_panel);
		add(Box.createRigidArea(new Dimension(0,10)));

		//add save button
		JPanel save_panel = new JPanel();
		save_panel.setLayout(new BoxLayout(save_panel, BoxLayout.LINE_AXIS));
		save_panel.setBackground(Color.WHITE);
		save_panel.setMinimumSize(new Dimension(300, 30));
		save_panel.setMaximumSize(new Dimension(300, 30));
		save_edit.setAlignmentX((float) 1.0);
		save_panel.add(Box.createHorizontalGlue());
		warning.setForeground(Color.RED);
		save_panel.add(warning);
		warning.setVisible(false);
		save_panel.add(save_edit);
		add(save_panel);
	}
	public String[] get_fields(){
		String[] fields = new String[5];
		fields[0] = fnameEntry.getText();
		fields[1] = lnameEntry.getText();
		fields[2] = (String)titleEntry.getSelectedItem();
		fields[3] = famEntry.getText();
		fields[4] = imgEntry.getText();
		return fields;
	}
	//did all required fields get filled in?
	public boolean reqs_filled(){
//		if 
//		fields[0] = fnameEntry.getText();
//		fields[1] = lnameEntry.getText();
//		fields[2] = titleEntry.getText();
//		fields[3] = famEntry.getText();
//		
//		if
		return false;
	}
	
	
}
