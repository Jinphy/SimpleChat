package com.example.jinphy.simplechat.utils;

/**
 * Created by jinphy on 2017/8/13.
 */

public class MathUtils {

    public static boolean within(int value,int min, int max) {
        return value >= min && value <= max;
    }

    public static boolean within(float value, float min, float max) {
        return value >= min && value <= max;
    }
}
