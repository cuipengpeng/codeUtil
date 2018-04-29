#!/bin/bash
export GRADLE_USER_HOME="$HOME/.gradle/"

fileName="$1"

filePath=`find . -iname ${fileName} -type f`
svn revert $filePath -R
