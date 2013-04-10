#!/bin/bash

# usage :
# 	./node.sh <node> [father]

if [[ -n "$1" ]] ; then
  	cd bin; rmiregistry & cd ..;
	java -jar rmi.jar $1 $2
else
	echo "usage :"
	echo "	./node.sh <node> [father]"
fi