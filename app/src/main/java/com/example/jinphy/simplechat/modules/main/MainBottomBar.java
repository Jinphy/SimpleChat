package com.example.jinphy.simplechat.modules.main;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;

/**
 * DESC:
 * Created by jinphy on 2018/4/3.
 */

public class MainBottomBar {

    public final View view;

    private Item[] items = new Item[4];

    private Item checkedItem;

    private int checkedIndex;

    public static MainBottomBar create(View view) {
        return new MainBottomBar(view);
    }

    private MainBottomBar(View view) {
        this.view = view;
        // 消息
        items[0] = Item.create(
                view.findViewById(R.id.btn_msg),
                R.drawable.ic_msg_close_24dp,
                R.drawable.ic_msg_open_24dp);
        // 朋友
        items[1] = Item.create(
                view.findViewById(R.id.btn_friends),
                R.drawable.ic_friends_close_24dp,
                R.drawable.ic_friends_open_24dp);
        // 日常
        items[2] = Item.create(
                view.findViewById(R.id.btn_routine),
                R.drawable.ic_routine_close_24dp,
                R.drawable.ic_routine_open_24dp);
        // 自己
        items[3] = Item.create(
                view.findViewById(R.id.btn_self),
                R.drawable.ic_self_close_24dp,
                R.drawable.ic_self_open_24dp);

        items[0].setCheck(true);
        checkedItem = items[0];
    }

    public void onClick(OnClick onClick) {
        if (onClick == null) {
            return;
        }
        for (int i = 0; i < items.length; i++) {
            final int index = i;
            items[i].view.setOnClickListener(v -> {
                onClick.call(index);
                checkItem(index);
            });
        }
    }


    /**
     * DESC: 选择
     * Created by jinphy, on 2018/4/3, at 14:06
     */
    public void checkItem(int position) {
        if (checkedItem != items[position]) {
            checkedItem.setCheck(false);
            checkedItem = items[position];
            checkedItem.setCheck(true);
            checkedIndex = position;
        }
    }

    public int getCheckedIndex() {
        return checkedIndex;
    }



    public interface OnClick{
        void call(int index);
    }


    public static class Item{

        private final int normalIcon;

        private final int checkedIcon;

        private final int normalColor = ContextCompat.getColor(App.app(), R.color.half_alpha_gray);

        private final int checkedColor = ContextCompat.getColor(App.app(), R.color.colorAccent);

        public final View view;

        public final ImageView icon;

        public final TextView tag;


        public static Item create(ViewGroup view, int normalIcon, int checkedIcon) {
            return new Item(view, normalIcon, checkedIcon);
        }

        private Item(ViewGroup view, int normalIcon, int checkIcon) {
            this.view = view;
            this.normalIcon = normalIcon;
            this.checkedIcon = checkIcon;

            this.icon = (ImageView) view.getChildAt(0);
            this.tag = (TextView) view.getChildAt(1);
            this.setCheck(false);
        }

        public void setCheck(boolean flag) {
            if (flag) {
                this.icon.setImageResource(this.checkedIcon);
                this.tag.setTextColor(this.checkedColor);
            } else {
                this.icon.setImageResource(this.normalIcon);
                this.tag.setTextColor(this.normalColor);
            }
        }
    }
}
