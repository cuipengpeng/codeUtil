package com.jf.jlfund.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.jf.jlfund.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 55 on 2017/12/19.
 */

public class StringUtil {


    public static String transferTimeStampToDate(String timeStamp, String pattern) {
        if (TextUtils.isEmpty(timeStamp)) {
            return "--";
        }
        String date = "--";
        try {
            long t = Long.parseLong(timeStamp);
            date = transferTimeStampToDate(t, pattern);
        } catch (Exception e) {
            LogUtils.e("transferTimeStampToDate: " + e.getMessage());
            date = "--";
        }
        return date;
    }

    public static String transferTimeStampToDate(Long timeStamp) {
        return transferTimeStampToDate(timeStamp, "yyyy-MM-dd HH:mm:ss");
    }

    public static String transferTimeStampToDate(Long timeStamp, String pattern) {
        if (timeStamp == null) {
            return "xx-xx";
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(timeStamp);
    }

    public static boolean notEmpty(String string) {
        boolean notEmpty = false;
        if (!"".equals(string) && string != null && !"null".equals(string)) {
            notEmpty = true;
        }
        return notEmpty;
    }

    public static boolean isNull(String string) {
        boolean isNull = false;
        if ("".equals(string) || string == null || "null".equals(string)) {
            isNull = true;
        }
        return isNull;
    }

    public static String replaceBlankStr(String string) {
        String emptyStr = string;
        if (isNull(string)) {
            emptyStr = ConstantsUtil.REPLACE_BLANK_SYMBOL;
        }
        return emptyStr;
    }

    public static double doubleValue(String money) {
        double moneyDouble = 0;
        if (StringUtil.notEmpty(money)) {
            moneyDouble = Double.valueOf(money);
        }

        return moneyDouble;
    }

    public static long longValue(String money) {
        long moneyLong = 0;
        if (StringUtil.notEmpty(money)) {
            moneyLong = Long.valueOf(money);
        }

        return moneyLong;
    }

    public static int intValue(String money) {
        int moneyInt = 0;
        if (StringUtil.notEmpty(money)) {
            moneyInt = Integer.valueOf(money);
        }

        return moneyInt;
    }

    public static String moneyDecimalFormat2(String money) {
        if (notEmpty(money)) {
            BigDecimal b = new BigDecimal(money);
            return b.setScale(2, BigDecimal.ROUND_HALF_UP) + "";
        }
        return ConstantsUtil.REPLACE_BLANK_SYMBOL;
    }

    public static String moneyDecimalFormat2(String money, boolean returnZeroStrAsDefault) {
        if (notEmpty(money)) {
            BigDecimal b = new BigDecimal(money);
            return b.setScale(2, BigDecimal.ROUND_HALF_UP) + "";
        }

        if (returnZeroStrAsDefault) {
            return "0";
        } else {
            return ConstantsUtil.REPLACE_BLANK_SYMBOL;
        }
    }

    public static String moneyDecimalFormat4(String money) {
        if (notEmpty(money)) {
            BigDecimal b = new BigDecimal(money);
            return b.setScale(4, BigDecimal.ROUND_HALF_UP) + "";
//            return new DecimalFormat("#0.0000").format(b.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        return ConstantsUtil.REPLACE_BLANK_SYMBOL;
    }

    public static String setMoneyTextView(Context context, String money, TextView textView) {
        money = StringUtil.moneyDecimalFormat2(money, true);
        if (Float.valueOf(money) > 0) {
            textView.setText("+" + money);
            textView.setTextColor(context.getResources().getColor(R.color.appRedColor));
        } else if (Float.valueOf(money) < 0) {
            textView.setText(money);
            textView.setTextColor(context.getResources().getColor(R.color.appNegativeRateColor));
        } else {
            textView.setText(money);
            textView.setTextColor(context.getResources().getColor(R.color.appContentColor));
        }
        return money;
    }


    /**
     * 判断是否为手机号
     *
     * @param tel 判断的字符串
     * @return 是为手机号:true  不是手机号:false
     */
    public static boolean isPhoneNum(String tel) {
        if (TextUtils.isEmpty(tel)) {
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


    public static String checkInputMoney(String str, EditText editText) {
        if (StringUtil.notEmpty(str)) {
            //首位不能是小数点.
            if (".".equals(str.substring(0, 1))) {
                str = "";
                inputMoneyText(str, editText);
            } else if (str.length() > 1 && "0".equals(str.substring(0, 1)) && !".".equals(str.substring(1, 2))) {
                //第一位是0，则第二位必须是小数点.
                str = str.substring(0, 1);
                inputMoneyText(str, editText);
            }

            //小数点.最多输入2位
            if (str.indexOf(".") != -1 && str.length() > (str.indexOf(".") + 1 + 2)) {
                str = str.substring(0, (str.indexOf(".") + 1 + 2));
                inputMoneyText(str, editText);
            }

            //只能输入一个小数点.
            if (str.indexOf(".") != str.lastIndexOf(".")) {
                str = str.substring(0, (str.lastIndexOf(".")));
                inputMoneyText(str, editText);
            }
        }
        return str;
    }

    private static void inputMoneyText(String str, EditText editText) {
        editText.setText(str);
        editText.setSelection(str.length());
    }


    public static String[] getCurrentPlusYAxisLabel(double realMinVale, double realMaxValue) {
        String[] yLabels = new String[5];
        double dy = realMaxValue - realMinVale;
        double x = dy * 5 / 38;
        double showMaxLabel = realMaxValue + x;
        double showMinLabel = realMinVale - x;
        yLabels[0] = moneyDecimalFormat4(showMaxLabel + "");
        yLabels[4] = moneyDecimalFormat4(showMinLabel + "");
        double offset = (showMaxLabel - showMinLabel) / 4;
        LogUtils.e("dy: " + dy + "  x: " + x + " realMax: " + realMaxValue + " realMin: " + realMinVale + " showMax: " + showMaxLabel + " showMin: " + showMinLabel + "  offset: " + offset);
        yLabels[1] = moneyDecimalFormat4((showMaxLabel - offset) + "");
        yLabels[2] = moneyDecimalFormat4((showMaxLabel - offset - offset) + "");
        yLabels[3] = moneyDecimalFormat4((showMinLabel + offset) + "");
        return yLabels;
    }

    public static double trunkDouble(double f) {
        if (Double.isNaN(f) || Double.isInfinite(f)) {
            return Double.NaN;
        }
        return trunkDouble(f, 2);
    }

    public static double trunkDouble(double f, int newScale) {
        BigDecimal b = new BigDecimal(f);
        double d = b.setScale(newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
        return d;
    }

    //不足两位补0
    public static String trunkDouble(String amount) {
        String result = "";
        if (TextUtils.isEmpty(amount)) {
            result = "0.00";
            return result;
        }
        try {
            Double d = Double.parseDouble(amount);
            if (Double.isInfinite(d) || Double.isNaN(d)) {
                return "0.00";
            }
            d = trunkDouble(d, 2);
            if (d == 0) {
                return "0.00";
            }
            result = d + "";
            if (result.length() == 3 && result.charAt(1) == '.') {
                result = result + "0";
            }
        } catch (Exception e) {
            result = "0.00";
        }
        return result;
    }

    public static String[] getYAxisLabel(double realMinVale, double realMaxValue) {
        String[] yLabels = new String[5];
        double dy = realMaxValue - realMinVale;
        double x = dy * 5 / 38;
        double showMaxLabel = realMaxValue + x;
        double showMinLabel = realMinVale - x;
        yLabels[0] = getRateWithSign(trunkDouble(showMaxLabel));
        yLabels[4] = getRateWithSign(trunkDouble(showMinLabel));
        double offset = (showMaxLabel - showMinLabel) / 4;
        LogUtils.e("dy: " + dy + "  x: " + x + " realMax: " + realMaxValue + " realMin: " + realMinVale + " showMax: " + showMaxLabel + " showMin: " + showMinLabel + "  offset: " + offset);
        yLabels[1] = getRateWithSign(trunkDouble(showMaxLabel - offset));
        yLabels[2] = getRateWithSign(trunkDouble(showMaxLabel - offset - offset));
        yLabels[3] = getRateWithSign(trunkDouble(showMinLabel + offset));
        return yLabels;
    }

    /**
     * 涨幅加一个正负号
     *
     * @param rate
     * @return
     */
    public static String getRateWithSign(String rate) {
        if (!TextUtils.isEmpty(rate)) {
            return getRateWithSign(Float.parseFloat(rate));
        }
        return "--";
    }

    /**
     * 在float前边加上正负号，小数点不足两位补0
     *
     * @param f
     * @return
     */
    public static String getRateWithSign(double f) {
        return getRateWithSign(f, true);
    }

    public static String getRateWithSign(double f, boolean isTrunk) {
        if (f == Double.NaN) {
            return "--";
        }
        if (isTrunk)
            f = trunkDouble(f);
        String rate = f + "";
        char[] r = rate.toCharArray();
        int length = r.length;
        if (r[length - 2] == '.') {
            rate = rate + "0%";
        } else {
            rate = rate + "%";
        }
        return f > 0 ? "+" + rate : rate;
    }

    public static boolean isNormalFund(String fundStatus) {
        String normalFundStatus = "12367";
        if (!TextUtils.isEmpty(fundStatus)) {
            return normalFundStatus.contains(fundStatus);
        }
        return true;
    }

    public static boolean isWeakPassword(String password) {
        String[] weakPwds = {
                "000000", "111111", "222222", "333333", "444444", "555555", "666666", "777777", "888888", "999999",
                "012345", "123456", "234567", "345678", "456789", "987654", "876543", "765432", "654321", "543210"
        };
        boolean result = false;
        for (int i = 0; i < weakPwds.length; i++) {
            if (weakPwds[i].equals(password)) {
                result = true;
            }
        }
        return result;
    }


    public static double parseDouble(String number) {
        return parseDouble(number, -1);
    }

    public static double parseDouble(String number, double defaultValue) {
        if (TextUtils.isEmpty(number)) {
            return defaultValue;
        }
        double result = defaultValue;
        try {
            result = Double.parseDouble(number);
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                result = defaultValue;
            }
        } catch (Exception e) {
            result = defaultValue;
        }
        return result;
    }


    public static BigDecimal transferToBigDecimal(String number) {
        return transferToBigDecimal(number, "0");
    }

    public static BigDecimal transferToBigDecimal(String number, String defaultValue) {
        BigDecimal bigDecimal = new BigDecimal(defaultValue);
        if (TextUtils.isEmpty(number)) {
            return bigDecimal;
        }
        try {
            bigDecimal = new BigDecimal(number);
        } catch (Exception e) {
            LogUtils.e("transferToBigDecimal: " + e.getMessage());
        }
        return bigDecimal;
    }

    /**
     * 格式化 银行卡
     * 加空格
     *
     * @param num
     * @return
     */
    public static String formatBankCardNum(String num) {
        if (!TextUtils.isEmpty(num)) {
            return num.replaceAll("\\d{4}(?!$)", "$0 ");
        }
        return "";
    }

    /**
     * 将金额以美元格式显示
     * 123456.78---> 123,456.78
     *
     * @param money
     * @return
     */
    public static String transferToDollar(String money) {
        if (TextUtils.isEmpty(money)) {
            return "--";
        }
        String result = "";
        BigDecimal value = new BigDecimal(money);
        BigDecimal flagDecimal = new BigDecimal("10000");
        boolean isLessThanFag = value.compareTo(flagDecimal) == -1; //小于10000
        String pi = "";     //整数部分
        String pf = "";     //小数部分
        try {
            if (!isLessThanFag) {
                value = value.divide(flagDecimal);
            }

            if (value.toPlainString().contains(".")) {
                String[] arr = splitValue(value.toString());
                pi = arr[0];
                pf = arr[1];
            } else {
                pi = value + "";
            }

            pi = formatDollar(Long.parseLong(pi));

            result = pi;

            if (!pf.equals("")) {
                result = result + "." + pf;
            }

            if (!isLessThanFag) {
                result = result + "万";
            }

        } catch (Exception e) {
            LogUtils.e(e.getMessage());
            result = "--";
        }
        return result;
    }

    private static String formatDollar(long value) {
        DecimalFormat decimalFormat = new DecimalFormat(",###");
        String result = decimalFormat.format(value);
        return result;
    }

    private static String[] splitValue(String money) {
        String[] arr = money.split("\\.");
        if (arr[1].length() >= 2 && !arr[1].startsWith("00")) {
            arr[1] = arr[1].substring(0, 2);
        } else if (arr[1].length() == 1 && !arr[1].startsWith("0")) {
            arr[1] = arr[1];
        } else {
            arr[1] = "";
        }
        return arr;
    }

}
