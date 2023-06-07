#!/bin/sh

# compile
javac -d bin $(find src -name '*.java')

# run
java -cp bin dev.ambi.localchat.Main

# clean
rm -r bin/*
