package com.jf.jlfund.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.base.BaseActivity;
import com.jf.jlfund.base.BaseBean;
import com.jf.jlfund.base.BaseBusiness;
import com.jf.jlfund.bean.SmsCheckCodeBean;
import com.jf.jlfund.http.NetService;
import com.jf.jlfund.http.ParamMap;
import com.jf.jlfund.inter.OnResponseListener;
import com.jf.jlfund.weight.dialog.CommonDialogFragment;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;

/**
 * Created by 55 on 2017/12/13.
 */

public class UIUtils {
    /**
     * 通过改变App的系统字体替换App内部所有控件的字体(TextView,Button,EditText,CheckBox,RadioButton等)
     *
     * @param context
     * @param fontPath 需要修改style样式为monospace：
     */
//    <style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
//    <!-- Customize your theme here. -->
//    <!-- Set system default typeface -->
//    <item name="android:typeface">monospace</item>
//    </style>
    public static void replaceSystemDefaultFont(@NonNull Context context, @NonNull String fontPath) {
        replaceTypefaceField("MONOSPACE", createTypeface(context, fontPath));
    }

    /**
     * <p>Replace field in class Typeface with reflection.</p>
     */
    private static void replaceTypefaceField(String fieldName, Object value) {
        Log.e("zzzzz", "replaceTypefaceField: " + fieldName + " value: " + value.toString());
        try {
            Field defaultField = Typeface.class.getDeclaredField(fieldName);
            defaultField.setAccessible(true);
            defaultField.set(null, value);
        } catch (Exception e) {
            Log.e("zzzzz", "error: " + e.toString());
            e.printStackTrace();
        }
    }

    /*
  * Create a Typeface instance with your font file
  */
    public static Typeface createTypeface(Context context, String fontPath) {
        return Typeface.createFromAsset(context.getAssets(), fontPath);
    }

    public static void setText(TextView textView, String content) {
        setText(textView, content, "");
    }

    public static void setText(TextView textView, CharSequence content, String replaceTxtWhileNull) {
        if (textView == null) {
            return;
        }
        textView.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(content)) {
            textView.setText(replaceTxtWhileNull);
        } else {
            textView.setText(content);
        }
    }

    public static void setRate(TextView textView, String rate) {
        setRate(textView, rate, true);
    }

    public static void setRate(TextView textView, String rate, boolean isTrunk) {
        if (textView == null) {
            return;
        }
        if (TextUtils.isEmpty(rate) || "null".equals(rate)) {
            textView.setText("--");
            textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.color_b9bbca));
            return;
        }
        if ("0.00".equals(rate) || "0.0".equals(rate)) {
            textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.color_b9bbca));
            textView.setText("0.00%");
            return;
        }
        try {
            double profit = Double.parseDouble(rate);
            setRate(textView, profit, isTrunk);
        } catch (Exception e) {
            LogUtils.e("UIUtils.setRate: " + e.toString());
            textView.setText("--");
            textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.color_b9bbca));
        }
    }

    public static void setRate(TextView textView, double profit) {
        setRate(textView, profit, true);
    }

    public static void setRate(TextView textView, double profit, boolean isTrunk) {
        if (textView == null || Double.isNaN(profit) && textView.getContext() != null) {
            textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.color_b9bbca));
            textView.setText("--");
            return;
        }
