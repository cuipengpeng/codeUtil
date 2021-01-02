#!/usr/bin/bash

adb logcat -G 30M
adb logcat -c
adb logcat -g

pid=`adb shell ps | grep com.aaa.abc | grep -v ":" | awk  '{print $2}'`

adb logcat | grep -a $pid





