package com.example.jinphy.simplechat.modules.chat;

import android.view.View;

import com.example.jinphy.simplechat.R;

/**
 * DESC:
 * Created by jinphy on 2018/3/20.
 */

public class BottomMoreMenu {

    /**
     * DESC: 底部更多菜单的布局
     * Created by jinphy, on 2018/3/20, at 10:35
     */
    public final View rootView;

    /**
     * DESC: 菜单项：图片
     * Created by jinphy, on 2018/3/20, at 10:36
     */
    public final View photoItem;

    public final View fileItem;

    public static BottomMoreMenu init(View bottomMore) {
        return new BottomMoreMenu(bottomMore);
    }


    private BottomMoreMenu(View bottomMore) {
        this.rootView = bottomMore;
        photoItem = bottomMore.findViewById(R.id.item_photo);
        fileItem = bottomMore.findViewById(R.id.item_file);
    }
}
