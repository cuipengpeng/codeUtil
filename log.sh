#!/usr/bin/bash

pid=`adb shell ps | grep com.jf.jlfund | awk  '{print $2}'`


adb logcat | grep $pid




