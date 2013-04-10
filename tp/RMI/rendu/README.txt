				RMI application in Java

Benjamin Van Ryseghem
Francois Lepan
10/04/2013


============
Introduction
============

	This is an application that use the RMI protocol. It is used to create a tree of node and transfer string message between node. 


============
Architecture
============

	There is the interface that gives the basic methods of the RMI object and the RMI Object that implements methods of the interface.

============
Code Samples
============

  Snippet to create a series of nodes
  -----------------------------------

	# between each instruction open a new terminal
	# create a node named node1
	java -jar rmi.jar "//localhost/node1"

	# create a son to node1 named node2
	java -jar rmi.jar "//localhost/node2" "//localhost/node1"

	# create a son to node2 named node3
	java -jar rmi.jar "//localhost/node3" "//localhost/node2"

	# create a son to node3 named node4
	java -jar rmi.jar "//localhost/node4" "//localhost/node3"

	# then into terminal of node 1 and 2 type:
	addFather //localhost/node4
	
	# this will add the node 4 as father of the node 1 and 2
	# it will create the node structure (see nodes.png)

   Message handled when a node is created (prompt)
   -----------------------------------------------

	- addFather: 	adds a father to the current node
	- childrenSize: displays the number of children
	- fathersSize:  displays the number of fathers
	- exit: 	exits