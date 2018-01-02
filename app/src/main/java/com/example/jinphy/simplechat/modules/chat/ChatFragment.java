package com.example.jinphy.simplechat.modules.chat;

import android.animation.AnimatorSet;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.constants.IntConst;
import com.example.jinphy.simplechat.listener_adapters.TextListener;
import com.example.jinphy.simplechat.utils.AnimUtils;
import com.example.jinphy.simplechat.utils.ColorUtils;
import com.example.jinphy.simplechat.utils.Keyboard;
import com.example.jinphy.simplechat.utils.ScreenUtils;
import com.example.jinphy.simplechat.utils.ViewUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an app of this fragment.
 */
public class ChatFragment extends BaseFragment<ChatPresenter> implements ChatContract.View {

    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;

    private RecyclerView recyclerView;
    private View appbarLayout;
    private View bottomBar;
    private Toolbar toolbar;

    private FloatingActionButton fab;
    private FloatingActionButton fabEmotion;

    private FrameLayout btnVoiceAndKeyboard;
    private FrameLayout inputTextAndVoice;
    private FrameLayout btnMoreAndSend;
    private FrameLayout extraBottomLayout;

    // TODO: 2017/8/15 隐藏appBar时 statusBar 的初始颜色，从好友头像获取
    int startStatusColor;
    // 隐藏 appBar 后的statusBar的 最终颜色，为colorAccent
    int endStatusColor;


    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_chat;
    }

    /**
     * Use this factory method to create a new app of
     * this fragment using the provided parameters.
     * @return A new app of fragment ChatFragment.
     */
    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }


    @Override
    protected void findViewsById(View view) {
        toolbar = getActivity().findViewById(R.id.toolbar);
        appbarLayout = getActivity().findViewById(R.id.appbar_layout);
        fab = getActivity().findViewById(R.id.fab);
        fabEmotion = view.findViewById(R.id.fab_emotion);
        bottomBar = view.findViewById(R.id.bottom_bar);
        recyclerView = view.findViewById(R.id.recycler_view);
        btnVoiceAndKeyboard = view.findViewById(R.id.btn_voice_and_keyboard);
        inputTextAndVoice = view.findViewById(R.id.input_text_and_voice);
        btnMoreAndSend = view.findViewById(R.id.btn_more_and_send);
        extraBottomLayout = view.findViewById(R.id.extra_bottom_layout);

    }

    @Override
    protected void setupViews(){

        appbarLayout.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));

        // 设置RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(presenter.getAdapter());
        recyclerView.smoothScrollToPosition(presenter.getItemCount()-1);

        // TODO: 2017/8/15 根据好友的头像设置appbar颜色和statusBar颜色

        // TODO: 2017/8/15 这个要放在数据获取完成后才执行
        Keyboard.open(getContext(), findInputText());

    }

    @Override
    protected void registerEvent() {

        fab.setOnClickListener(this::fabAction);
        fabEmotion.setOnClickListener(this::fabAction);

        recyclerView.addOnScrollListener(getRecyclerViewListener());
        // 底部栏左边的按钮
        findBtnVoice().setOnClickListener(this::onClickOfBtnVoice);
        findBtnKeyboard().setOnClickListener(this::onClickOfBtnKeyboard);

        // 底部栏中间的文本输入和语音输入
        EditText inputText = findInputText();
        inputText.setOnFocusChangeListener(this::onFocusChangeOfInputText);
        inputText.addTextChangedListener((TextListener.After) editable -> {
            if (editable.length() == 0) {
                showMoreBtn();
            } else {
                showSendBtn();
            }
        });
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
        Log.e(getClass().getSimpleName(), "height" + fabEmotion.getHeight());
        if (hasFocus) {
            hideExtraBottomLayout();
            showFabEmotion();
        } else {
            Keyboard.close(getContext(), findInputText());
            hideFabEmotion();
        }
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
        showMoreLayout();
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
    protected void initData() {
        screenWidth = ScreenUtils.getScreenWidth(getContext());
        onThirdScreenWidth = screenWidth/3;
        maxElevation = ScreenUtils.dp2px(getContext(), 20);
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
        hideExtraBottomLayout();
        EditText inputText = findInputText();
        inputText.setVisibility(View.VISIBLE);
        inputText.requestFocus();
        Keyboard.open(getContext(),inputText);
        recyclerView.smoothScrollToPosition(presenter.getItemCount()-1);
        findInputVoice().setVisibility(View.GONE);

    }

    @Override
    public void showVoiceInput() {
        hideExtraBottomLayout();
        findInputText().setVisibility(View.GONE);
        findInputVoice().setVisibility(View.VISIBLE);
    }


    //----------------------------------------------

    private View findEmotionLayout() {
        return extraBottomLayout.findViewById(R.id.emotion_layout);
    }

    private View findMoreLayout() {
        return extraBottomLayout.findViewById(R.id.more_layout);
    }


    @Override
    public void showEmotionLayout() {
        hideExtraBottomLayout();
        findEmotionLayout().setVisibility(View.VISIBLE);
        findInputText().clearFocus();
    }

    @Override
    public void hideEmotionLayout() {
        findEmotionLayout().setVisibility(View.GONE);
    }

    @Override
    public void showMoreLayout() {
        hideExtraBottomLayout();
        findMoreLayout().setVisibility(View.VISIBLE);
        findInputText().clearFocus();
    }

    @Override
    public void hideMoreLayout() {

    }

    @Override
    public void hideExtraBottomLayout() {
        int count = extraBottomLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            extraBottomLayout.getChildAt(i).setVisibility(View.GONE);
        }
    }


    //----------------------------------------------

    @Override
    public void showFabEmotion() {
        scaleFabEmotion(0,1,false);
    }

    @Override
    public void hideFabEmotion() {
        scaleFabEmotion(1,0,true);
    }

    private void scaleFabEmotion(float from,float to,final boolean gone) {
        AnimUtils.just(fabEmotion)
                .setScaleX(from,to)
                .setScaleY(from,to)
                .setDuration(IntConst.DURATION_250)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .onStart(a->{if(!gone) fabEmotion.setVisibility(View.VISIBLE);})
                .onEnd(a->{
                    if (gone) {
                        fabEmotion.setVisibility(View.GONE);
                    } else {
                        setMargin(
                                recyclerView,
                                appbarLayout.getMeasuredHeight(),
                                bottomBar.getMeasuredHeight());
                        recyclerView.smoothScrollToPosition(presenter.getItemCount() - 1);
                    }
                })
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
            case android.R.id.home:
                onBackPressed();
                break;
            default:
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
            fabEmotion.setVisibility(View.GONE);
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
                    // TODO: 2017/8/15 设置statusBar的颜色
                    //setStatusBarColor(value);
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


    @Override
    public void setStatusBarColor(float factor) {
        int color = ColorUtils.rgbColorByFactor(startStatusColor, endStatusColor, factor);
        ScreenUtils.setStatusBarColor(getActivity(),color);
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

    private int moveOrientation;
    private float oldX;
    private float deltaX;
    private float downX;
    private float onThirdScreenWidth;
    private float screenWidth;
    private int maxElevation;

    @Override
    public boolean handleHorizontalTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                oldX = downX;
                return false;
            case MotionEvent.ACTION_MOVE:
                Log.e(getClass().getSimpleName(), "move");
                moveOrientation = HORIZONTAL;
                deltaX = event.getX()-oldX;
                oldX = event.getX();
                if (canMoveHorizontal()) {
                    float factor = getHorizontalMoveFactor(deltaX);
                    moveHorizontal(factor);
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (moveOrientation == HORIZONTAL) {
                    float factor  = rootView.getTranslationX()/screenWidth;
                    if (factor < 1.0f / 3) {
                        animateHorizontal(factor,0,false);
                    } else {
                        animateHorizontal(factor,1f,true);
                    }
                }

                return false;
            default:
                return false;
        }

    }

    private boolean canMoveHorizontal() {
        return (downX < onThirdScreenWidth) && (rootView.getTranslationX()>=0);
    }

    @Override
    public void moveHorizontal(float factor) {
        float transX = factor * screenWidth;
        rootView.setTranslationX(transX);
        appbarLayout.setTranslationX(transX);
        recyclerView.setAlpha(1-factor);
        toolbar.setAlpha((1-factor));
        bottomBar.setAlpha(1-factor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rootView.setElevation((float) (maxElevation *(1-factor*0.5)));
        }
    }

    private float getHorizontalMoveFactor(float deltaX) {
        float transX = rootView.getTranslationX();
        transX +=deltaX;

        if (deltaX < 0 && transX<0) {
            // 向左滑动
            transX = 0;
        }

        float factor = transX / screenWidth;

        return factor;
    }

    @Override
    public void animateHorizontal(float fromFactor, float toFactor,boolean exit) {
        float deltaFactor = Math.abs(toFactor-fromFactor);
        AnimUtils.just()
                .setFloat(fromFactor,toFactor)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration((long) (IntConst.DURATION_500 * deltaFactor))
                .onUpdateFloat(animator -> {
                    float factor = (float) animator.getAnimatedValue();
                    moveHorizontal(factor);
                })
                .onEnd(animator -> {
                    if (exit) {
                        Keyboard.close(getContext(),findInputText());
                        getActivity().finish();
                    }
                })
                .animate();
    }

    @Override
    public boolean onBackPressed() {
        animateHorizontal(0,1,true);
        return false;
    }
}
