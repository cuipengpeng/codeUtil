# vim ~/.muttrc 
set envelope_from=yes
set from=owinux@sina.cn
set realname="Owinux"
set use_from=yes
set rfc2047_parameters=yes
set charset="utf-8"

#automail.sh
cat content.txt | /usr/bin/mutt -a test.txt -s "title" -- admin@test.com -c cc@126.com -c cc2@126.com
      正文                附件         主题          收件人 		抄送人
	  
#--  指定收件人
#-c：抄送，需要抄送多人的话，须使用多个 -c 选项	
#-s：指定主题
#-a：附件  

#http://blog.163.com/a12333a_li/blog/static/87594285201212042332551/
#msmtp+mutt+shell来实现

#http://blog.csdn.net/findstr/article/details/7309880

#http://www.ibm.com/developerworks/cn/linux/l-cn-screen/
#http://www.cnblogs.com/mchina/archive/2013/01/30/2880680.html