package com.example.jinphy.simplechat.modules.main;

import android.app.Activity;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.custom_view.MenuItemView;
import com.example.jinphy.simplechat.utils.ScreenUtils;

/**
 * DESC:
 * Created by jinphy on 2018/4/3.
 */

public class MainMenu {

    public final View view;

    private PopupWindow popupWindow;

    public final MenuItemView[] items = new MenuItemView[4];


    private MainMenu(View view) {
        this.view = view;
        items[0] = view.findViewById(R.id.item_add_friend);
        items[1] = view.findViewById(R.id.item_search_group);
        items[2] = view.findViewById(R.id.item_group_chat);
        items[3] = view.findViewById(R.id.item_scan);
        this.view.setOnClickListener(v -> dismiss());
    }

    public static MainMenu create(View view) {
        return new MainMenu(view);
    }

    public void onClick(OnClick onClick) {
        if (onClick == null) {
            return;
        }
        for (int i = 0; i < items.length; i++) {
            int index = i;
            items[i].onClick((menuItemView, hasFocus) -> {
                onClick.call(index);
                popupWindow.dismiss();
            });
        }
    }

    public void show(Activity activity,View anchor) {
        if (popupWindow != null) {
            popupWindow.dismiss();
            return;
        }
        popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setAnimationStyle(R.style.main_menu_animate);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(ScreenUtils.dp2px(activity,6));
            popupWindow.showAsDropDown(
                    anchor,
                    ScreenUtils.dp2px(activity,-5),
                    0,
                    Gravity.END);
        }
        popupWindow.setOnDismissListener(() -> popupWindow = null);
    }

    public boolean dismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            return true;
        }
        return false;
    }


    public interface OnClick{
        void call(int index);
    }
}
