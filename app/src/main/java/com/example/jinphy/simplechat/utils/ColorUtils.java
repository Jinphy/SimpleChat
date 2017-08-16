package com.example.jinphy.simplechat.utils;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;

/**
 * Created by jinphy on 2017/8/12.
 */

public class ColorUtils {

    /**
     * 获取两个颜色之间的指定位置的颜色
     *
     *
     * @param startColor  起始颜色
     * @param endColor  结束颜色
     * @param factor   两个颜色之间的特定位置，取值范围0~1
     * */
    public static int argbColorByFactor(
            @ColorInt int startColor,
            @ColorInt int endColor,
            @FloatRange(from = 0.0f,to = 1.0f) float factor) {
        if (factor < 0 && factor > 1) {
            throw new IllegalArgumentException(
                    "the range of argument factor must be from 0 to 1 float value");
        }
        int a = Color.alpha(startColor);
        int r = Color.red(startColor);
        int g = Color.green(startColor);
        int b = Color.blue(startColor);

        a += ((int) ((Color.alpha(endColor) - a) * factor));
        r += ((int) ((Color.red(endColor) - r) * factor));
        g += ((int) ((Color.green(endColor) - g) * factor));
        b += ((int) ((Color.blue(endColor) - b) * factor));

        return Color.argb(a,r,g,b);
    }


    /**
     * 获取两个颜色之间的指定位置的颜色
     *
     *
     * @param startColor  起始颜色
     * @param endColor  结束颜色
     * @param factor   两个颜色之间的特定位置，取值范围0~1
     * */
    public static int rgbColorByFactor(
            @ColorInt int startColor,
            @ColorInt int endColor,
            @FloatRange(from = 0.0f,to = 1.0f) float factor) {
        if (factor < 0 && factor > 1) {
            throw new IllegalArgumentException(
                    "the range of argument factor must be from 0 to 1 float value");
        }
        int r = Color.red(startColor);
        int g = Color.green(startColor);
        int b = Color.blue(startColor);

        r += ((int) ((Color.red(endColor) - r) * factor));
        g += ((int) ((Color.green(endColor) - g) * factor));
        b += ((int) ((Color.blue(endColor) - b) * factor));

        return Color.rgb(r,g,b);
    }

    public static int setAlpha(@ColorInt int color, @FloatRange(from = 0, to = 1) float factor) {
        int a = (int) (255 * factor);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(a, r, g, b);
    }


}
