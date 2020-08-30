package com.test.xcamera.phonealbum.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.xcamera.R;


/**
 * Created by zhujiahua on 17/4/27.
 */

public class MusicSearchView extends RelativeLayout implements TextWatcher, View.OnClickListener, TextView.OnEditorActionListener {
    /**
     * 输入框
     */
    private EditText et_search;
    /**
     * 输入框后面的那个清除按钮
     */
    private ImageView bt_clear;
    private OnMusicSearchView onMusicSearchView;

    public interface OnMusicSearchView{
        void onOnMusicSearchView(String keyWord);
    }
    public MusicSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.music_search_layout, this, true);
        et_search = findViewById(R.id.et_search);
        bt_clear = findViewById(R.id.bt_clear);
//        bt_clear.setImageResource(R.drawable.tool_bar_ic_cancel_pressed);
        bt_clear.setEnabled(true);
        et_search.addTextChangedListener(this);
        bt_clear.setOnClickListener(this);
        et_search.setOnEditorActionListener(this);

        //验证空格输入
//        et_search.setFilters(new InputFilter[]{getInputFilterForSpace()});
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    /****
     * 对用户输入文字的监听
     *
     * @param editable
     */
    @Override
    public void afterTextChanged(Editable editable) {
        /**获取输入文字**/
        String input = et_search.getText().toString().trim();

    }
    /**
     * 禁止输入空格
     *
     * @return
     */
    public InputFilter getInputFilterForSpace() {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                //返回null表示接收输入的字符,返回空字符串表示不接受输入的字符
                if (source.equals(" "))
                    return "";
                else
                    return null;
            }
        };
    }

    @Override
    public void onClick(View view) {
        et_search.setText("");
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if(i == EditorInfo.IME_ACTION_SEARCH){
            String input = et_search.getText().toString().trim();

            if(onMusicSearchView!=null&&!input.isEmpty()){
                onMusicSearchView.onOnMusicSearchView(input);
            }
        }

        return false;
    }
    public String getSearchValue(OnMusicSearchView searchView){
        onMusicSearchView=searchView;
        if(et_search!=null){
            return et_search.getText().toString();

        }else {
            return "";
        }
    }
    public EditText getEt_search(){
        return et_search;
    }

}
