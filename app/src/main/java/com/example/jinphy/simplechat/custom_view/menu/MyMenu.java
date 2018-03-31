package com.example.jinphy.simplechat.custom_view.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.custom_view.dialog.my_dialog.MyDialog;
import com.example.jinphy.simplechat.custom_view.dialog.my_dialog.MyDialogInterface;

/**
 * DESC:
 * Created by jinphy on 2018/3/31.
 */

public class MyMenu {

    /**
     * DESC: 菜单的根布局
     * Created by jinphy, on 2018/3/31, at 20:20
     */
    public final View view;

    private final LinearLayout container;
    private Context context;

    private MyDialogInterface dialog;

    private boolean autoDismiss = true;


    /**
     * DESC: 私有化构造函数
     * Created by jinphy, on 2018/3/31, at 19:45
     */
    private MyMenu(Context context) {
        this.context = context;
        view = LayoutInflater.from(context)
                .inflate(R.layout.menu_container, null, false);

        container = view.findViewById(R.id.container_layout);
        dialog = MyDialog.create(context).view(view);
    }

    /**
     * DESC: 创建一个菜单
     * Created by jinphy, on 2018/3/31, at 20:56
     */
    public static MyMenu create(Context context) {
        return new MyMenu(context);
    }

    /**
     * DESC: 设置菜单布局的对齐方式
     * Created by jinphy, on 2018/3/31, at 20:55
     */
    public MyMenu gravity(int gravity) {
        dialog.gravity(gravity);
        return this;
    }

    /**
     * DESC: 设置菜单宽度
     * Created by jinphy, on 2018/3/31, at 20:55
     */
    public MyMenu width(int valueDp) {
        dialog.width(valueDp);
        return this;
    }


    /**
     * DESC: 设置菜单高度
     *
     * Created by jinphy, on 2018/3/31, at 20:55
     */
    public MyMenu height(int valueDp) {
        dialog.height(valueDp);
        return this;
    }

    /**
     * DESC: 设置透明度
     * Created by jinphy, on 2018/3/31, at 20:55
     */
    public MyMenu alpha(float alpha) {
        dialog.alpha(alpha);
        return this;
    }

    /**
     * DESC: 菜单布局相对于当前位置的偏移量
     * Created by jinphy, on 2018/3/31, at 20:54
     */
    public MyMenu offset(int xValueDp, int yValueDp) {
        dialog.x(xValueDp);
        dialog.y(yValueDp);
        return this;
    }

    /**
     * DESC: 当点击菜单外部是是否可以取消
     *
     * Created by jinphy, on 2018/3/31, at 20:54
     */
    public MyMenu cancelable(boolean flag) {
        dialog.cancelable(flag);
        return this;
    }

    /**
     * DESC: 菜单项被点击时是否自动关闭菜单，默认自动关闭
     * Created by jinphy, on 2018/3/31, at 20:53
     */
    public MyMenu autoDismiss(boolean flag) {
        this.autoDismiss = false;
        return this;
    }

    /**
     * DESC: 添加一个菜单项
     * Created by jinphy, on 2018/3/31, at 20:52
     */
    public MyMenu item(CharSequence text, OnClick onClick) {
        TextView item = createItem();
        item.setText(text);
        item.setOnClickListener(v -> {
            if (onClick != null) {
                onClick.call(this, v);
            }
            if (autoDismiss) {
                dismiss();
            }
        });
        container.addView(item);
        return this;
    }

    /**
     * DESC: 添加一个菜单项
     * Created by jinphy, on 2018/3/31, at 20:52
     */
    public MyMenu item(int textId, OnClick onClick) {
        TextView item = createItem();
        item.setText(textId);
        item.setOnClickListener(v -> {
            if (onClick != null) {
                onClick.call(this, v);
            }
            if (autoDismiss) {
                dismiss();
            }
        });
        container.addView(item);
        return this;
    }

    /**
     * DESC: 添加一个菜单项
     * Created by jinphy, on 2018/3/31, at 20:52
     */
    public MyMenu item(View item, OnClick onClick) {
        return item(item, null, onClick);
    }

    /**
     * DESC: 添加一个菜单项
     * Created by jinphy, on 2018/3/31, at 20:52
     */
    public MyMenu item(View item, LinearLayout.LayoutParams layoutParams, OnClick onClick) {
        if (item != null) {
            item.setOnClickListener(v -> {
                if (onClick != null) {
                    onClick.call(this, v);
                }
                if (autoDismiss) {
                    dismiss();
                }
            });
            if (layoutParams != null) {
                container.addView(item, layoutParams);
            } else {
                container.addView(item);
            }
        }
        return this;
    }

    /**
     * DESC: 当菜单开始显示时回调
     * Created by jinphy, on 2018/3/31, at 20:52
     */
    public MyMenu onDisplay(Callback onDisplay) {
        dialog.onDisplay(holder -> onDisplay.call(this));
        return this;
    }

    /**
     * DESC: 当菜单关闭时回调
     * Created by jinphy, on 2018/3/31, at 20:53
     */
    public MyMenu onDismiss(Callback onDismiss) {
        dialog.onDismiss(holder -> onDismiss.call(this));
        return this;
    }

    /**
     * DESC: 显示菜单
     * Created by jinphy, on 2018/3/31, at 20:52
     */
    public MyMenu display() {
        dialog.display();
        return this;
    }

    /**
     * DESC: 关闭菜单
     * Created by jinphy, on 2018/3/31, at 20:51
     */
    public void dismiss() {
        dialog.dismiss();
    }

    /**
     * DESC: 创建一个菜单项
     * Created by jinphy, on 2018/3/31, at 20:51
     */
    private TextView createItem() {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.menu_item, this.container, false);
        return (TextView) view;
    }

    /**
     * DESC: 获取菜单项
     * Created by jinphy, on 2018/3/31, at 20:38
     */
    public View item(int index) {
        if (index < 0 || container.getChildCount() <= index) {
            return null;
        }
        return container.getChildAt(index);
    }


    public interface OnClick{
        void call(MyMenu menu, View item);
    }

    public interface Callback{
        void call(MyMenu menu);
    }
}