//        if (profit == 0) {
//            textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.color_b9bbca));
//            textView.setQuestionText("0.00%");
//            return;
//        }
        if (profit > 0) {
            textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.color_f35857));
        } else if (profit < 0) {
            textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.color_18b293));
        } else {
            textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.color_b9bbca));
        }
        if (isTrunk)
            profit = StringUtil.trunkDouble(profit);
        //不足两位补0
        String rate = profit + "";
        char[] r = rate.toCharArray();
        int length = r.length;
        if (r[length - 2] == '.') {
            rate = rate + "0%";
        } else {
            rate = rate + "%";
        }
        if (profit > 0) {
            rate = "+" + rate;
        }
        textView.setText(rate);
    }

    public static void setIncome(TextView textView, String amount) {
        if (textView == null) {
            return;
        }
        setIncome(textView, amount, "--");
    }

    public static void setIncome(TextView textView, String amount, String replaceTxt) {
        if (TextUtils.isEmpty(amount)) {
            setGrayText(textView, replaceTxt);
            return;
        }
        try {
            Double d = Double.parseDouble(amount);
            if (Double.isNaN(d) || Double.isInfinite(d)) {
                setGrayText(textView, replaceTxt);
                return;
            }
            setIncome(textView, d, replaceTxt);
        } catch (Exception e) {
            setGrayText(textView, replaceTxt);
            LogUtils.e("UIUtils.setIncome: " + e.getMessage());
        }
    }

    //默认小数点两位，不足补0
    public static void setIncome(TextView textView, Double amount, String replaceTxt) {
        setIncome(textView, amount, 2, true, true);
    }

    /**
     * @param textView
     * @param amount            金额
     * @param numAfterPoint     小数点后保留的位数【不足补0】
     * @param isAddSign         大于0时是否在前边添加正号 +
     * @param isChangeTextColor 是否改变字体颜色【正红负绿0灰】
     */
    public static void setIncome(TextView textView, String amount, String replaceTxt, int numAfterPoint, boolean isAddSign, boolean isChangeTextColor) {
        if (TextUtils.isEmpty(amount)) {
            if (isChangeTextColor)
                setGrayText(textView, replaceTxt);
            else {
                textView.setText(replaceTxt);
            }
            return;
        }
        try {
            Double d = Double.parseDouble(amount);
            if (Double.isNaN(d) || Double.isInfinite(d)) {
                if (isChangeTextColor)
                    setGrayText(textView, replaceTxt);
                else {
                    textView.setText(replaceTxt);
                }
                return;
            }
            setIncome(textView, d, numAfterPoint, isAddSign, isChangeTextColor);
        } catch (Exception e) {
            setGrayText(textView, replaceTxt);
            LogUtils.e("UIUtils.setIncome: " + e.getMessage());
        }
    }

    public static void setIncome(TextView textView, Double amount, int numAfterPoint, boolean isAddSign, boolean isChangeTextColor) {
        if (amount == 0) {
            if (isChangeTextColor) {
                setGrayText(textView, "0.00");
            } else {
                textView.setText("0.00");
            }
            return;
        }
        String sign = "";
        if (amount > 0) {
            sign = "+";
            if (isChangeTextColor)
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.color_f35857));
        } else {
            if (isChangeTextColor)
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.color_18b293));
        }
        String result = fillUpZeroInAmountEnd(amount, numAfterPoint);
        if (isAddSign) {
            result = sign + result;
        }
        textView.setText(result);
    }

    public static String fillUpZeroInAmountEnd(String amount, int numAfterPoint, String replaceTxt) {
        if (TextUtils.isEmpty(amount)) {
            return replaceTxt;
        }
        String result;
        try {
            double d = Double.parseDouble(amount);
            if (Double.isInfinite(d) || Double.isNaN(d)) {
                return replaceTxt;
            }
            result = fillUpZeroInAmountEnd(d, numAfterPoint);
        } catch (Exception e) {
            LogUtils.e("fillUpZeroInAmountEnd: " + e.toString());
            result = replaceTxt;
        }
        return result;
    }

    public static String fillUpZeroInAmountEnd(Double amount, int numAfterPoint) {
        String result = amount + "";
        if (result.contains(".")) {       //包含小数点
            int indexOfPoint = result.indexOf("."); //小数点下标
            int realNumAfterPoint = result.length() - indexOfPoint - 1;
            if (indexOfPoint < result.length() - numAfterPoint - 1) {   //小数点后超过指定位数
                result = trunkDoubleAndFillUpZero(result, numAfterPoint);        //小数点后保留几位小数
            } else if (indexOfPoint == result.length() - numAfterPoint - 1) {
                result = amount + "";
            } else {
                result = amount + getZeroAfterPoint(numAfterPoint - realNumAfterPoint);     //不足指定位数补0
            }
        } else {      //不包含小数点
            result = amount + ".00";
        }
        return result;
    }

    public static String trunkDoubleAndFillUpZero(String amount, int numAfterPoint) {
        BigDecimal b = new BigDecimal(amount);
        double d = b.setScale(numAfterPoint, BigDecimal.ROUND_HALF_UP).doubleValue();
        String result = d + "";
        System.out.println("result: " + result);
        if (result.contains(".")) {
            int indexOfPoint = result.indexOf("."); //小数点下标
            int realNumAfterPoint = result.length() - indexOfPoint - 1;
            System.out.println("真实小数点后： " + realNumAfterPoint + " , require: " + numAfterPoint);
            if (realNumAfterPoint > numAfterPoint) {
                System.out.println(">>>>? impossible...");
            } else if (realNumAfterPoint == numAfterPoint) {
                System.out.println("==. do noting...");
            } else if (realNumAfterPoint < numAfterPoint) {
                System.out.println("<<.fill");
                result = result + getZeroAfterPoint(numAfterPoint - realNumAfterPoint);
            }
        }
        return result;
    }

    public static String getZeroAfterPoint(int numOfZero) {
        System.out.println("numOfZero: " + numOfZero);
        if (numOfZero <= 0) {
            return "";
        }
        String result = "";
        for (int i = 0; i < numOfZero; i++) {
            result += "0";
        }
        return result;
    }

    public static void setGrayText(TextView textView, String content) {
        textView.setText(content);
        textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.color_b9bbca));
    }

    /**
     * 添加输入框清除按钮显示隐藏的监听
     *
     * @param editText
     */
    public static void setViewsVisiiblityOnTextWatcher(final EditText editText, final View... views) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for (View view : views) {
                    view.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                for (View view : views) {
                    view.setVisibility(hasFocus && !TextUtils.isEmpty(editText.getText().toString().trim()) ? View.VISIBLE : View.GONE);
                }
            }
        });
    }

    public static void onClickEyesListener(EditText etPwd, ImageView ivEyes, boolean isAcceptSpace) {
        if (ivEyes.isSelected()) {
            etPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        ivEyes.setSelected(!ivEyes.isSelected());
        etPwd.setSelection(etPwd.getText().toString().trim().length());
        etPwd.setKeyListener(getNumberKeyListener(isAcceptSpace));
    }

    public static NumberKeyListener getNumberKeyListener(final boolean isAcceptSpace) {
        return getNumberKeyListener(isAcceptSpace, false);
    }

    public static NumberKeyListener getNumberKeyListener(final boolean isAcceptSpace, final boolean isOnlyNumbers) {
        return new NumberKeyListener() {
            @Override
            protected char[] getAcceptedChars() {
                char[] acceptChars = null;
                if (isAcceptSpace) {
                    if (isOnlyNumbers) {
                        acceptChars = "0123456789 ".toCharArray();
                    } else {
                        acceptChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ".toCharArray();
                    }
                } else {
                    if (isOnlyNumbers) {
                        acceptChars = "0123456789".toCharArray();
                    } else {
                        acceptChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
                    }
                }
                return acceptChars;
            }

            @Override
            public int getInputType() {
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }
        };
    }

    public static NumberKeyListener getIDCardFilter() {
        return new NumberKeyListener() {
            @Override
            protected char[] getAcceptedChars() {
                char[] acceptChars = "0123456789xX".toCharArray();
                return acceptChars;
            }

            @Override
            public int getInputType() {
                return InputType.TYPE_CLASS_TEXT;
            }
        };
    }

    /**
     * 高亮文本 默认颜色为 0084ff
     *
     * @param text
     * @param target
     * @param start
     * @param end
     * @return
     */
    public static SpannableString highlight(String text, String target, int start, int end) {
        return highlight(text, target, start, end, "#0084ff");
    }

    /**
     * 高亮文本，颜色格式为 "#0084ff"
     *
     * @param text
     * @param target
     * @param start
     * @param end
     * @param color
     * @return
     */
    public static SpannableString highlight(String text, String target, int start, int end, String color) {
        SpannableString spannableString = new SpannableString(text);
        try {
            Pattern pattern = Pattern.compile(target);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor(color));
                spannableString.setSpan(span, matcher.start() - start, matcher.end() + end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            LogUtils.e("highlight>>>Exception: " + e.getMessage());
        }
        return spannableString;
    }

    /**
     * 获取短信验证码
     *
     * @param mobile     手机号
     * @param showModule 获取渠道
     */
    public static void getSmsCheckCode(final String mobile, final String showModule, final OnGetSmsCheckCodeListener onGetSmsCheckCodeListener) {
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.showShort("手机号码不可为空");
            return;
        }
        if (!StringUtil.isPhoneNum(mobile)) {
            ToastUtils.showShort("请输入正确手机号码");
            return;
        }
        BaseBusiness.postRequest(new OnResponseListener<SmsCheckCodeBean>() {
            @Override
            public Observable<BaseBean<SmsCheckCodeBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("mobile", Aes.encryptAES(mobile));
                paramMap.putLast("showModule", showModule);
                return NetService.getNetService().getCheckCode(paramMap);
            }

            @Override
            public void onResponse(BaseBean<SmsCheckCodeBean> result) {
                if (onGetSmsCheckCodeListener != null) {
                    onGetSmsCheckCodeListener.onGetSmsCheckCode(result);
                }
            }

            @Override
            public void onError(String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }

    public interface OnGetSmsCheckCodeListener {
        void onGetSmsCheckCode(BaseBean<SmsCheckCodeBean> result);
    }


    /**
     * 拦截空格
     *
     * @param editTexts
     */
    public static void addSpaceInputFilter(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setFilters(getNewInputFilters(editText, getSpaceInputFilter()));
        }
    }

    private static InputFilter getSpaceInputFilter() {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" "))
                    return "";
                return null;
            }
        };
    }

    public static void addSpecialCharFilter(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setFilters(getNewInputFilters(editText, getSpecialCharFilter()));
        }
    }

    public static void addSpaceCharFilterAndSpecialCharFilter(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setFilters(getNewInputFilters(editText, getSpecialCharFilter(), getSpaceInputFilter()));
        }
    }

    /**
     * 将新的InputFilter添加到现有InputFilter数组中
     *
     * @param editText        用来获取原有的InputFilter数组
     * @param newInputFilters 将要添加的新的InputFilter
     * @return
     */
    private static InputFilter[] getNewInputFilters(EditText editText, InputFilter... newInputFilters) {
        int oldLength = editText.getFilters().length;
        int newLength = newInputFilters.length;

        InputFilter[] destFilters = new InputFilter[oldLength + newLength];

        System.arraycopy(editText.getFilters(), 0, destFilters, 0, oldLength);    //将原有Filters拷贝到destFilters中

        System.arraycopy(newInputFilters, 0, destFilters, oldLength, newLength);    //将新的Filters添加到destFilters中

        return destFilters;
    }

    public static InputFilter getSpecialCharFilter() {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String speChat = "^[a-zA-Z0-9]$";
//                String speChat = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）_——+|{}【】‘；：”“'。，、？-]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source.toString());
                if (!matcher.find())
                    return "";
                else
                    return null;
            }
        };
        return filter;
    }

    public static void showDialDialog(final BaseActivity baseActivity) {
        baseActivity.showCommonDialog(baseActivity.getString(R.string.hotline).replaceAll("-", " "), "取消", "呼叫", null, new CommonDialogFragment.OnRightClickListener() {
            @Override
            public void onClickRight() {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + baseActivity.getString(R.string.hotline).replaceAll("-", "")));
                baseActivity.startActivity(intent);
            }
        });
    }

    public static void setTextColor(TextView textView, int colorRes) {
        if (textView == null || textView.getContext() == null) {
            LogUtils.e("Error in setTextColor...");
            return;
        }
        textView.setTextColor(ContextCompat.getColor(textView.getContext(), colorRes));
    }

    public static float measureTextWidth(TextView textView, String text) {
        return textView.getPaint().measureText(text);
    }

}
