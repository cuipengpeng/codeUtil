#!/bin/bash
export TZ=GMT-8
buildNumber="$1"
timestamp=`date "+%Y%m%d_%H%M%S"`
#timestamp=`TZ=GMT-8;date "+%Y%m%d_%H%M%S"`
author="test"
apkPath="/home/peng/project/QualityMarket_1.2.5/qualityMarket/build/outputs/apk"
appName="qualityMarket-debug.apk"


debugOrRelease="debug"
echo $* | grep -q "online"
if [ $? -eq 0 ] ;then 
    debugOrRelease=online
fi


svnapkPath="/home/peng/project/apk/debug"

#newAppName=${appName%.*}_${timestamp}_${debugOrRelease}_${author}.apk
newAppName="_${buildNumber}_QualityMarket_${timestamp}_${debugOrRelease}_${author}.apk"
echo "$*" | grep -q "sign"
if [ $? -eq 0 ];then
    newAppName="_${buildNumber}_QualityMarket_${timestamp}_${debugOrRelease}_${author}_signed.apk"
fi

cp -rf $apkPath/$appName $svnapkPath/$newAppName

cd $svnapkPath
svn add . --force
svn ci . -m "submit apk file"
svn up

