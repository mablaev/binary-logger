#!/bin/sh

THE_CLASSPATH=
PROGRAM_NAME=Main.java

rm -fr build
mkdir build

cd src

javac -classpath ".:${THE_CLASSPATH}" -d ../build com/logger/$PROGRAM_NAME

cd ../build

echo Main-Class: com.logger.Main > MANIFEST.MF
jar -cvmf MANIFEST.MF binary-logger.jar com/logger/*

if [ $? -eq 0 ]
then
    echo "compiled!"
fi