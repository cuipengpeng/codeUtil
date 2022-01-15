#!/usr/bin/bash

adb devices
adb root
adb disable-verity
adb remount
adb shell settings put secure user_setup_complete 1 
adb shell settings put global device_provisioned 1 
adb reboot

