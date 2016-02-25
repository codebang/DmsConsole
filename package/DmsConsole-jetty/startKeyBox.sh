#!/bin/bash

cd jetty;

java -Xms1024m -Xmx1024m  -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=1044  -jar start.jar
