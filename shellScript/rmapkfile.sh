#!/bin/bash
svnapkPath="/home/peng/project/apk/debug"
currentDate=`date +%Y%m%d`

cd "$svnapkPath"
apkArray=`ls "$svnapkPath" | grep -v "$currentDate"`

for apk in ${apkArray[@]};
do
svn rm "$apk"
done

svn ci . -m "rm apk file"



