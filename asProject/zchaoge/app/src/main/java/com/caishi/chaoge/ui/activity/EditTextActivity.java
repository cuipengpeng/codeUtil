package com.caishi.chaoge.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.bean.LrcBean;
import com.caishi.chaoge.ui.widget.dialog.DialogUtil;
import com.caishi.chaoge.ui.widget.dialog.IDialog;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.StringUtil;
import com.google.common.base.Joiner;
import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditTextActivity extends BaseActivity {

    private ArrayList<LrcBean> responseLrcBeanList;
    private EditText et_editText_text;
    private ArrayList<String> strList = new ArrayList<>();
    private final int MAX_TEXT_PER_LINE = 9;
    private String personMusicPath;

    @Override
    public void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        personMusicPath = bundle.getString("personMusicPath");
        responseLrcBeanList = (ArrayList<LrcBean>) bundle.getSerializable("ResponseLrcBeanList");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_edit_text;
    }

    @Override
    public void initView(View view) {
        ImmersionBar.with(this).titleBar(R.id.editText_view)
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .init();
        setBaseTitle("编辑文字", 0, "下一步");
        et_editText_text = $(R.id.et_editText_text);
        for (int i = 0; i < responseLrcBeanList.size(); i++) {
            strList.add(responseLrcBeanList.get(i).getLrc());
        }
    }

    @Override
    public void doBusiness() {
        if (strList.size() > 0)
            et_editText_text.setText(Joiner.on("\n").join(strList));
    }

    @Override
    public void setListener() {

        et_editText_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                LogUtil.printLog("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (StringUtil.isNull(s.toString())) {
                    return;
                }

                ((EditTextActivity) mContext).tv_baseTitle_save.setEnabled(s.length() != 0);
                LogUtil.printLog("");
                String string = s.toString();
                String[] arr = string.split("\n");

                boolean hasBlankLine = false;
                List<String> stringList = new ArrayList<>();
                stringList.addAll(Arrays.asList(arr));
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = arr[i].replace(" ", "");
                    if (StringUtil.isNull(arr[i])) {
                        hasBlankLine = true;
                        stringList.remove(i);
                    }
                    LogUtil.printLog(arr[i]);
                }
                if (hasBlankLine) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < stringList.size(); i++) {
                        stringBuilder.append(stringList.get(i) + "\n");
                    }
                    et_editText_text.setText(stringBuilder);
                    return;
                }

                LogUtil.printLog("光标行号：" + getCurrentCursorLine(et_editText_text));
                int lineNumber = getCurrentCursorLine(et_editText_text);
                if (arr.length > responseLrcBeanList.size()) {
                    //增加行
                    if (lineNumber > 1) {
                        int editIndex = lineNumber - 1 - 1;
                        LrcBean lrcBean = responseLrcBeanList.get(editIndex);
                        long charTime = (lrcBean.getEnd() - lrcBean.getStart()) / lrcBean.getLrc().length();
                        LrcBean generateLrcBean1 = new LrcBean();
                        generateLrcBean1.setLrc(arr[editIndex]);
                        generateLrcBean1.setStart(lrcBean.getStart());
                        generateLrcBean1.setEnd(lrcBean.getStart() + charTime * arr[editIndex].length());
                        LrcBean generateLrcBean2 = new LrcBean();
                        generateLrcBean2.setLrc(arr[editIndex + 1]);
                        generateLrcBean2.setStart(generateLrcBean1.getEnd());
                        generateLrcBean2.setEnd(lrcBean.getEnd());

                        responseLrcBeanList.remove(editIndex);
                        responseLrcBeanList.add(editIndex, generateLrcBean1);
                        responseLrcBeanList.add(editIndex + 1, generateLrcBean2);
                    }
                } else if (arr.length < responseLrcBeanList.size()) {
                    //删除行
                    if(arr.length<=0){
                        responseLrcBeanList.clear();
                    }
                    if (lineNumber >= 1  && arr.length > 0) {
                        int editIndex = 0;
                        if (lineNumber > arr.length) {
                            //光标行数判断，避免删除最后一行，导致异常
                            lineNumber = lineNumber - 1;
                            editIndex = lineNumber - 1;
                        } else {
                            editIndex = lineNumber - 1;
                        }
                        LrcBean lrcBean1 = responseLrcBeanList.get(editIndex);
                        LrcBean lrcBean2 = new LrcBean();
                        //光标行数判断，避免删除最后一行，导致异常
                        if (responseLrcBeanList.size() > lineNumber) {
                            lrcBean2 = responseLrcBeanList.get(lineNumber);
                        }
                        LrcBean generateLrcBean1 = new LrcBean();
                        generateLrcBean1.setLrc(arr[editIndex]);
                        generateLrcBean1.setStart(lrcBean1.getStart());
                        generateLrcBean1.setEnd(lrcBean1.getEnd() + lrcBean2.getEnd() - lrcBean2.getStart());
                        responseLrcBeanList.remove(editIndex);
                        responseLrcBeanList.remove(editIndex);
                        responseLrcBeanList.add(editIndex, generateLrcBean1);
                    }

                } else {
                    //行数不变，只删除或增加文字
                    for (int i = 0; i < arr.length; i++) {
                        responseLrcBeanList.get(i).setLrc(arr[i]);
                    }
                }

                for (int i = 0; i < responseLrcBeanList.size(); i++) {
                    LogUtil.printLog(responseLrcBeanList.get(i).getStart() + "--" + responseLrcBeanList.get(i).getEnd() + "--" + responseLrcBeanList.get(i).getLrc());
                }

                LogUtil.printLog("---------------------- size相等：" + (arr.length == responseLrcBeanList.size()));
                //处理一行多于9个字的行，自动断行，并分配时间
                boolean greaterNine = false;
                int removePosition = 0;
                for (int k = 0; k < arr.length; k++) {
                    int nineCount = 0;
                    String lineString = arr[k];
                    if (lineString.length() > MAX_TEXT_PER_LINE) {
                        greaterNine = true;
                        LrcBean lrcBean = responseLrcBeanList.get(k);
                        long charTime = (lrcBean.getEnd() - lrcBean.getStart()) / lineString.length();
                        responseLrcBeanList.remove(removePosition);
                        nineCount = lineString.length() / MAX_TEXT_PER_LINE;
                        String tmp = "";
                        for (int p = 0; p < nineCount; p++) {
                            tmp = lineString.substring(p * MAX_TEXT_PER_LINE, (p + 1) * MAX_TEXT_PER_LINE);
                            LrcBean generateLrcBean = new LrcBean();
                            generateLrcBean.setLrc(tmp);
                            generateLrcBean.setStart(lrcBean.getStart() + charTime * p * MAX_TEXT_PER_LINE);
                            generateLrcBean.setEnd(lrcBean.getStart() + charTime * (p + 1) * MAX_TEXT_PER_LINE);
                            responseLrcBeanList.add((p + removePosition), generateLrcBean);
                        }
                        removePosition = removePosition + nineCount - 1;

                        tmp = lineString.substring(nineCount * MAX_TEXT_PER_LINE, lineString.length());
                        if (StringUtil.notEmpty(tmp)) {
                            LrcBean generateLrcBean = new LrcBean();
                            generateLrcBean.setLrc(tmp);
                            generateLrcBean.setStart(lrcBean.getStart() + charTime * nineCount * MAX_TEXT_PER_LINE);
                            generateLrcBean.setEnd(lrcBean.getEnd());
                            responseLrcBeanList.add(removePosition + nineCount, generateLrcBean);
                            removePosition += 1;
                        }
                    }
                    removePosition++;
                }

                for (int i = 0; i < responseLrcBeanList.size(); i++) {
                    LogUtil.printLog(responseLrcBeanList.get(i).getStart() + "--" + responseLrcBeanList.get(i).getEnd() + "--" + responseLrcBeanList.get(i).getLrc());
                }

                //设置edittext文本框内容
                if (greaterNine) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < responseLrcBeanList.size(); i++) {
                        stringBuilder.append(responseLrcBeanList.get(i).getLrc() + "\n");
                    }
                    et_editText_text.setText(stringBuilder);
                    //移动光标
                    stringBuilder.delete(0, stringBuilder.length());
                    for (int i = 0; i < lineNumber; i++) {
                        stringBuilder.append(responseLrcBeanList.get(i).getLrc());
                    }
                    et_editText_text.setSelection(stringBuilder.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void onBackPressed() {
        DialogUtil.createDefaultDialog(mContext, "提示", "返回后将删除语音和文字",
                "确定", new IDialog.OnClickListener() {
                    @Override
                    public void onClick(IDialog dialog) {
                        dialog.dismiss();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                },
                "取消", new IDialog.OnClickListener() {
                    @Override
                    public void onClick(IDialog dialog) {
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void onSaveClick(View v) {
        super.onSaveClick(v);
        Bundle bundle = new Bundle();
        bundle.putString("personMusicPath", personMusicPath);
        bundle.putSerializable("lrcList", responseLrcBeanList);
        startActivity(DeclaimActivity.class,bundle);

        finish();
    }

    private int getCurrentCursorLine(EditText editText) {
        int selectionStart = Selection.getSelectionStart(editText.getText());
        Layout layout = editText.getLayout();

        if (selectionStart != -1) {
            return layout.getLineForOffset(selectionStart) + 1;
        }
        return -1;
    }


}
