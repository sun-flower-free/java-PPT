#!/bin/sh
javac src/*.java -d ./out
echo 'Main-Class: entrance' >>out/manifest.txt
cd out
mkdir release
jar cfm release/Java-PPT.jar manifest.txt *.class
