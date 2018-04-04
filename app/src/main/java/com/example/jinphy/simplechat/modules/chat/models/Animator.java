package com.example.jinphy.simplechat.modules.chat.models;

import android.animation.AnimatorSet;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.jinphy.simplechat.constants.IntConst;
import com.example.jinphy.simplechat.modules.chat.ChatActivity;
import com.example.jinphy.simplechat.utils.AnimUtils;
import com.example.jinphy.simplechat.utils.ColorUtils;
import com.example.jinphy.simplechat.utils.Keyboard;
import com.example.jinphy.simplechat.utils.ScreenUtils;
import com.example.jinphy.simplechat.utils.ViewUtils;

/**
 * DESC:
 * Created by jinphy on 2018/4/1.
 */

public class Animator {
    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;

    private final ChatActivity activity;
    private MyView myView;
    private AnimatorSet animatorSet = null;

    private int moveOrientation;
    private float oldX;
    private float deltaX;
    private float downX;

    // 状态栏颜色
    public final int startStatusBarColor;
    public final int endStatusBarColor;

    // 屏幕宽度
    public final int screenWidth;
    public final int screenWidthFor1Of3;

    // 最大高度
    public final int maxElevation;

    public int recyclerViewScrollY;


    public static Animator init(ChatActivity activity, MyView myView) {
        return new Animator(activity, myView);
    }

    private Animator(ChatActivity activity, MyView myView) {
        this.activity = activity;
        this.myView = myView;
        startStatusBarColor = activity.colorPrimaryDark();
        endStatusBarColor = activity.colorAccent();
        screenWidth = ScreenUtils.getScreenWidth(activity);
        screenWidthFor1Of3 = screenWidth / 3;
        maxElevation = ScreenUtils.dp2px(activity, 20);
    }


    public boolean handleHorizontalEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                oldX = downX;
                return false;
            case MotionEvent.ACTION_MOVE:
                moveOrientation = HORIZONTAL;
                deltaX = event.getX() - oldX;
                oldX = event.getX();
                if (canMoveHorizontal()) {
                    float factor = getHorizontalMoveFactor(deltaX);
                    moveHorizontal(factor);
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (moveOrientation == HORIZONTAL) {
                    float factor = myView.rootView.getTranslationX() / screenWidth;
                    if (factor < 1.0f / 3) {
                        animateHorizontal(factor, 0, false);
                    } else {
                        animateHorizontal(factor, 1f, true);
                    }
                    return true;
                }
                return false;
            default:
                return false;
        }

    }

    public boolean handleVerticalEvent(MotionEvent event) {

        return true;
    }


    private boolean canMoveHorizontal() {
        return (downX < screenWidthFor1Of3) && (myView.rootView.getTranslationX() >= 0);
    }

    public void moveHorizontal(float factor) {
        float transX = factor * screenWidth;
        myView.rootView.setTranslationX(transX);
        myView.appBar.setTranslationX(transX);
        myView.recyclerView.setAlpha(1 - factor);
        myView.toolbar.setAlpha((1 - factor));
        myView.bottomBar.rootView.setAlpha(1 - factor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myView.rootView.setElevation((float) (maxElevation * (1 - factor * 0.5)));
        }
    }



    private float getHorizontalMoveFactor(float deltaX) {
        float transX = myView.rootView.getTranslationX();
        transX += deltaX;

        if (deltaX < 0 && transX < 0) {
            // 向左滑动
            transX = 0;
        }

        float factor = transX / screenWidth;

        return factor;
    }

    public void animateHorizontal(float fromFactor, float toFactor, boolean exit) {
        float deltaFactor = Math.abs(toFactor - fromFactor);
        AnimUtils.just()
                .setFloat(fromFactor, toFactor)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration((long) (IntConst.DURATION_500 * deltaFactor))
                .onUpdateFloat(animator -> {
                    float factor = (float) animator.getAnimatedValue();
                    moveHorizontal(factor);
                })
                .onEnd(animator -> {
                    if (exit) {
                        Keyboard.close(activity, myView.bottomBar.textInput);
                        activity.finish();
                    }
                })
                .animate();
    }

    public void animateBar(float fromValue, float toValue, boolean showBar) {
        if (animatorSet != null && animatorSet.isRunning()) {
            animatorSet.end();
        }
        int appbarHeight = myView.toolbar.getHeight();
        int bottomBarHeight = myView.bottomBar.rootView.getMeasuredHeight();

        animatorSet = AnimUtils.just()
                .setFloat(fromValue, toValue)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(IntConst.DURATION_300)
                .onStart(animator -> {
                    if (showBar) {
                        myView.toolbar.setVisibility(View.VISIBLE);
                        myView.bottomBar.rootView.setVisibility(View.VISIBLE);
                    } else {
                        myView.fab.setVisibility(View.VISIBLE);
                    }
                })
                .onUpdateFloat(animator -> {
                    float value = (float) animator.getAnimatedValue();
                    float marginTop = appbarHeight * (1 - value);
                    float marginBottom = bottomBarHeight * (1 - value);
                    myView.toolbar.setTranslationY(value * (-appbarHeight));
                    myView.bottomBar.rootView.setTranslationY(value * bottomBarHeight);
                    ViewUtils.setScaleXY(myView.fab, value);
                    animateStatusColor(value);
                    //setMargin(view, marginTop, marginBottom);
                })
                .onEnd(animator -> {
                    if (showBar) {
                        myView.fab.setVisibility(View.GONE);
                    } else {
                        myView.toolbar.setVisibility(View.GONE);
                        myView.bottomBar.rootView.setVisibility(View.GONE);
                    }
                })
                .animate();
    }

    public void animateStatusColor(float factor) {
        int color = ColorUtils.rgbColorByFactor(startStatusBarColor, endStatusBarColor, factor);
        ScreenUtils.setStatusBarColor(activity, color);
    }
}
