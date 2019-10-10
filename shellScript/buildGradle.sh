#!/bin/bash
export GRADLE_USER_HOME="$HOME/.gradle/"

buildNumber="$1"
androidStudioProjectDir="/home/dsafdsa/project"


while getopts ":p:bc:" opt
do
    case $opt in
    p )
    androidStudioProjectDir="$OPTARG";;
    esac
done


echo $androidStudioProjectDir
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
        echo "clean---------"
	buildMode="clean"
else
        echo "not clean-------------"
fi

assembleMode="assembleDebug"
installMode="installDebug" 
echo "$*" | grep -q "release"
if [ $? -eq 0 ];then
    assembleMode="assembleRelease"
    #installMode="installRelease"
	installMode=""
fi

#--max-workers 8 
#gradle uninstallDebug -b "$androidStudioProjectDir/build.gradle" -c "$androidStudioProjectDir/settings.gradle"

time gradle $buildMode $assembleMode $installMode -b "$androidStudioProjectDir/build.gradle" -c "$androidStudioProjectDir/settings.gradle" --daemon --parallel $syncMode -Dorg.gradle.jvmargs="-Xmx2048m -Xms512m -XX:MaxPermSize=1024m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8" --continue --info --stacktrace --scan 

adb shell am start -n com.jfbank.qualitymarket/com.jfbank.qualitymarket.activity.WelcomeActivity

#bash $HOME/submitapkfile.sh $buildNumber


