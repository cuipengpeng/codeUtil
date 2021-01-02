#!/usr/bin/bash

PROJECT_PATH="/d/projectPath"
adb logcat | $NDK_PATH/ndk-stack -sym $PROJECT_PATH
#adb logcat | $NDK_PATH/ndk-stack -sym $PROJECT_PATH/obj/local/armeabi-v7a

#adb logcat > /tmp/foo.txt
#$NDK/ndk-stack -sym $PROJECT_PATH/obj/local/armeabi-v7a -dump foo.txt





