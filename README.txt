README

There are 3 main packages that correspond to the three components of our system:
1) GUI/frontend (Boiar)
	-  interface that users interact with
2) Networking (Gaomin)
	- relays messages over network (Java sockets) from GUI to server
3) Server/backend (Jasdeep)
	- stores permanent information to disk
	- retrieves information from disk

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

--FaceBreakRegion
	- FaceBreakRegion contains the server side code for creating, managing, and interacting with regions (which serve as posting boards)
	- It creates and manages the files on the server that store the information for a region as well as all posts contained in that region
--FaceBreakUser

users
- Include this file in the same directory that the main program is being run
- It represents the list of users
userID
- Include this file in the same directory that the main program is being run
- It stores the next user ID number that should be assigned to any new user

INSTALLATION
[<10 min]

- Copy the 

COMPILING

RUNNING/TUTORIAL
