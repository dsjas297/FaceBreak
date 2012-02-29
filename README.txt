README

There are 3 main packages that correspond to the three components of our system:
1) GUI/frontend (Boiar)
    - interface allows users to log in or sign up, then view and post information to different profiles
2) Networking (Gaomin)
    - relays messages over network (Java sockets) from GUI to server
3) Server/backend (Jasdeep)
    - Stores permanent information to disk
        - This includes user ID’s and name, other profile information, posting regions and the their associated info, including the list of permitted users, and every post that is made in the region.
    - Retrieves information from disk
    - Sets up and maintains a directory structure that enables it to perform the above tasks

Additional package common contains some classes shared among the aforementioned three.

********************************************************************************************

CONTENTS
-common
--Board
--Error
--FBClientUser
--GenericPost
--Post
--Profile
--Region
--SerializableAvatar
--Title
--User

-dummyserver
--DummyQuery

-gui
--FBPage
--FBWindow
--LaunchGui
--LimitedText
--Login
--ProfileEditor
--Regionlink
--Userlink

-networking
--AuthenticatedUser
--Client
--Content
--FBClient
--FBClientHandler
--FBServer
--Reply
--Request
--SampleMain

-server
--FaceBreakUser
--FaceBreakRegion
--ServerBackend



********************************************************************************************
----GUI----

After starting FBServer:
LaunchGui: Opens a new FBWindow.

FBWindow: Displays either a Login panel or an FBPage panel.
    -Login procedure: Displays Login panel. Allows one user to sign up for an account or log in to an existing account. Signing up calls the client to create a new user with a given username and password. Both methods call the client to log in the user. Upon successful log in, FBWindow will display an FBPage.
    -FBPage procedure: If a user clicks on “Logout” in the top right corner, FBWindow will ask the client to log out and re-display the Login panel.

FBPage: Displays a menu at the top with home, search, profile edit, and logout options. Displays the user’s profile in the left pane, and one of the user’s region in the right.
    -Home (“FaceBreak” label): Allows user to view his own profile and public region.
    -Search: Searching for a username will return the profile and public region for that user. Assumes existing usernames.
    -Profile edit: Displays user profile normally. In place of public region, displays a panel that allows user to modify his first name, last name, title, family, and profile picture.
    -User Profile: Displays a user’s first name, last name, title, family, and profile picture (if it exists). Displays Regionlinks to access user’s regions (public, private, and coverts). Currently displays all regions.
    -User Region: Always defaults to public region (same user whose profile is being displayed). Shows a comment box, and calls client to get all (if any) posts ever made in the region. Each post contains the name (first and last) of its writer, the message, and the time it was posted. Clicking on the name of the writer will display their profile.

LimitedText: Text field with character limit

********************************************************************************************

----NETWORKING----

This layer takes care of
(1) creating messages that correspond to different users' actions,
(2) serializing messages,and sending them over the network to the server
(here we assume that client and server are operating from the same machine, localhost),
(3) doing basic error checking (additional security features to be implemented later),
(4) deserializing messages on the server side and "querying" the server for information
that client user needs/expects,
(5) returning the information to the client in a usable format (or an error message).

Users are allowed the following actions: login, logout, create new user, view profile, edit profile,
create post, view all posts, add friend, and (to be implemented) post deletion.

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
    - It contains methods to add a user, check if user exists, add a friend for a user, delete a friend for a user, mark other users trustworthy or untrustworthy, and edit profile information.
--ServerBackend
    - Function initDirTree() initializes the directory structure by creating an empty users file if one does not exist, and creating the userID file which we
    - Other functions are createPost, viewPosts, addFriend, and deleteFriend
    - This does repeat some functionality in the other files and will have to be refactored

INSTALLATION
1. Unzip the files. You will see a folder titled “FaceBreak”
2. Open Eclipse (download at http://www.eclipse.org/downloads/moreinfo/java.php)
3. Create a new project named FaceBreak from the “FaceBreak” folder you just unzipped.

COMPILING
4. File > Export: Select Java > JAR file
5. Under Resources to Export, select FaceBreak (left pane), .classpath and .project (right pane). Set the export destination to [current working directory]/FaceBreak.jar

RUNNING
6. Run FaceBreakServer.bat
7. Run FaceBreakGui.bat

TUTORIAL
[Log in screen]
Signing up: Enter desired username/password combination. Click “Sign up”.
Logging in: Enter username/password combination. Click “Log in”.

[FaceBreak screen after logging in]
Log out: Click on “Logout” in the top right hand corner.
Edit Profile: Click on “Edit” in the top right hand corner.
Access user’s own profile: Click on the FaceBreak logo in the top left hand corner.
Search user: Type an existing username into the search bar. Click “Search”. That user’s profile should now be displayed.

[Edit Profile]
Fill in first name, last name, title, and family fields. Click “Save”. You should see immediate updates to your profile.

[Adding friends/viewing users]
Search for another user to view their profile. In the left pane, click on “Add friend” to add them as a friend.

[Viewing posts]
The right pane shows public region posts for the user whose profile is currently being viewed. To view posts in other regions, select the appropriate region from the list in the left pane.

[Posting to a region]
Type a comment in the test box at the top of the right pane. Click “Post” to post the comment and see the region updated immediately.
