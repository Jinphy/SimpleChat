package com.example.jinphy.simplechat.utils;

import android.support.annotation.NonNull;
import android.view.View;

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
}
