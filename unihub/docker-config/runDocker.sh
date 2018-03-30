#! /bin/bash

# s/init/run/g if the image already exists

# init the database
cd database/
./runDatabase.sh
cd ../

# init the webserver
cd appserver
./runAppServer.sh
cd ../

