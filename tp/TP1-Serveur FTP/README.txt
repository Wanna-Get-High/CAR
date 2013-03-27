
				FTP Server in SmallTalk

Benjamin Van Ryseghem
Francois Lepan
6/03/2013


============
Introduction
============

	This program is used to create a FTP server with the basic function USER, PASS, PORT, RETR, STOR, LIST, QUIT, PWD, CWD, CDUP, MKD, RMD, PASV.
	It uses the flyweight pattern which is used for the users of the server.


============
Architecture
============


Packages :
----------

------------------
FTPServer-Core
------------------
	The classes that creates and destroy the FTP server.

------------------
FTPServer-Commands
------------------
	The useful commands of the FTP Server (USER, PORT, etc).

------------------
FTPServer-Users
------------------
	The relative classes of the FTP server users.

------------------
FTPServer-Types
------------------
	The classes that allow the different data transfer types.
	(here only binary and ASCII)


Classes:
--------

------------------FTPServer-Core------------------
	---------------------------------------------------------	FTPServerLauncher		A FTPServerLauncher is in charge of handling connections.		I use a multithreaded server and forward request to an ftp command handler.			Usage:			| server |	    			server := FTPServerLauncher new.			server port: 2163.			server start.	    			"To stop it"			server stop	---------------------------------------------------------	FTPServerBehavior		FTPServerBehavior encapsulates the behavior of the FTP server (without taking care of the connection part).		A FTPServerBehavior handle the connection with a client (associated to an user).		It loops to handle command until the user quit (or the connection is lost)	---------------------------------------------------------
------------------FTPServer-Commands------------------
	---------------------------------------------------------	NLSTCommand
		Much like the LISTCommand I return a list of paths in the current working directory for completion. 
		But unlike the LISTCommand I only return the base names as crlf separated list.	---------------------------------------------------------	DELECommand		A DELECommand is used to remove a file from the server (write access is mandatory)	---------------------------------------------------------	STORCommand		A STORCommand is used to send a file from the client to the server	---------------------------------------------------------	NOOPCommand		An NOOP command does nothing and is usually just used to keep the session alive and returns only an OK response	---------------------------------------------------------	CDUPCommand		A CDUPCommand is the command to "cd .."	---------------------------------------------------------	CWDCommand		A CWDCommand is the command corresponding to cd (change working directory)	---------------------------------------------------------	MDTMCommand		A MDTMCommand is used to return the last modification time of a file.		Format: "YYYYMMDDhhmmss": YYYY is the four-digit year, 
					  MM is the month from 01 to 12, 
					  DD is the day of the month from 01 to 31, 
					  hh is the hour from 00 to 23, 
					  mm is the minute from 00 to 59, 
					  ss is the second from 00 to 59.	---------------------------------------------------------	USERCommand		An USERCommand is the command used by a ftp client for log in a user	---------------------------------------------------------	FTPCommand		A FTPCommandHandler is in charge of interpreting ftp commands	---------------------------------------------------------
	SIZECommand		A SIZECommand is used to retrieve the size of a file stored on the server	---------------------------------------------------------
	PWDCommand    		A PWDCommand is used to print the current working directory	---------------------------------------------------------	RMDCommand		A RMDCommand is used to remove a directory (write access is mandatory)	---------------------------------------------------------	RETRCommand    		A RETRCommand is used by the client to retrieve a file from the server	---------------------------------------------------------	MKDCommand		A MKDCommand is used to create a directory (write access is mandatory)	---------------------------------------------------------	PASSCommand    		A PASSCommand is the command used to check the password	---------------------------------------------------------	PASVCommand		A PASVCommand is used to switch to passive mode	---------------------------------------------------------	UndefinedCommand	    	An UndefinedCommand is invoked when the server does not understand the command sent by the client	---------------------------------------------------------	LISTCommand    		A LISTCommand is used for listing files and directory starting from current working directory	---------------------------------------------------------	QUITCommand    		A QUITCommand is used to close the connection with the server	---------------------------------------------------------	PORTCommand    		A PORTCommand is used to specify data transfer port.		RFC 959		The argument is a HOST-PORT specification for the data port            	to be used in data connection.  There are defaults for both            	the user and server data ports, and under normal            	circumstances this command and its reply are not needed.  If            	this command is used, the argument is the concatenation of a            	32-bit internet host address and a 16-bit TCP port address.            	This address information is broken into 8-bit fields and the            	value of each field is transmitted as a decimal number (in            	character string representation).  The fields are separated            	by commas.  A port command would be:               	PORT h1,h2,h3,h4,p1,p2            	where h1 is the high order 8 bits of the internet host address.	---------------------------------------------------------	TYPECommand    		A TYPECommand is used to switch between transfer types
	
	---------------------------------------------------------------------------FTPServer-Users------------------	---------------------------------------------------------	FTPUser		A FTPUser is a user of the ftp server		This class also act as a flyweight (more or less the database for the server)		A user is a data object and this class simulates a database.		Usage:    			FTPUser 
			    login: 'username' 			    password: 'password'	---------------------------------------------------------
	FTPAnonymousUser		An FTPAnonymousUser is an anonymous user with read only access	---------------------------------------------------------	FTPInvalidUser		An FTPInvalidUser is an invalid user, i.e. we already know that the login used at connection is wrong, 
		but for security purpose we still ask for a password	---------------------------------------------------------------------------FTPServer-Types
------------------
	---------------------------------------------------------	FTPBinaryType    		A FTPBinaryType is used to binary transfer (ftp command image)	---------------------------------------------------------
	FTPASCIIType    		A FTPASCIIType is used to transfer text (ftp command ascii)	---------------------------------------------------------
	FTPType    		A FTPType is the abstract class for the different kind of transfer types

	---------------------------------------------------------

============
Code Samples
============
	
- Here is a code sample that is used to trigger the #commandHandle: method of the right class depending of the command received from the client:

    commandHandle: data

 	"This method match the incoming command with the command class"

 	| substrings command arguments className class |
  	
  	substrings := data substrings.

 	"We retrieve the command name"
  	command := substrings first.
  	arguments := substrings allButFirst joinUsing: ' '.
  	
 	"The class name is the command name concatenated with 'Command'"
  	className := (command, 'Command') asSymbol.
  	
 	"Then we use the ClassDictionary to retrieve the class "
  	class := self class environment at: className ifAbsent:[ UndefinedCommand ].
 	
 	"We log the received command if in debug mode"
  	self logCr: data.
 	
  	^ class handleCommand:command arguments: arguments from: self


- Snippet to start the server:
	| server |
	server := FTPServerLauncher new.
	server port: 2122.
	server execute.

	“To stop the server”
	server stop.

=======
Pattern
=======

- There is a flyweight pattern implemented for the users. It acts as the database of the registered clients for the server. 
   It uses the classes FTPUser, FTPInvalidUser, FTPAnonymousUser.

===========
Source code
===========

Here is the link to download a Pharo image with the code loaded:
	https://ci.inria.fr/pharo-contribution/job/FTP-Server/lastSuccessfulBuild/artifact/FTP-Server.zip

Here is a link to download the latest build of the VM: 
	https://ci.inria.fr/pharo/view/VM/job/PharoVM/
