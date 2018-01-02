package com.example.jinphy.simplechat.animate_interpolator;

import android.view.animation.Interpolator;

/**
 * DESC:
 * Created by jinphy on 2018/1/1.
 */

public class HorizontalLoadingInterpolator implements Interpolator {

    private static double POW = 1.0/2.0;

    @Override
    public float getInterpolation(float input) {
        return input < 0.5
                ? (float) Math.pow(input * 2, POW) * 0.5f
                : (float) Math.pow((1 - input) * 2, POW) * -0.5f + 1;
    }
}

