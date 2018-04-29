#!/usr/bin/bash
timestamp=`TZ=GMT-8;date "+%Y%m%d_%H%M%S"`
debugOrRelease="debug"
author="peng"

apkPath="/d/server"
appName="zscf.apk"
# appName="qualityMarket.apk"

svnapkPath="/g/HttpClient"
newAppName=${appName%.*}_${timestamp}_${debugOrRelease}_${author}.apk

cp -rf $apkPath/$appName $svnapkPath/$newAppName

cd $svnapkPath
svn add . --force 
svn ci . -m "submit apk file"
svn up


