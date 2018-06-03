package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
    /**
     * 判断是否为手机号
     *
     * @param tel 判断的字符串
     * @return 是为手机号:true  不是手机号:false
     */
    public static boolean isPhoneNum(String tel) {
        if ("".equals(tel) || tel == null) {
            return false;
        }
        return tel.startsWith("1") && tel.length() == 11;       //前端不在对手机号码有效段号进行拦截限制【只要1开头，11位就行】2018.3.1
//        return tel.matches("^[1][3-9]\\d{9}$");
//        return isPhoneLegal(tel);
    }

    /**
     * 大陆号码或香港号码均可
     */
    public static boolean isPhoneLegal(String str) {
        return isChinaPhoneLegal(str) || isHKPhoneLegal(str);
    }

    /**
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 15+除4的任意数
     * 18+除1和4的任意数
     * 17+除9的任意数
     * 147
     */
    public static boolean isChinaPhoneLegal(String str) {
        return str.matches("^[1][3-9]\\d{9}$");

//        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
//        Pattern p = Pattern.compile(regExp);
//        Matcher m = p.matcher(str);
//        return m.matches();
    }

    /**
     * 香港手机号码8位数，5|6|8|9开头+7位任意数
     */
    public static boolean isHKPhoneLegal(String str) {
        String regExp = "^(5|6|8|9)\\d{7}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean isIncludeSpecialChars(String password) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
//        String limitEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@①#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(password);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 校验密码合法性
     *
     * @param password
     * @return
     */
    public static boolean isPasswordValid(String password) {
        return Pattern.compile("^[a-zA-Z0-9]{6,16}$").matcher(password).matches();
    }


    /**
     * 隐藏手机号中间四位数
     *
     * @param phone
     * @return
     */
    public static String hidePhoneNumber(String phone) {
        if (isPhoneNum(phone)) {
            // 隐藏中间四位
            return new StringBuilder().append(phone.substring(0, 3)).append("****").append(phone.substring(phone.length() - 4)).toString();
        }
        return "";
    }
}


