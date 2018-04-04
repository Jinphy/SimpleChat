package com.example.jinphy.simplechat.modules.main.self;

import android.view.View;
import android.view.ViewGroup;

/**
 * DESC:
 * Created by jinphy on 2018/4/3.
 */

public class SelfMenu {

    public final View view;

    public final View[] items;

    private SelfMenu(ViewGroup view) {
        this.view = view;
        items = new View[view.getChildCount()];
        for (int i = 0; i < items.length; i++) {
            items[i] = view.getChildAt(i);
        }
    }

    public static SelfMenu create(ViewGroup view) {
        return new SelfMenu(view);
    }

    public void onClick(OnClick onClick) {
        if (onClick == null) {
            return;
        }
        for (int i = 0; i < items.length; i++) {
            int index = i;
            items[i].setOnClickListener(v -> onClick.call(index));
        }
    }


    public interface OnClick{
        void call(int index);
    }
}
