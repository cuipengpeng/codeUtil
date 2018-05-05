#coding: utf-8 
import shutil,os
import zipfile

channelFileName='channel.txt'

#创建一个空文件（不存在则创建）
blankFile = "BlankFile"
with open(blankFile, 'w') as f:
    f.close()

#clear channel content
with open(channelFileName, 'w') as f:
    f.write('')
    
with open(channelFileName, 'a+') as f:
    for i in range(1,5):
        f.write('test'+str(i)+'\n')

# 获取当前目录中所有的apk源包
apkList = []
# python3 : os.listdir()即可，这里使用兼容Python2的os.listdir('.')
for file in os.listdir('.'):
    if os.path.isfile(file):
        extension = os.path.splitext(file)[1][1:]
        if extension == 'apk':
            apkList.append(file)

for apkFileName in apkList:
    print (apkFileName+"====")
    with open(channelFileName, 'r') as f:
        line = f.readline()               
        while line:  
            channelName = line.strip()
            filename = apkFileName[0:apkFileName.rindex('.')]
            newFileName = filename+"-"+channelName+".apk"
            print (newFileName)
            shutil.copy(apkFileName,newFileName)
            
            zipped = zipfile.ZipFile(newFileName, 'a', zipfile.ZIP_DEFLATED) 
            empty_channel_file = "META-INF/channel_{channel}".format(channel=channelName)
            zipped.write(blankFile, empty_channel_file)
            zipped.close()
            line = f.readline()  
