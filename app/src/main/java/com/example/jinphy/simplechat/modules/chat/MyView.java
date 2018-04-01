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

    public final View rootView;

    public final View appBar;

    public final Toolbar toolbar;

    public final RecyclerView recyclerView;

    public final BottomBar bottomBar;

    public final FloatingActionButton fab;


    public final LinearLayoutManager layoutManager;

    public final int startStatusBarColor;

    public final int endStatusBarColor;

    public final int screenWidth;

    public final int screenWidthFor1Of3;
    public final int maxElevation;

    public int recyclerViewScrollY;


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
        startStatusBarColor = activity.colorPrimaryDark();
        endStatusBarColor = activity.colorAccent();
        screenWidth = ScreenUtils.getScreenWidth(activity);
        screenWidthFor1Of3 = screenWidth / 3;
        maxElevation = ScreenUtils.dp2px(activity, 20);
    }


    public void showVoiceBtn() {
        bottomBar.btnVoice.setVisibility(View.VISIBLE);
        bottomBar.btnKeyboard.setVisibility(View.GONE);
    }

    public void showKeyboardBtn() {
        bottomBar.btnVoice.setVisibility(View.GONE);
        bottomBar.btnKeyboard.setVisibility(View.VISIBLE);
    }


    public void showSendBtn() {
        bottomBar.btnSend.setVisibility(View.VISIBLE);
        bottomBar.btnMore.setVisibility(View.GONE);
        bottomBar.btnDown.setVisibility(View.GONE);
    }

    public void showMoreBtn() {
        bottomBar.btnSend.setVisibility(View.GONE);
        bottomBar.btnMore.setVisibility(View.VISIBLE);
        bottomBar.btnDown.setVisibility(View.GONE);
    }

    public void showDownBtn() {
        bottomBar.btnSend.setVisibility(View.GONE);
        bottomBar.btnMore.setVisibility(View.GONE);
        bottomBar.btnDown.setVisibility(View.VISIBLE);
    }


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

    public void showVoiceInput() {
        hideExtraBottomLayout();
        bottomBar.textInput.setVisibility(View.GONE);
        bottomBar.voiceInput.setVisibility(View.VISIBLE);
    }


    public void showMoreLayout() {
        hideExtraBottomLayout();
        bottomBar.textInput.clearFocus();
        bottomBar.moreMenu.rootView.setVisibility(View.VISIBLE);
    }

    public void hideMoreLayout() {
        bottomBar.moreMenu.rootView.setVisibility(View.GONE);
    }


    public void hideExtraBottomLayout() {
        bottomBar.moreMenu.rootView.setVisibility(View.GONE);
    }

    public boolean handleHorizontalEvent(MotionEvent event) {

        return true;
    }

    public boolean handleVerticalEvent(MotionEvent event) {

        return true;
    }

}
