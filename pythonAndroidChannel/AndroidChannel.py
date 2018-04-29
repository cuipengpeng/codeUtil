#coding: utf-8 

import shutil,os
import zipfile

apkFileName = "app-debug.apk"
blankFile = "channelBlankFile"

with open('channelList', 'w') as f:
    f.write('')
with open('channelList', 'a+') as f:
    for i in range(1,10):
        f.write('test'+str(i)+'\n')

with open('channelList', 'r') as f:
    line = f.readline()               
    while line:  
        channelName = line.strip()
        print (channelName)
        filename = apkFileName[0:apkFileName.rindex('.')]
        newFileName = filename+"-"+channelName+".apk"
        print (newFileName)
        shutil.copy(apkFileName,newFileName)
        
        zipped = zipfile.ZipFile(newFileName, 'a', zipfile.ZIP_DEFLATED) 
        empty_channel_file = "META-INF/channel_{channel}".format(channel=channelName)
        zipped.write(blankFile, empty_channel_file)
        zipped.close()
        line = f.readline()  



