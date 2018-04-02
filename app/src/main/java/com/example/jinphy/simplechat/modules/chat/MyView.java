package com.example.jinphy.simplechat.modules.chat;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.utils.Keyboard;
import com.example.jinphy.simplechat.utils.ScreenUtils;

/**
 * DESC:
 * Created by jinphy on 2018/4/1.
 */

public class MyView {

    private final ChatActivity activity;

    /**
     * DESC: chatFragment 的根布局
     * Created by jinphy, on 2018/4/2, at 8:54
     */
    public final View rootView;

    /**
     * DESC: 顶部布局
     * Created by jinphy, on 2018/4/2, at 8:55
     */
    public final View appBar;

    /**
     * DESC: 顶部布局中的toolbar
     * Created by jinphy, on 2018/4/2, at 8:55
     */
    public final Toolbar toolbar;

    public final RecyclerView recyclerView;

    /**
     * DESC: RecyclerView的布局管理器
     * Created by jinphy, on 2018/4/2, at 8:56
     */
    public final LinearLayoutManager layoutManager;

    /**
     * DESC: 底部总布局
     * Created by jinphy, on 2018/4/2, at 8:55
     */
    public final BottomBar bottomBar;

    /**
     * DESC: 悬浮按钮
     * Created by jinphy, on 2018/4/2, at 8:55
     */
    public final FloatingActionButton fab;

    /**
     * DESC: 动画执行器
     * Created by jinphy, on 2018/4/2, at 8:56
     */
    public Animator animator;


    private boolean isBarVisible = true;


    public static MyView init(ChatActivity activity, View view) {
        return new MyView(activity, view);
    }

    private MyView(ChatActivity activity, View view) {
        this.activity = activity;
        rootView = view;
        appBar = activity.findViewById(R.id.appbar_layout);
        toolbar = activity.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.recycler_view);
        bottomBar = BottomBar.init(view.findViewById(R.id.bottom_bar));
        fab = activity.findViewById(R.id.fab);
        layoutManager = new LinearLayoutManager(activity);
        animator = Animator.init(activity, this);
    }

    /**
     * DESC: 显示语音按钮
     * Created by jinphy, on 2018/4/2, at 8:56
     */
    public void showVoiceBtn() {
        bottomBar.btnVoice.setVisibility(View.VISIBLE);
        bottomBar.btnKeyboard.setVisibility(View.GONE);
    }

    /**
     * DESC: 显示键盘按钮
     * Created by jinphy, on 2018/4/2, at 8:57
     */
    public void showKeyboardBtn() {
        bottomBar.btnVoice.setVisibility(View.GONE);
        bottomBar.btnKeyboard.setVisibility(View.VISIBLE);
    }

    /**
     * DESC: 显示发送按钮
     * Created by jinphy, on 2018/4/2, at 8:57
     */
    public void showSendBtn() {
        bottomBar.btnSend.setVisibility(View.VISIBLE);
        bottomBar.btnMore.setVisibility(View.GONE);
        bottomBar.btnDown.setVisibility(View.GONE);
    }

    /**
     * DESC: 显示"更多"按钮
     * Created by jinphy, on 2018/4/2, at 8:57
     */
    public void showMoreBtn() {
        bottomBar.btnSend.setVisibility(View.GONE);
        bottomBar.btnMore.setVisibility(View.VISIBLE);
        bottomBar.btnDown.setVisibility(View.GONE);
    }

    /**
     * DESC: 显示向下按钮，点击"更多"按钮后显示
     * Created by jinphy, on 2018/4/2, at 8:58
     */
    public void showDownBtn() {
        bottomBar.btnSend.setVisibility(View.GONE);
        bottomBar.btnMore.setVisibility(View.GONE);
        bottomBar.btnDown.setVisibility(View.VISIBLE);
    }

    /**
     * DESC: 显示文本输入框
     * Created by jinphy, on 2018/4/2, at 8:58
     */
    public void showTextInput(MyData myData) {

        hideExtraBottomLayout();

        EditText inputText = bottomBar.textInput;
        inputText.setVisibility(View.VISIBLE);
        inputText.requestFocus();
        Keyboard.open(activity, inputText);
        int position = myData.adapter.getItemCount();
        if (position >= 0) {
            recyclerView.smoothScrollToPosition(position);
        }
        bottomBar.voiceInput.setVisibility(View.GONE);
    }

    /**
     * DESC: 显示语音输入框
     * Created by jinphy, on 2018/4/2, at 8:58
     */
    public void showVoiceInput() {
        hideExtraBottomLayout();
        bottomBar.textInput.setVisibility(View.GONE);
        bottomBar.voiceInput.setVisibility(View.VISIBLE);
    }

    /**
     * DESC: 显示"更多"布局
     * Created by jinphy, on 2018/4/2, at 8:59
     */
    public void showMoreLayout() {
        hideExtraBottomLayout();
        bottomBar.textInput.clearFocus();
        bottomBar.moreMenu.rootView.setVisibility(View.VISIBLE);
        showDownBtn();
    }

    /**
     * DESC: 隐藏“更多”布局
     * Created by jinphy, on 2018/4/2, at 8:59
     */
    public void hideMoreLayout() {
        hideExtraBottomLayout();
        bottomBar.btnDown.setVisibility(View.GONE);
        bottomBar.btnMore.setVisibility(View.VISIBLE);

    }

    /**
     * DESC: 关闭所有额外的底部布局（不包括输入框、按钮等控件的那一栏
     * Created by jinphy, on 2018/4/2, at 9:00
     */
    public void hideExtraBottomLayout() {
        // 关闭底部额外的布局
        bottomBar.moreMenu.rootView.setVisibility(View.GONE);

        // 更新按键的显示
        if (bottomBar.textInput.getText().length() == 0) {
            showMoreBtn();
        } else {
            showSendBtn();
        }

    }


    /**
     * DESC: 显示appBar和bottomBar，同时隐藏fab
     * Created by jinphy, on 2018/4/2, at 9:01
     */
    public void showBar() {
        if (isBarVisible) {
            return;
        }
        synchronized (this) {
            if (isBarVisible) {
                return;
            }
            isBarVisible = true;
        }
        animator.animateBar(1, 0, true);
    }

    /**
     * 隐藏appBar和bottomBar，同时显示fab
     *
     */
    public void hideBar() {
        if (!isBarVisible) {
            return;
        }
        synchronized (this) {
            if (!isBarVisible) {
                return;
            }
            isBarVisible = false;
        }
        animator.animateBar(0, 1, false);
    }

    public boolean isBarShowing() {
        return isBarVisible;
    }

    /**
     * DESC: 处理水平方向的滑动事件
     * Created by jinphy, on 2018/4/2, at 9:03
     */
    public boolean handleHorizontalEvent(MotionEvent event) {
        return animator.handleHorizontalEvent(event);
    }

    /**
     * DESC: 处理竖直方向的滑动事件
     * Created by jinphy, on 2018/4/2, at 9:04
     */
    public boolean handleVerticalEvent(MotionEvent event) {
        return false;
    }

    public void exit() {
        animator.animateHorizontal(0, 1, true);
    }
}
