## run

This folder contains files to build and run the different spark projects. 

###  Build Scala Fat-Jar

The "run" directory contains a "Dockerfile", "entypoint.sh" and "docker-compose" file.
Those will start a container which will build  a "Scala" jar file you can use. 

To build the project copy and rename the template folder. Now change the configuration inside the new folder to meet your need project.

Now run the script inside your folder with "./assambly.sh -b". The with the "-b" modifier builds the docker container first and is only needed the first time.

### Run spark application

To run the Fat-Jar your use the "submit.sh" inside your folder. The file contains a few variables for the Jar path and class names you need to configure first for the script to function correct.