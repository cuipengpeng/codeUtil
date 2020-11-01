#!/usr/bin/bash

pid=`adb shell ps | grep com.jf.jlfund | grep -v ":" | awk  '{print $2}'`

adb logcat | grep -a $pid





#logso.sh
#adb logcat | $NDK_PATH/ndk-stack -sym $PROJECT_PATH

#adb logcat | $NDK_PATH/ndk-stack -sym $PROJECT_PATH/obj/local/armeabi-v7a

#adb logcat > /tmp/foo.txt
#$NDK/ndk-stack -sym $PROJECT_PATH/obj/local/armeabi-v7a -dump foo.txt





