README

There are 3 main packages that correspond to the three components of our system:
1) GUI/frontend (Boiar)
    - interface allows users to:
    	- Log in or sign up
    	- View and post information to different profiles
		- Add friends
		- Change profile information
		- Display and act on notifications
2) Networking (Gaomin)
	- Establish a secure channel of communication
    - Relays messages over network (Java sockets) from GUI to server
3) Server/backend (Jasdeep)
    - Securely stores permanent information to disk
        - This includes user ID's and name, other profile information, posting regions and the their associated info, including the list of permitted users, and every post that is made in the region.
    - Retrieves information from disk
    - Sets up and maintains a directory structure that enables it to perform the above tasks

Additional package common contains some classes shared among the aforementioned three.

********************************************************************************************

CONTENTS
-common
--Error
--FBClientUser
--GenericPost
--Notification
--Post
--Profile
--Region
--Title
--User

-gui
--FBPage
--FBWindow
--FriendsPage
--LimitedText
--Login
--NotifButton
--NotificationPage
--ProfileEditor
--Regionlink
--Userlink

-launchers
--LaunchGui
--LaunchServer

-messages
--AsymmetricKEM
--GenericMsg
--Item
--ItemList
--MsgSealer
--MsgWrapper
--Reply
--Request
--SymmetricKEM

-networking
--AsymmetricKeys
--AuthenticatedUser
--Client
--FBClient
--FBClientHandler
--FBServer
--RandomGenerator

-server
--FaceBreakUser
--FaceBreakRegion
--FileSystem



********************************************************************************************
----GUI----

After starting FBServer:
LaunchGui: Opens a new FBWindow.

FBWindow: Displays either a Login panel or an FBPage panel.
    -Login procedure: Displays Login panel. Allows one user to sign up for an account or log in to an existing account. Signing up calls the client to create a new user with a given username and password. Both methods call the client to log in the user. Upon successful log in, FBWindow will display an FBPage.
    -FBPage procedure: If a user clicks on â€œLogoutâ€� in the top right corner, FBWindow will ask the client to log out and re-display the Login panel.

FBPage: Displays a menu at the top with home, search, profile edit, and logout options. Displays the userâ€™s profile in the left pane, and one of the userâ€™s region in the right.
    -Home (â€œFaceBreakâ€� label): Allows user to view his own profile and public region.
    -Search: Searching for a username will return the profile and public region for that user. Assumes existing usernames.
    -Profile edit: Displays user profile normally. In place of public region, displays a panel that allows user to modify his first name, last name, title, family, and profile picture.
    -User Profile: Displays a user's first name, last name, title, family, and profile picture (if it exists). Displays Regionlinks to access userâ€™s regions (public, private, and coverts). Currently displays all regions.
    -User Region: Always defaults to public region (same user whose profile is being displayed). Shows a comment box, and calls client to get all (if any) posts ever made in the region. Each post contains the name (first and last) of its writer, the message, and the time it was posted. Clicking on the name of the writer will display their profile.

FriendsPage: Displays list of user's friends.

NotificationPage: Displays list of notifications. You can approve/deny other user requests.

LimitedText: Text field with character limit

NotifButton, Regionlink, Userlink: actionListeners with user/region/notification information

********************************************************************************************

----NETWORKING----

This layer takes care of
(1) creating messages that correspond to different users' actions,
(2) serializing messages, encrypting, sending over the network to the server
(here we assume that client and server are operating from the same machine, localhost),
(3) decrypt message on recipient's end and deserialize
(4) basic error checking such as checksum; also checking a "counter" (basically a nonce) and 
time stamp to guard against basic replay attacks

Users are allowed the following actions: login, logout, create new user, view profile, edit profile,
create post, view all posts, add friend, delete friend, viewing covert boards and adding users
to covert boards, getting notifications (friend requests and title changes that
need to be approved by the Boss), etc.

The networking package consists of:

FBClient
    - client corresponding to this machine/session;
    - socket for connecting to server's socket
    - GUI creates an FBClient and uses it to make calls
    - see Client interface for a concise listing of all such calls/actions
FBServer
    - creates a ServerSocket bound to a port and waits for connections from clients
    - once it receives a connection, spins up a new thread to handle that connection
