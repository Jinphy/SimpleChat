package com.example.jinphy.simplechat.listener_adapters;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * DESC: 默认实现的TextWatcher，如果要监听EditText的一个以上的方法请使用该接口，否则使用该类的特定的某个子接口
 *
 * Created by jinphy on 2017/12/24.
 */

public interface TextListener extends TextWatcher {
    /**
     * DESC:
     * start：在s中，将要被替换的字符串的起始位置
     * replacedCount：将要被替换的旧字符个数
     * replacingCount：将要替换的新字符个数
     * Created by Jinphy, on 2017/12/6, at 16:35
     */
    @Override
    default void beforeTextChanged(CharSequence s, int start, int replacedCount, int replacingCount) {}

    /**
     * DESC:
     * start：在s中，被替换的字符串的起始位置
     * replacedCount：已经被替换的旧字符个数
     * replacingCount：已经替换的新字符个数
     * Created by Jinphy, on 2017/12/6, at 16:34
     */
    @Override
    default void onTextChanged(CharSequence s, int start, int replacedCount, int replacingCount) {}

    /**
     * DESC:
     * editable.toString()-->返回改变后EditText中的字符串
     * editable.length()-->返回改变后EditText中的字符串长度
     * <p>
     * Created by Jinphy, on 2017/12/6, at 16:33
     */
    @Override
    default void afterTextChanged(Editable editable) {}


    //=============以下三个内部类时三个函数式接口，用来分别执行某一个方法的回调 ============================

    //--------该接口用来只监听EditText文本变化前的回调 -------------------------------------------------

    /**
     * DESC: 函数式接口，监听Text改变前回调
     * <p>
     * Created by jinphy on 2017/12/24.
     */
    interface Before extends TextListener {
        /**
         * DESC:
         * start：在s中，将要被替换的字符串的起始位置
         * replacedCount：将要被替换的旧字符个数
         * replacingCount：将要替换的新字符个数
         * Created by Jinphy, on 2017/12/6, at 16:35
         */
        @Override
        void beforeTextChanged(CharSequence s, int start, int replacedCount, int replacingCount);

    }


    //--------该接口用来只监听EditText文本变化时的回调 -------------------------------------------------

    /**
     * DESC: 函数式接口，监听Text改变时的回调
     * <p>
     * Created by jinphy on 2017/12/24.
     */
    interface When extends TextListener {
        /**
         * DESC:
         * start：在s中，被替换的字符串的起始位置
         * replacedCount：已经被替换的旧字符个数
         * replacingCount：已经替换的新字符个数
         * Created by Jinphy, on 2017/12/6, at 16:34
         */
        @Override
        void onTextChanged(CharSequence s, int start, int replacedCount, int replacingCount);

    }


    //--------该接口用来只监听EditText文本变化后的回调 -------------------------------------------------

    /**
     * DESC: 函数式接口，监听Text改变后回调
     * <p>
     * Created by jinphy on 2017/8/14.
     */
    interface After extends TextListener {
        /**
         * DESC:
         * editable.toString()-->返回改变后EditText中的字符串
         * editable.length()-->返回改变后EditText中的字符串长度
         * <p>
         * Created by Jinphy, on 2017/12/6, at 16:33
         */
        @Override
        void afterTextChanged(Editable editable);
    }
}
