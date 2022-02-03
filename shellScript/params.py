#!/c/Python37/python
# -*-coding:utf-8 -*-
import sys

print(sys.argv[0])          #sys.argv[0] 类似于shell中的$0,但不是脚本名称，而是脚本的路径   
print(sys.argv[1])          #sys.argv[1] 表示传入的第一个参数

print(sys.argv)
name = ''
if len(sys.argv) >= 2:
    name = sys.argv[1]
 
print("name=%s" % name)

