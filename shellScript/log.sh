#!/usr/bin/bash

adb logcat -G 50M
adb logcat -c
adb logcat -g
#adb bugreport > bugreport.zip

pid=`adb shell ps | grep "com.aaa.abc" | grep -v ":" | awk  '{print $2}'`
#adb logcat | grep -10nE "###|Exception"

if [ -n "$1" ];then
    echo "###"
    adb logcat | grep -E "###|FragmentBottomAction"
else
    echo "--"
    adb logcat | grep -a $pid | grep -v "CameraMetadataJV"
fi





