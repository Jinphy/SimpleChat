package com.example.jinphy.simplechat.custom_view.dialog.my_dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.jinphy.simplechat.utils.ScreenUtils;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

/**
 * DESC:
 * Created by jinphy on 2018/3/14.
 */

public class MyDialog extends AlertDialog implements MyDialogInterface {

    private int width;

    private int height;

    private int resourceId;

    private View view;

    private boolean hasFocus = true;

    /**
     * DESC: x = 0 为默认值，对话框会在中间
     * Created by jinphy, on 2018/3/27, at 9:02
     */
    private int x;

    /**
     * DESC: y = 0 为默认值，对话框会在中间
     * Created by jinphy, on 2018/3/27, at 9:03
     */
    private int y;

    private int gravity = Gravity.CENTER;
    private Holder holder;
    private float alpha = 1;


    protected MyDialog(Context context) {
        super(context);
    }

    public static MyDialogInterface create(Context context) {
        return new MyDialog(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        getWindow().setLayout(600,300);
        Window window = getWindow();

        window.setFlags(FLAG_FULLSCREEN,FLAG_FULLSCREEN);

        if (!hasFocus) {
            window.setFlags(FLAG_NOT_FOCUSABLE,FLAG_NOT_FOCUSABLE);
        }

        final WindowManager.LayoutParams attrs = window.getAttributes();
        // 设置该值可以自己设置的布局背景的形状才能够显示，比如cardView的圆角
        window.getDecorView().setBackgroundColor(Color.TRANSPARENT);

        if (width > 0) {
            attrs.width = ScreenUtils.dp2px(getContext(), width);
        }
        if (height > 0) {
            attrs.height = ScreenUtils.dp2px(getContext(), height);
        }
        attrs.x = ScreenUtils.dp2px(getContext(), x);
        attrs.y = ScreenUtils.dp2px(getContext(), y);
        attrs.gravity = gravity;
        attrs.alpha = alpha;
        window.setAttributes(attrs);
    }

    @Override
    public MyDialogInterface width(int valueDp) {
        this.width = valueDp;
        return this;
    }

    @Override
    public MyDialogInterface height(int valueDp) {
        this.height = valueDp;
        return this;
    }

    @Override
    public MyDialogInterface alpha(float alpha) {
        this.alpha = alpha;
        return this;
    }

    @Override
    public MyDialogInterface x(int valueDp) {
        this.x = valueDp;
        return this;
    }

    @Override
    public MyDialogInterface y(int valueDp) {
        this.y = valueDp;
        return this;
    }

    @Override
    public MyDialogInterface gravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    @Override
    public MyDialogInterface view(View view) {
        this.view = view;
        setView(view);
        holder = new Holder(view, this);
        return this;
    }

    @Override
    public MyDialogInterface view(int resourceId) {
        view =LayoutInflater.from(getContext())
                .inflate(resourceId, null, false);
        setView(view);
        holder = new Holder(view, this);
        return this;
    }

    @Override
    public MyDialogInterface cancelable(boolean cancelable) {
        setCancelable(cancelable);
        return this;
    }

    @Override
    public MyDialogInterface hasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
        return this;
    }

    @Override
    public MyDialogInterface onDismiss(Callback onDismiss) {
        this.setOnDismissListener(dialog -> onDismiss.call(holder));
        return this;
    }

    @Override
    public MyDialogInterface onDisplay(Callback onDisplay) {
        this.setOnShowListener(dialog -> onDisplay.call(holder));
        return this;
    }

    @Override
    public Holder display() {
        show();
        return holder;
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }

    public static class Holder{
        public final View view;

        public final MyDialogInterface dialog;

        public Holder(View view, MyDialogInterface dialog) {
            this.view = view;
            this.dialog = dialog;
        }
    }

    public interface Callback {
        void call(Holder holder);
    }
}
