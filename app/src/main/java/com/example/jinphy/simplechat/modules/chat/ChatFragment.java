package com.example.jinphy.simplechat.modules.chat;

import android.animation.AnimatorSet;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.constants.IntConst;
import com.example.jinphy.simplechat.listener_adapters.TextWatcherAdapter;
import com.example.jinphy.simplechat.utils.AnimUtils;
import com.example.jinphy.simplechat.utils.Keyboard;
import com.example.jinphy.simplechat.utils.Preconditions;
import com.example.jinphy.simplechat.utils.ViewUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment implements ChatContract.View {

    private ChatContract.Presenter presenter;

    private RecyclerView recyclerView;
    private View appbarLayout;
    private View bottomBar;

    private FloatingActionButton fab;
    private FloatingActionButton fabEmotin;
    private View emotionLayout;

    private FrameLayout btnVoiceAndKeyboard;
    private FrameLayout inputTextAndVoice;
    private FrameLayout btnMoreAndSend;


    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ChatFragment.
     */
    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        initView(root);

        initData();

        // Must set to true,if you want to use options menu in the fragment
        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void setPresenter(ChatContract.Presenter presenter) {
        this.presenter = Preconditions.checkNotNull(presenter);
    }

    @Override
    public void initView(View view) {

        // 查找所有需要用到的View
        findViewsById(view);

        // 设置Views
        setupViews();

        // 为需要的View注册各种点击事件
        registerEvent();


    }

    private void findViewsById(View view) {
        appbarLayout = getActivity().findViewById(R.id.appbar_layout);
        fab = getActivity().findViewById(R.id.fab);
        fabEmotin = view.findViewById(R.id.fab_emotion);
        bottomBar = view.findViewById(R.id.bottom_bar);
        recyclerView = view.findViewById(R.id.recycler_view);
        btnVoiceAndKeyboard = view.findViewById(R.id.btn_voice_and_keyboard);
        inputTextAndVoice = view.findViewById(R.id.input_text_and_voice);
        btnMoreAndSend = view.findViewById(R.id.btn_more_and_send);

        emotionLayout = view.findViewById(R.id.emotion_layout);

    }

    private void setupViews(){
        // 设置RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(presenter.getAdapter());
        recyclerView.smoothScrollToPosition(presenter.getItemCount()-1);



        Keyboard.open(getContext(), findInputText());

    }

    private void registerEvent() {

        fab.setOnClickListener(this::fabAction);
        fabEmotin.setOnClickListener(this::fabAction);

        recyclerView.addOnScrollListener(getRecyclerViewListener());
        // 底部栏左边的按钮
        findBtnVoice().setOnClickListener(this::onClickOfBtnVoice);
        findBtnKeyboard().setOnClickListener(this::onClickOfBtnKeyboard);

        // 底部栏中间的文本输入和语音输入
        EditText inputText = findInputText();
        inputText.setOnFocusChangeListener(this::onFocusChangeOfInputText);
        inputText.addTextChangedListener(getTextWatcher());
        findInputVoice().setOnTouchListener(this::onTouchOfInputVoice);

        // 底部栏右边的按钮
        findBtnSend().setOnClickListener(this::onClickOfBtnSend);
        findBtnMore().setOnClickListener(this::onClickOfBtnMore);

    }

    // 声音按钮的点击事件
    private void onClickOfBtnVoice(View view) {
        this.showKeyboardBtn();
        this.showVoiceInput();
    }

    // 键盘按钮的点击事件
    private void onClickOfBtnKeyboard(View view) {
        this.showVoiceBtn();
        this.showTextInput();
    }

    // 文本输入框的焦点改变事件
    private void onFocusChangeOfInputText(View view, boolean hasFocus) {
        if (hasFocus) {
            recyclerView.smoothScrollToPosition(presenter.getItemCount() - 1);
            showFabEmotion();
            hideEmotionLayout();
        } else {
            Keyboard.close(getContext(), findInputText());
            hideFabEmotion();
        }
    }

    // 文本输入框的TextWatcher，监听文本的输入
    private TextWatcher getTextWatcher() {
        return new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    showMoreBtn();
                } else {
                    showSendBtn();
                }
            }
        };
    }


    // 语音输入的触摸事件
    private boolean onTouchOfInputVoice(View view, MotionEvent motionEvent) {
        // TODO: 2017/8/14 录音功能


        return false;
    }

    // 发送按钮的点击事件
    private void onClickOfBtnSend(View view) {

        EditText inputText =  findInputText();
        String content = inputText.getText().toString();

        inputText.setText("");
        // TODO: 2017/8/14 发送消息逻辑


    }

    // 更多功能按钮的点击事件
    private void onClickOfBtnMore(View view) {
        // TODO: 2017/8/14 显示更多的逻辑
    }

    // RecyclerView的滑动事件
    private RecyclerView.OnScrollListener getRecyclerViewListener() {
        return new RecyclerView.OnScrollListener() {
            int total = 0;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dy>0时，向上滑动，反之向下
                total+=dy;
                if (total > 300) {
                    total=0;
                    showBar(recyclerView);
                }
                if (total < -300) {
                    total=0;
                    hideBar(recyclerView);
                }
            }
        };

    }

    @Override
    public void initData() {

    }

    @Override
    public void fabAction(View view) {
        switch (view.getId()) {
            case R.id.fab:
                recyclerView.smoothScrollToPosition(presenter.getItemCount()-1);
                showBar(recyclerView);
                break;
            case R.id.fab_emotion:
                showEmotionLayout();
                break;
            default:break;
        }

    }

    //----------------------------------------------
    private View findParentOfBtnSend() {
        return btnMoreAndSend.findViewById(R.id.send_view_parent);
    }

    private View findBtnSend() {
        return ((CardView) findParentOfBtnSend()).getChildAt(0);
    }

    private View findBtnMore() {
        return btnMoreAndSend.findViewById(R.id.more_view);
    }

    @Override
    public void showSendBtn() {
        findParentOfBtnSend().setVisibility(View.VISIBLE);
        findBtnMore().setVisibility(View.GONE);

    }

    @Override
    public void showMoreBtn() {
        findParentOfBtnSend().setVisibility(View.GONE);
        findBtnMore().setVisibility(View.VISIBLE);
    }



    //----------------------------------------------

    private View findBtnVoice() {
        return btnVoiceAndKeyboard.findViewById(R.id.voice_View);
    }
    private View findBtnKeyboard() {
        return btnVoiceAndKeyboard.findViewById(R.id.keyboard_view);
    }

    @Override
    public void showVoiceBtn() {
        findBtnVoice().setVisibility(View.VISIBLE);
        findBtnKeyboard().setVisibility(View.GONE);
    }

    @Override
    public void showKeyboardBtn() {
        findBtnVoice().setVisibility(View.GONE);
        findBtnKeyboard().setVisibility(View.VISIBLE);

    }


    //----------------------------------------------

    private EditText findInputText() {
        return inputTextAndVoice.findViewById(R.id.input_text);
    }
    private View findInputVoice() {
        return inputTextAndVoice.findViewById(R.id.input_voice);
    }


    @Override
    public void showTextInput() {
        EditText inputText = findInputText();
        inputText.setVisibility(View.VISIBLE);
        inputText.requestFocus();
        Keyboard.open(getContext(),inputText);
        recyclerView.smoothScrollToPosition(presenter.getItemCount()-1);
        findInputVoice().setVisibility(View.GONE);


    }

    @Override
    public void showVoiceInput() {
        findInputText().setVisibility(View.GONE);
        findInputVoice().setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmotionLayout() {
        emotionLayout.setVisibility(View.VISIBLE);
        findInputText().clearFocus();
    }

    @Override
    public void hideEmotionLayout() {
        emotionLayout.setVisibility(View.GONE);
    }

    @Override
    public void showFabEmotion() {
        scaleFabEmotion(0,1,false);
    }

    @Override
    public void hideFabEmotion() {
        scaleFabEmotion(1,0,true);
    }

    private void scaleFabEmotion(float from,float to,final boolean gone) {
        AnimUtils.just(fabEmotin)
                .setScaleX(from,to)
                .setScaleY(from,to)
                .setDuration(IntConst.DURATION_250)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .onStart(a->{if(!gone)fabEmotin.setVisibility(View.VISIBLE);})
                .onEnd(a->{if(gone)fabEmotin.setVisibility(View.GONE);})
                .animate();
    }

    //----------------------------------------------
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chat_fragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_friend:

                break;


        }
        return super.onOptionsItemSelected(item);
    }


    private boolean isBarVisible = true;

    private AnimatorSet animatorSet = null;

    /**
     * 显示toolbar和bottomBar，同时隐藏fab
     *
     * @param view 是一个RecyclerView，传该参数的目的是为了在
     * 移动toolbar和bottomBar时，更新RecyclerView的margin值
     * */
    @Override
    public void showBar(View view) {
        if (!isBarVisible) {
            isBarVisible = true;
            animateBar(view,1,0,true);

        }

    }


    /**
     * 隐藏toolbar和bottomBar，同时显示fab
     *
     * @param view 是一个RecyclerView，传该参数的目的是为了在
     * 移动toolbar和bottomBar时，更新RecyclerView的margin值
     * */
    @Override
    public void hideBar(View view) {
        if (isBarVisible) {
            isBarVisible = false;
            animateBar(view,0,1,false);

        }
    }


    @Override
    public void animateBar(View view, float fromValue, float toValue, boolean showBar) {

        if (animatorSet != null && animatorSet.isRunning()) {
            animatorSet.end();
        }

        int appbarHeight = appbarLayout.getHeight();
        int bottomBarHeight = bottomBar.getMeasuredHeight();


        animatorSet = AnimUtils.just()
                .setFloat(fromValue,toValue)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(IntConst.DURATION_500)
                .onStart(animator ->{
                    if (showBar) {
                        appbarLayout.setVisibility(View.VISIBLE);
                        bottomBar.setVisibility(View.VISIBLE);
                    } else {
                        fab.setVisibility(View.VISIBLE);
                    }
                } )
                .onUpdateFloat(animator -> {
                    float value = (float) animator.getAnimatedValue();
                    float marginTop = appbarHeight * (1-value);
                    float marginBottom = bottomBarHeight * (1 - value);
                    appbarLayout.setTranslationY(value * (-appbarHeight));
                    bottomBar.setTranslationY(value * bottomBarHeight);
                    ViewUtils.setScaleXY(fab,value);
                    setMargin(view,marginTop,marginBottom);
                })
                .onEnd(animator -> {
                    if (showBar) {
                        fab.setVisibility(View.GONE);
                    } else {
                        appbarLayout.setVisibility(View.GONE);
                        bottomBar.setVisibility(View.GONE);
                    }
                })
                .build();
        animatorSet.start();
    }

    //设置View的margin，用在移动toolbar和bottomBar时改变其他View
    //的margin
    private void setMargin(View view, float marginTop,float marginBottom) {
        if (view == null) {
            return;
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(view.getLayoutParams());
        lp.setMargins(lp.leftMargin, (int) marginTop,lp.rightMargin, (int) marginBottom);
        view.setLayoutParams(lp);
        view.requestLayout();
    }
}