FBClientHandler
    - receives 'requests' from the client to complete actions (login, logout, view/post, etc.)
    - parses these requests and makes calls to server to write permanent changes
      to disk, or read from disk
    - returns 'replies' with appropriate information/error messages
Some additional helper classes

********************************************************************************************

----MESSAGES----

These are messages sent over the network, of which there are 3 main types:
1) Requests from Client to Server
2) Replies from Server to Client
3) Key Exchange messages

Requests and replies are encrypted using symmetric AES keys. Key exchange messages, which are much shorter,
are encrypted using RSA public keys.

********************************************************************************************

----SERVER----

The Server package

--FaceBreakRegion
    - FaceBreakRegion contains the server side code for creating, managing, and interacting with regions (which serve as posting boards). These methods are used by the ServerBackend and FBClientHandler classes to interact with region information.
    - It creates and manages the files on the server that store the information for a region as well as all posts contained in that region
    - It has a constructor which creates a representation of a Region object, but any changes made to this object are automatically copied to disk (this should hopefully keep the state consistent)
    - It contains methods to add a region, check if a region exists, add people to the allowed users list, check if a user is allowed to view the region, post to the region, and view all posts in the region
--FaceBreakUser
    - FaceBreakUser contains the server side code for creating and managing users. These methods are used by the ServerBackend and FBClientHandler classes to interact with user information.
    - It creates and manages the files on the server that store the information for a user.
    - It has a constructor which creates a representation of a User object, but any changes made to this object are automatically copied to disk (this should hopefully keep the state consistent)
    - It contains methods to add a user, check if user exists, add a friend for a user, delete a friend for a user, generate notifications, and edit profile information.
--FileSystem
    - Function initDirTree() initializes the directory structure:
    	- Creating an empty users file if one does not exist
    	- Creates user ID file if it doesn't exist (to assign unique user IDs)
    	- Creates notification ID file if it doesn't exist (to assign unique notification IDs)
    	- Creates family / boss file if it doesn't exist
    - Function writeSecure uses a hash of the server password to write data and a hash of the data to disk in an encrypted format
    - Function readSecure reads what writeSecure has written
    - Also contains system setup constants (like the location of the above files)

********************************************************************************************

INSTALLATION
1. Unzip the files. You will see a folder titled FaceBreak
2. Open Eclipse (download at http://www.eclipse.org/downloads/moreinfo/java.php)
3. Create a new project named FaceBreak from the FaceBreak folder you just unzipped.

COMPILING
4. File > Export: Select Java > JAR file
5. Under Resources to Export, select FaceBreak (left pane) and everything in the right pane. Set the export destination to [current working directory]/FaceBreak.jar
Copy the encryptedPrivate.key to the directory that the JAR sits in.

RUNNING
6. Run FaceBreakServer.bat
	- Password is SrrEs5d7Um
7. Run FaceBreakGui.bat

TUTORIAL
[Log in screen]
Signing up: Enter desired username/password combination. Click Sign up.
Logging in: Enter username/password combination. Click Log in.

[FaceBreak screen after logging in]
Log out: Click on Logout in the top right hand corner.
Edit Profile: Click on Edit in the top right hand corner.
Access user's own profile: Click on the FaceBreak logo in the top left hand corner.
Search user: Type an existing username into the search bar. Click "Search". That user's profile should now be displayed.

[Edit Profile]
Fill in first name, last name, title, and family fields. Click "Save". You should see immediate updates to your profile.

[Adding friends/viewing users]
Search for another user to view their profile. In the left pane, click on "Add friend" to add them as a friend.

[Removing friends]
Search for another user to view their profile. In the left pane, click on "Remove friend" to remove them from your friends. This assumes you have added them as a friend previously.

[Viewing friends]
In the left pane, click on "View friends" to view a users friends.

[Viewing posts]
The right pane shows public region posts for the user whose profile is currently being viewed. To view posts in other regions, select the appropriate region from the list in the left pane.

[Posting to a region]
Type a comment in the test box at the top of the right pane. Click "Post" to post the comment and see the region updated immediately.

[Viewing notifications]
Notifications are updated every time an action is performed. The digit next to "Edit" shows the number of notifications pending.
Once you have performed an action on a notification, reload the notifications page to update it.
