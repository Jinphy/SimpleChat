package com.example.jinphy.simplechat.modules.chat.models;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.modules.chat.ChatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/20.
 */

public class BottomExtraView {

    /**
     * DESC: 底部更多菜单的布局
     * Created by jinphy, on 2018/3/20, at 10:35
     */
    public final FrameLayout rootView;

    public final RecyclerView moreMenu;

    public static BottomExtraView init(ChatActivity activity, View bottomMore) {
        return new BottomExtraView(activity, bottomMore);
    }


    private BottomExtraView(ChatActivity activity, View bottomMore) {
        this.rootView = (FrameLayout) bottomMore;
        moreMenu = rootView.findViewById(R.id.more_menu);
        moreMenu.setLayoutManager(new GridLayoutManager(activity, 4));
    }


    public static class MenuItem{

        public final int icon;

        public final String tag;

        private MenuItem(int icon, String tag) {
            this.icon = icon;
            this.tag = tag;
        }

        public static List<MenuItem> create() {
            int[] icons = {
                    R.drawable.ic_photo_24dp,
                    R.drawable.ic_folder_2_24dp
            };
            String[] tags = {
                    "图片",
                    "文件"
            };
            List<MenuItem> items = new ArrayList<>(icons.length);
            for (int i = 0; i < icons.length; i++) {
                items.add(new MenuItem(icons[i], tags[i]));
            }
            return items;
        }
    }
}
