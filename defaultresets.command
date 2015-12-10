#!/bin/sh


DIRNAME=`dirname $0`
CP=$DIRNAME/defaultresets.jar
java -cp $CP uk.chromis.defaultresets.DefaultResets
