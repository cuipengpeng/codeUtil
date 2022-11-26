# -*- coding: utf-8 -*-

import os
import sys
import platform

def printLog(log=None):
    #当前文件名，可以通过__file__获得：print(__file__)
    fullPath=sys._getframe().f_code.co_filename
    fileName=fullPath.split(os.sep)[-1]
    #当前函数名
    methodName=sys._getframe(0).f_code.co_name
    #调用该函数的函数的名字，如果没有被调用，则返回<module>，貌似call stack的栈低
    parentMethodName=sys._getframe(1).f_code.co_name
    #当前行号
    line=sys._getframe().f_lineno
    print("########    "+fileName+"."+methodName+"("+str(line)+"):   "+str(log))
        


printLog(123)












