#!/bin/bash
export GRADLE_USER_HOME="$HOME/.gradle/"

buildNumber="$1"
androidStudioProjectDir="/home/peng/project/QualityMarket_1.2.5"

cd $androidStudioProjectDir
svn up
svn up
cd -

syncMode="--offline"
echo "$*" | grep -q "online"
if [ $? -eq 0 ];then
	syncMode=""
fi

buildMode=""
echo "$*" | grep -q "clean"
if [ $? -eq 0 ];then
	buildMode="clean"
fi

#--max-workers 8 
#gradle uninstallDebug -b "$androidStudioProjectDir/build.gradle" -c "$androidStudioProjectDir/settings.gradle"

#time gradle $buildMode assembleDebug installDebug -b "$androidStudioProjectDir/build.gradle" -c "$androidStudioProjectDir/settings.gradle" --daemon --parallel "$syncMode" -Dorg.gradle.jvmargs="-Xmx2048m -Xms512m -XX:MaxPermSize=1024m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8"

time gradle $buildMode assembleDebug -b "$androidStudioProjectDir/build.gradle" -c "$androidStudioProjectDir/settings.gradle" --daemon --parallel "$syncMode" -Dorg.gradle.jvmargs="-Xmx2048m -Xms512m -XX:MaxPermSize=1024m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8"


#adb shell am start -n com.jfbank.qualitymarket/com.jfbank.qualitymarket.activity.WelcomeActivity
adb shell am start -n com.jfbank.qualitymarket/com.jfbank.qualitymarket.activity.WelcomeActivity

bash $HOME/submitapkfile.sh $buildNumber
