package com.example.jinphy.simplechat.listener_adapters;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by jinphy on 2017/8/14.
 */

public class TextWatcherAdapter implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int replacedCount, int replacingCount) {
        //start：在s中，将要被替换的字符串的起始位置
        //replacedCount：将要被替换的旧字符个数
        //replacingCount：将要替换的新字符个数

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int replacedCount, int replacingCount) {
        //start：在s中，被替换的字符串的起始位置
        //replacedCount：已经被替换的旧字符个数
        //replacingCount：已经替换的新字符个数
    }

    @Override
    public void afterTextChanged(Editable editable) {
        // editable.length()-->返回改变后EditText中的字符串长度
        // editable.toString()-->返回改变后EditText中的字符串

    }
}
