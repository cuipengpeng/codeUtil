package com.test.bank.utils;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by 55 on 2018/2/7.
 */

public class BankTextWatcher implements TextWatcher {

    //default max length = 21 + 5 space
    private static final int DEFAULT_MAX_LENGTH = 21 + 5;
    //max input length
    private int maxLength = DEFAULT_MAX_LENGTH;
    private int beforeTextLength = 0;
    private boolean isChanged = false;

    //space count
    private int space = 0;

    private StringBuffer buffer = new StringBuffer();
    private EditText editText;
    ImageView ivClear;

    public static void bind(EditText editText, ImageView ivClear) {
        bind(editText, ivClear, null);
    }

    public static void bind(EditText editText, ImageView ivClear, OnCheckBankNoListener onCheckBankNoListener) {
        new BankTextWatcher(editText, DEFAULT_MAX_LENGTH, ivClear, onCheckBankNoListener);
    }

    public static void bind(EditText editText, int maxLength, ImageView ivClear, OnCheckBankNoListener onCheckBankNoListener) {
        new BankTextWatcher(editText, maxLength, ivClear, onCheckBankNoListener);
    }

    public BankTextWatcher(EditText editText, int maxLength, ImageView ivClear, OnCheckBankNoListener onCheckBankNoListener) {
        this.editText = editText;
        this.maxLength = maxLength;
        this.ivClear = ivClear;
        this.onCheckBankNoListener = onCheckBankNoListener;
        editText.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        beforeTextLength = s.length();
        if (buffer.length() > 0) {
            buffer.delete(0, buffer.length());
        }
        space = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
                space++;
            }
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (ivClear != null) {
            ivClear.setVisibility(s.length() == 0 ? View.GONE : View.VISIBLE);
        }
        int length = s.length();
        String realBankNo = s.toString().replaceAll(" ", "").trim();
        if (onCheckBankNoListener != null && realBankNo.length() >= 16) {
            LogUtils.d("onBankNoChange: " + s.toString() + "[" + s.length() + "]" + " realBankNo: " + realBankNo + " realLength: " + realBankNo.length());
            onCheckBankNoListener.onCheckBankNo(realBankNo);
        }
        buffer.append(s.toString());
        if (length == beforeTextLength || length <= 3
                || isChanged) {
            isChanged = false;
            return;
        }
        isChanged = true;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isChanged) {
            int selectionIndex = editText.getSelectionEnd();
            //total char length
            int index = 0;
            while (index < buffer.length()) {
                if (buffer.charAt(index) == ' ') {
                    buffer.deleteCharAt(index);
                } else {
                    index++;
                }
            }
            //total space count
            index = 0;
            int totalSpace = 0;
            while (index < buffer.length()) {
                if ((index == 4 || index == 9 || index == 14 || index == 19 || index == 24)) {
                    buffer.insert(index, ' ');
                    totalSpace++;
                }
                index++;
            }
            //selection index
            if (totalSpace > space) {
                selectionIndex += (totalSpace - space);
            }
            char[] tempChar = new char[buffer.length()];
            buffer.getChars(0, buffer.length(), tempChar, 0);
            String str = buffer.toString();
            if (selectionIndex > str.length()) {
                selectionIndex = str.length();
            } else if (selectionIndex < 0) {
                selectionIndex = 0;
            }
            editText.setText(str);
            Editable text = editText.getText();
            //set selection
            Selection.setSelection(text, selectionIndex < maxLength ? selectionIndex : maxLength);
            isChanged = false;
        }
    }

    private OnCheckBankNoListener onCheckBankNoListener;

    public interface OnCheckBankNoListener {
        void onCheckBankNo(String bankNo);
    }
}
