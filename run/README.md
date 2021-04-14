## run

This folder contains files to build and run the different spark projects. 

###  Build Scala Fat-Jar

The "run" directory contains a "Dockerfile", "entypoint.sh" and "docker-compose" file.
Those are for the purpose of building a "Scala" jar file which can be uploaded to the spark server.

To build the project copy and rename the template folder. Now change the path configuration inside the
"template.env".

Now run the script with "./assambly.sh -b". The with the "-b" modifier builds the docker container first and is only needed the first time.

### Run spark application

To run the Fat-Jar your use the "submit.sh". The file contains a few variables for the Jar path and class names you need to configure first for the script to function correct.