package com.example.jinphy.simplechat.utils;

import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by jinphy on 2017/8/12.
 */

public class ViewUtils {


    public static void setScaleXY(@NonNull View target, float scale) {
        target.setScaleX(scale);
        target.setScaleY(scale);
    }

    public static void setTransXY(@NonNull View target, float trans) {
        target.setTranslationX(trans);
        target.setTranslationY(trans);
    }

    public static void setAlpha(float alpha, View... targets) {
        for (View target : targets) {
            target.setAlpha(alpha);
        }
    }

    /**
     * 输入框中的文本以明文的方式显示
     *
     * */
    public static void showExpressText(EditText editText) {
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
    }

    /**
     * 输入框中的文本以密文的方式显示
     *
     * */
    public static void showCipherText(EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//        editText.setTransformationMethod(PasswordTransformationMethod.getInstance())
    }
}
