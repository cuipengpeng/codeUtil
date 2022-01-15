#coding: utf-8    
  
import smtplib    
from email.mime.multipart import MIMEMultipart    
from email.mime.text import MIMEText    
from email.mime.image import MIMEImage 
from email.header import Header   
    
#设置smtplib所需的参数
#下面的发件人，收件人是用于邮件传输的。
smtpserver = 'xxx.com'
username = 'xxx@abc.com'
password='xxx'

sender='xxx@abc.com'
#收件人为多个收件人
receiver=['xx@abc.com','xxx@abc.com']
#receiver='XXX@abc.com'
subject = '最新版本发布'
#通过Header对象编码的文本，包含utf-8编码信息和Base64编码信息。以下中文名测试ok
#subject = '中文标题'
#subject=Header(subject, 'utf-8').encode()
    
#构造邮件对象MIMEMultipart对象
#下面的主题，发件人，收件人，日期是显示在邮件页面上的。
msg = MIMEMultipart('mixed') 
msg['Subject'] = subject
msg['From'] = sender
#收件人为多个收件人,通过join将列表转换为以;为间隔的字符串
msg['To'] = ";".join(receiver) 
#msg['To'] = 'XXX@abc.com'
#msg['Date']='2012-3-16' #不添加该参数，默认用当前最新时间

#构造文字内容   
text = "Hi!\n最新版本发布\n最新版本发布，自己搜索一下吧\nhttp://www.baidu.com"    
text_plain = MIMEText(text,'plain', 'utf-8')    
msg.attach(text_plain)    

#构造图片链接
sendimagefile=open(r'D:/5.jpg','rb').read()
image = MIMEImage(sendimagefile)
image.add_header('Content-ID','<image1>')
image["Content-Disposition"] = 'attachment; filename="headimg.jpg"'
msg.attach(image)

#构造html
#发送正文中的图片:由于包含未被许可的信息，网易邮箱定义为垃圾邮件，报554 DT:SPM ：<p><img src="cid:image1"></p>
html = """
<html>  
  <head></head>  
  <body>  
    <p>Hi!<br>  
       网页格式请看这里：<br>  
       最新版本发布 <a href="http://www.baidu.com">link</a> 自己搜索一下吧.<br> 
    </p> 
  </body>  
</html>  
"""    
text_html = MIMEText(html,'html', 'utf-8')
text_html["Content-Disposition"] = 'attachment; filename="texthtml.html"'   
msg.attach(text_html)    


#构造附件
sendfile=open(r'D:/1.txt','rb').read()
text_att = MIMEText(sendfile, 'base64', 'utf-8') 
text_att["Content-Type"] = 'application/octet-stream'  
#以下附件可以重命名成aaa.txt  
#text_att["Content-Disposition"] = 'attachment; filename="aaa.txt"'
#另一种实现方式
text_att.add_header('Content-Disposition', 'attachment', filename='1.txt')
#以下中文测试不ok
#text_att["Content-Disposition"] = u'attachment; filename="中文附件.txt"'.decode('utf-8')
msg.attach(text_att)    
       
#发送邮件
smtp = smtplib.SMTP()    
smtp.connect(smtpserver)
#一些邮箱登录比如 QQ 邮箱需要 SSL 认证，所以 SMTP 已经不能满足要求，而需要SMTP_SSL，解决办法为：用SMTP_SSL(smtpserver)替换connect(smtpserver)
# smtp = smtplib.SMTP_SSL(smtpserver)
#我们用set_debuglevel(1)就可以打印出和SMTP服务器交互的所有信息。
#smtp.set_debuglevel(1)  
smtp.login(username, password)    
smtp.sendmail(sender, receiver, msg.as_string())    
smtp.quit()






