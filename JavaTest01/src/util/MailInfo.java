package util;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * 邮件类，用于构造要发送的邮件。
 */
public class MailInfo extends Authenticator {
    private String host;
    private String port;
    private String user;
    private String pass;
    private String from;
    private String to;
    private String subject;
    private String body;

    private Multipart multipart;
    private Properties props;

    public final static SimpleDateFormat yyyy_MM_dd_HH_mm_ss_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS", Locale.getDefault());
    
    public MailInfo() {
    }

    /**
     * @param user    邮箱登录名
     * @param pass    邮箱登录密码
     * @param from    发件人
     * @param to      收件人
     * @param host    主机
     * @param port    SMTP端口号
     * @param subject 邮件主题
     * @param body    邮件正文
     */
    public MailInfo(String user, String pass, String from, String to, String host, String port, String subject, String body) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
    }
    
    private UncaughtExceptionHandler uncaught = new UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
//			Thread.setDefaultUncaughtExceptionHandler(uncaught);
			ex.printStackTrace();
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			ex.printStackTrace(printWriter);
			Throwable cause = ex.getCause();
			while (cause != null) {
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
			printWriter.close();
			String crashInfo = writer.toString();
		}
    };	
    
    public static void main(String[] args) {
		MailInfo sender = new MailInfo()
						.setUser("qualitymarket5566@163.com")
						.setPass("Zef6tQ8fY")
//						.setPass(AESUtil.decrypt(new ConstantsUtil().sayHello(), "12345678"))
						.setFrom("qualitymarket5566@163.com")
						.setTo("peng2011@126.com")
						.setHost("smtp.163.com") //smtp.qq.com
						.setPort("465")
						.setSubject("【CrashLog】  " + yyyy_MM_dd_HH_mm_ss_SS.format(Calendar.getInstance().getTime()))
//						.setSubject("有关今年高考的几个新动态！个个都值得你了解！")
						.setBody("4月10日，教育部办公厅发布《关于2018年普通高等学校招生全国统一考试全国统考科目 时间安排的通知》。考试时间安排为：6月7日9:00 至11:30 语文; 15:00至17:00 数学6月8日9:00 至11:30 文科综合/理科综合; 15:00至17:00 外语(有外语听力测试内容的应安排在外语笔试考试开始前进行)");
    	
		sender.init();
		sendCrashMail(sender);
		//此处非常关键 如果处理了，让主程序继续运行3秒再退出，保证异步的写操作能及时完成
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
    
	private static void sendCrashMail(final MailInfo sender) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					sender.send();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}).start();
}
    
    public MailInfo setHost(String host) {
        this.host = host;
        return this;
    }

    public MailInfo setPort(String port) {
        this.port = port;
        return this;
    }

    public MailInfo setUser(String user) {
        this.user = user;
        return this;
    }

    public MailInfo setPass(String pass) {
        this.pass = pass;
        return this;
    }

    public MailInfo setFrom(String from) {
        this.from = from;
        return this;
    }

    public MailInfo setTo(String to) {
        this.to = to;
        return this;
    }

    public MailInfo setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public MailInfo setBody(String body) {
        this.body = body;
        return this;
    }

    /**
     * 初始化。它在设置好用户名、密码、发件人、收件人、主题、正文、主机及端口号之后显示调用。
     */
    public void init() {
        multipart = new MimeMultipart();
        //发送附件时有时候会报java-mail的Error, eg:javax.activation.UnsupportedDataTypeException: no object DCH for MIME type multipart/related;所以务必添加以下几行代码来确定DCH
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
        props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
    }

    /**
     * 发送邮件
     *
     * @return 是否发送成功
     * @throws MessagingException
     */
    public boolean send() throws MessagingException {
        if (!user.equals("") && !pass.equals("") && !to.equals("") && !from.equals("")) {
            Session session = Session.getDefaultInstance(props, this);
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            InternetAddress addressTo = new InternetAddress(to);
            msg.setRecipient(MimeMessage.RecipientType.TO, addressTo);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            // setup message body
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            multipart.addBodyPart(messageBodyPart, 0);
            // Put parts in message
            msg.setContent(multipart);
            // send email
            Transport.send(msg);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 添加附件
     *
     * @param filePath 附件路径
     * @param fileName 附件名称
     * @throws Exception
     */
    public void addAttachment(String filePath, String fileName) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        ((MimeBodyPart) messageBodyPart).attachFile(filePath);
        multipart.addBodyPart(messageBodyPart);
    }

    @Override
    public javax.mail.PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, pass);
    }
}