package com.example.jinphy.simplechat.modules.main.self;

import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;

/**
 * DESC:
 * Created by jinphy on 2018/4/3.
 */

public class SelfView {

    public final ScrollView scrollView;
    public final CardView headView;
    public final ImageView avatarView;
    public final TextView nameText;
    public final TextView dateText;
    public final ImageView sexView;
    public final TextView btnLogout;
    public final ImageView blurBackground;

    public final SelfMenu menu;

    private SelfAnimator animator;

    private SelfView(SelfFragment fragment, View view) {
        scrollView = view.findViewById(R.id.scroll_view);
        headView = view.findViewById(R.id.head_view);
        avatarView = headView.findViewById(R.id.avatar);
        nameText = headView.findViewById(R.id.name);
        dateText = headView.findViewById(R.id.date);
        sexView = headView.findViewById(R.id.sex);
        btnLogout = view.findViewById(R.id.btn_logout);
        blurBackground = headView.findViewById(R.id.blur_background);

        menu = SelfMenu.create(view.findViewById(R.id.menu_view));

        animator = SelfAnimator.init(fragment.getContext(), this, fragment);
    }

    public static SelfView init(SelfFragment fragment, View view) {
        return new SelfView(fragment, view);
    }


    public boolean handleVerticalTouchEvent(MotionEvent event){
        return animator.handleVerticalTouchEvent(event);
    }

    public void onViewPagerScrolled(int position, float offset, int offsetPixels) {
        animator.onViewPagerScrolled(position, offset, offsetPixels);
    }
}
