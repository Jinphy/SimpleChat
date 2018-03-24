package com.example.jinphy.simplechat.modules.chat;

import android.view.View;

import com.example.jinphy.simplechat.R;

import me.iwf.photopicker.PhotoPicker;

/**
 * DESC:
 * Created by jinphy on 2018/3/20.
 */

public class BottomMoreMenu {

    private ChatFragment fragment;
    /**
     * DESC: 底部更多菜单的布局
     * Created by jinphy, on 2018/3/20, at 10:35
     */
    public View bottomMore;

    /**
     * DESC: 菜单项：图片
     * Created by jinphy, on 2018/3/20, at 10:36
     */
    public View photoItem;


    public BottomMoreMenu(ChatFragment fragment, View bottomMore) {
        this.fragment = fragment;
        this.bottomMore = bottomMore;
        photoItem = bottomMore.findViewById(R.id.item_photo);

        registerEvent();
    }


    public void clearFragment() {
        this.fragment = null;
    }

    /**
     * DESC: 注册监听器
     *
     * Created by jinphy, on 2018/3/20, at 10:49
     */
    private void registerEvent() {
        photoItem.setOnClickListener(v -> {
            PhotoPicker.builder()
                    .setPhotoCount(9)
                    .setShowCamera(true)
                    .setShowGif(true)
                    .setPreviewEnabled(true)
                    .start(fragment.activity(), PhotoPicker.REQUEST_CODE);
        });
    }

}
