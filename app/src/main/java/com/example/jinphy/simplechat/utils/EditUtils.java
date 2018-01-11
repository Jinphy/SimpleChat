package com.example.jinphy.simplechat.utils;

import android.text.InputType;
import android.widget.EditText;

/**
 * DESC:
 * Created by jinphy on 2018/1/9.
 */

public class EditUtils {


    /**
     * DESC: 设置EditText为文本密码的方式输入
     * Created by jinphy, on 2018/1/9, at 17:16
     */
    public static void textPassword(EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    /**
     * DESC: 设置EditText为数字密码的方式输入
     * Created by jinphy, on 2018/1/9, at 17:29
     */
    public static void numberPassword(EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
    }

    /**
     * DESC: 设置EditText为文本明文的方式输入
     * Created by jinphy, on 2018/1/9, at 17:17
     */
    public static void text(EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
    }

    /**
     * DESC: 设置EditText为数字明文的方式输入
     * Created by jinphy, on 2018/1/9, at 17:37
     */
    public static void number(EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_NORMAL);
    }

    /**
     * DESC: 设置EditText为十进制数字明文的方式输入
     * Created by jinphy, on 2018/1/9, at 17:37
     */
    public static void numberDecimal(EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }



    /**
     * DESC: 设置EditText为电话明文的方式输入
     * Created by jinphy, on 2018/1/9, at 17:38
     */
    public static void phone(EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
    }

}
