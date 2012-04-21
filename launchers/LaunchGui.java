package launchers;

import gui.FBWindow;

public class LaunchGui {
	public static FBWindow window;

	/**
	 * Call LaunchGui after FBServer to open the GUI to Login page.
	 */
	public static void main(String[] args) {
		window = new FBWindow();
	}
}
