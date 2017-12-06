package com.example.jinphy.simplechat.listener_adapters;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by jinphy on 2017/8/14.
 */
@FunctionalInterface
public interface TextListener extends TextWatcher {
    /**
     * DESC:
     * start：在s中，将要被替换的字符串的起始位置
     * replacedCount：将要被替换的旧字符个数
     * replacingCount：将要替换的新字符个数
     * Created by Jinphy, on 2017/12/6, at 16:35
     */
    @Override
    default void beforeTextChanged(CharSequence s, int start, int replacedCount, int replacingCount) {


    }

    /**
     * DESC:
     * start：在s中，被替换的字符串的起始位置
     * replacedCount：已经被替换的旧字符个数
     * replacingCount：已经替换的新字符个数
     * Created by Jinphy, on 2017/12/6, at 16:34
     */
    @Override
    default void onTextChanged(CharSequence s, int start, int replacedCount, int replacingCount) {

    }

    /**
     *
     * DESC:
     * editable.toString()-->返回改变后EditText中的字符串
     * editable.length()-->返回改变后EditText中的字符串长度
     *
     * Created by Jinphy, on 2017/12/6, at 16:33
     */
    @Override
    void afterTextChanged(Editable editable);
}
