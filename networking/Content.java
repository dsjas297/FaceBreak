package networking;

import java.io.Serializable;

import common.FBClientUser;
import common.Post;
import common.Profile;
import common.Region;



/*
 * Wrapper class
 */
public class Content implements Serializable {
	
	public class UsernamePwd extends Content {
		private String username;
		private String pwd;
		
		public UsernamePwd(String username, String pwd) {
			this.username = username;
			this.pwd = pwd;
		}
		
		public String getUsername() {
			return username;
		}
		
		public String getPassword() {
			return pwd;
		}
	}
}
