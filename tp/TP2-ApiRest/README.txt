				REST Api in Smalltalk

Benjamin Van Ryseghem
Francois Lepan
27/03/2013


============
Introduction
============

	This is an API for the REST software architecture. It handle REST request and send them to a file manager. Here we use the previously developed FTP Server to manage files.



============
Issue
============

	The binary transfer corrupt the file probably because of a bug in the architecture.



============
Architecture
============

Packages :
----------

-----------------RestAPI-----------------
	The classes that handle the REST Request and store
	login information for the TFPServer.



-----------------RestAPI-Client-----------------
	The classes that display datas retrieved from REST
	queries.




Classes:
--------

-----------------RestAPI-----------------

---------------------------------------------------------RARestHandler	I am the REST Handler.	My role is to catch rest commands and handle them.	I have methods tagged with pragmas indicating which
	kind of REST verb they fulfill, plus a kind of regex 
	for url matching.	Then I delegate the action to a RAWrapper, and
	simply return what I get back.
---------------------------------------------------------RAAbstractBindingWrapper	I am an abstract class for all the bindings between
	the REST api and a way to manage files.	My purpose is to provide a way to easily extent the
	REST api backend (FTP, FS, ...)---------------------------------------------------------RAAnonymousLoginInformation	I am information about login anonymously to a ftp
	server---------------------------------------------------------RALoginInformation	I am used to log in to a FTP Server---------------------------------------------------------RAFTPWrapper	I am a binder from the REST api to FTP---------------------------------------------------------


-----------------RestAPI-Client-----------------

---------------------------------------------------------RAUploadFileComponent	I am the component used to present the form required
	to upload a file to the server---------------------------------------------------------RACreateDirectoryComponent	I am the component used to present the form required
	to create a new directory---------------------------------------------------------RAAnswerComponent	I am a component use to render a list request---------------------------------------------------------RAAuthentificationComponent	I am an abstract component for component requiring
	to send an authentification to the server---------------------------------------------------------RARootComponent	I am the root component for the whole web site


---------------------------------------------------------


============
Code Samples
============

  Snippet to start the server
-----------------------------

(ZnZincServerAdaptor port: 8080)    codec: GRPharoUtf8Codec new;    start



  Snippet to generate the source file from the classes
------------------------------------------------------

packages := RPackageOrganizer default packages select:[:each |	(each name beginsWith: 'RestAPI') and: [ (each name endsWith: '-Tests') not ]].packages do:[ :e || dir |	dir := (FileSystem workingDirectory resolve: e name).	dir ensureDirectory.	e classes do: [ :c || file |		file := dir resolve: c name.		file ensureFile.		file writeStreamDo: [:s |			s << c definition ; cr;cr; << c comment;cr;cr .			s<< '=========================' ;cr.			c protocols do: [:p |				s cr; << p; cr;cr; << '---------------------';cr;cr.				(c methodsInProtocol: p) do: [ :m |					s << m sourceCode;cr;cr ].				s << '---------------------' ] ] ] ]




  Snippet to generate the documentation
---------------------------------------

packages := RPackageOrganizer default packages select:[:each |
	(each name beginsWith: 'RestAPI') and: [ (each name endsWith: '-Tests') not ]].

Clipboard clipboardText: (String streamContents: [:s | 
packages do: [:p | 
	s << '================='; cr ; cr; << (p name) ; cr ; cr ; << '================='; cr .
	p definedClasses do: [:c |
		s << c name ; cr ; tab ; << (c comment) ;cr;cr
	]]])
