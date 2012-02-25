package facebreak.gui;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

//public class ImageTest {
//
//  public static void main(String[] args) {
//    ImagePanel panel = new ImagePanel(new ImageIcon("gangster.png").getImage());
//
//    JFrame frame = new JFrame();
//    frame.getContentPane().add(panel);
//    frame.pack();
//    frame.setVisible(true);
//  }
//}

public class ImagePanel extends JPanel {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private Image img;
  public ImagePanel(String img) {
    this(new ImageIcon("gangsters.jpg").getImage());
  }

  public ImagePanel(Image img) {
    this.img = img;
    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
    setPreferredSize(size);
    setMinimumSize(size);
    setMaximumSize(size);
    setSize(size);
    setLayout(null);
  }

  public void paintComponent(Graphics g) {
	super.paintComponent(g);
    g.drawImage(img, 0, 0, this);
  }

}

           