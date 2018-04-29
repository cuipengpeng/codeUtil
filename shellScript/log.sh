#!/usr/bin/bash

pid=`adb shell ps | grep com.jf.jlfund | grep -v ":" | awk  '{print $2}'`


adb logcat | grep $pid




