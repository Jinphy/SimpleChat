package com.example.jinphy.simplechat.custom_view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.jinphy.simplechat.utils.ScreenUtils;

/**
 * DESC:
 * Created by jinphy on 2018/3/14.
 */

public class MyDialog extends AlertDialog implements Dialog{

    private int width;

    private int height ;

    private int resourceId;

    private View view;

    protected MyDialog(Context context) {
        super(context);
    }

    public static Dialog create(Context context) {
        return new MyDialog(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        getWindow().setLayout(600,300);
        Window window = getWindow();
        final WindowManager.LayoutParams attrs = window.getAttributes();
        // 设置该值可以自己设置的布局背景的形状才能够显示，比如cardView的圆角
        window.getDecorView().setBackgroundColor(Color.TRANSPARENT);

        if (width > 0) {
            attrs.width = ScreenUtils.dp2px(getContext(), width);
        }
        if (height > 0) {
            attrs.height = ScreenUtils.dp2px(getContext(), height);
        }

        window.setAttributes(attrs);
    }

    @Override
    public Dialog width(int valueDp) {
        this.width = valueDp;
        return this;
    }

    @Override
    public Dialog height(int valueDp) {
        this.height = valueDp;
        return this;
    }

    @Override
    public Dialog view(View view) {
        this.view = view;
        setView(view);
        return this;
    }

    @Override
    public Dialog view(int resourceId) {
        view =LayoutInflater.from(getContext())
                .inflate(resourceId, null, false);
        setView(view);
        return this;
    }

    @Override
    public Holder display() {
        show();
        return new Holder(view, this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public static class Holder{
        public final View view;

        public final Dialog dialog;

        public Holder(View view, Dialog dialog) {
            this.view = view;
            this.dialog = dialog;
        }
    }
}
