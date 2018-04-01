package com.example.jinphy.simplechat.modules.chat;

import android.animation.AnimatorSet;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.constants.IntConst;
import com.example.jinphy.simplechat.custom_libs.AudioPlayer;
import com.example.jinphy.simplechat.custom_libs.AudioRecorder;
import com.example.jinphy.simplechat.custom_view.AudioRecordButton;
import com.example.jinphy.simplechat.listener_adapters.TextListener;
import com.example.jinphy.simplechat.models.event_bus.EBSendMsg;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.modules.group.group_detail.ModifyGroupActivity;
import com.example.jinphy.simplechat.modules.modify_friend_info.ModifyFriendInfoActivity;
import com.example.jinphy.simplechat.models.event_bus.EBMessage;
import com.example.jinphy.simplechat.modules.show_file.ShowFileActivity;
import com.example.jinphy.simplechat.modules.show_photo.ShowPhotoActivity;
import com.example.jinphy.simplechat.services.common_service.aidl.models.OnUpdate;
import com.example.jinphy.simplechat.utils.AnimUtils;
import com.example.jinphy.simplechat.utils.ColorUtils;
import com.example.jinphy.simplechat.utils.DeviceUtils;
import com.example.jinphy.simplechat.utils.Keyboard;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.example.jinphy.simplechat.utils.ScreenUtils;
import com.example.jinphy.simplechat.utils.ViewUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an app of this fragment.
 */
public class ChatFragment extends BaseFragment<ChatPresenter> implements ChatContract.View {

    public static final String SAVE_KEY_WITH_ACCOUNT = "SAVE_KEY_WITH_ACCOUNT";

    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;

    private RecyclerView recyclerView;
    private View appbarLayout;
    private View bottomBar;
    private Toolbar toolbar;

    private FloatingActionButton fab;

    private RelativeLayout btnVoiceAndKeyboard;
    private RelativeLayout inputTextAndVoice;
    private RelativeLayout btnMoreAndSend;
    private FrameLayout extraBottomLayout;

    private Friend friend;
    private Group group;
    private Member selfMember;// 群聊成员自己
    private BottomMoreMenu bottomMoreMenu;

    int startStatusColor;
    // 隐藏 appBar 后的statusBar的 最终颜色，为colorAccent
    int endStatusColor;
    private String withAccount;
    private String ownerAccount;
    private ChatAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private boolean isFriend;


    /**
     * DESC: 缓存正在发送的或者正在下载语音信息的消息
     * Created by jinphy, on 2018/3/26, at 9:29
     */
    private Map<Long,Message> msgMap;


    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            withAccount = savedInstanceState.getString(SAVE_KEY_WITH_ACCOUNT);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_KEY_WITH_ACCOUNT, withAccount);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.updateRecord(adapter.getLast());
        EventBus.getDefault().post(new EBUpdateView());
        bottomMoreMenu.clearFragment();
        AudioRecorder.getInstance().release();
        AudioPlayer.getInstance().release();
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_chat;
    }

    /**
     * Use this factory method to create a new app of
     * this fragment using the provided parameters.
     *
     * @return A new app of fragment ChatFragment.
     */
    public static ChatFragment newInstance(String withAccount) {
        ChatFragment fragment = new ChatFragment();
        fragment.withAccount = withAccount;
        return fragment;
    }

    @Override
    public boolean isFriend() {
        return isFriend;
    }

    @Override
    protected void findViewsById(View view) {
        toolbar = getActivity().findViewById(R.id.toolbar);
        appbarLayout = getActivity().findViewById(R.id.appbar_layout);
        fab = getActivity().findViewById(R.id.fab);
        bottomBar = view.findViewById(R.id.bottom_bar);
        recyclerView = view.findViewById(R.id.recycler_view);
        btnVoiceAndKeyboard = view.findViewById(R.id.btn_voice_and_keyboard);
        inputTextAndVoice = view.findViewById(R.id.input_text_and_voice);
        btnMoreAndSend = view.findViewById(R.id.btn_more_and_send);
        extraBottomLayout = view.findViewById(R.id.extra_bottom_layout);
        bottomMoreMenu = new BottomMoreMenu(this, extraBottomLayout.findViewById(R.id.more_layout));
    }

    @Override
    protected void initData() {
        ownerAccount = presenter.getOwner();
        if (ObjectHelper.isTrimEmpty(withAccount)) {
            App.showToast("数据异常！", false);
            finishActivity();
            return;
        }
        isFriend = !withAccount.contains("G");
        startStatusColor = colorPrimaryDark();
        endStatusColor = colorAccent();

        screenWidth = ScreenUtils.getScreenWidth(getContext());
        onThirdScreenWidth = screenWidth / 3;
        maxElevation = ScreenUtils.dp2px(getContext(), 20);
        msgMap = new ConcurrentHashMap<>();

        if (withAccount.contains("G")) {
            group = presenter.getGroup(withAccount);
            selfMember = presenter.getSelfMember(withAccount);
        } else {
            friend = presenter.getFriend(withAccount);
        }
    }


    @Override
    protected void setupViews() {
        appbarLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        if (withAccount.contains("G")) {
            activity().setTitle(group.getName());
        } else {
            activity().setTitle(friend.getShowName());
        }

        // 设置RecyclerView
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ChatAdapter(presenter.getUserAvatar(), withAccount);
        recyclerView.setAdapter(adapter);

        Observable.just(withAccount)
                .subscribeOn(Schedulers.io())
                .map(with -> presenter.loadMessages(with))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(messages -> {
                    adapter.update(messages);
                    if (!isFriend) {
                        adapter.setGroup(group);
                        adapter.setMembers(presenter.loadMembers(withAccount));
                    }

                    int position = adapter.getItemCount() - 1;
                    if (position >= 0) {
                        recyclerView.scrollToPosition(position);
                    }
                })
                .subscribe();
    }


    @Override
    public void updateView() {
        int last = linearLayoutManager.findLastVisibleItemPosition();
        int size = adapter.getItemCount();
        int scrollY = recyclerView.getScrollY();

        Observable.<List<Message>>create(e -> {
            e.onNext(presenter.loadNewMessages(withAccount));
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .map(newMessages -> newMessages.toArray(new Message[newMessages.size()]))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(newMessages -> {
                    adapter.add(newMessages);
                })
                .doOnComplete(()->{
                    if (size - last < 5) {
                        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                    } else {
                        recyclerView.scrollBy(0, scrollY);
                    }
                })
                .subscribe();
    }

    @Override
    protected void registerEvent() {
        fab.setOnClickListener(this::fabAction);

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

        // 底部栏右边的按钮
        findSendBtn().setOnClickListener(this::onClickOfBtnSend);
        findMoreBtn().setOnClickListener(this::onClickOfBtnMore);

        // 录音按键的录音事件
        ((AudioRecordButton) findInputVoice()).onRecordFinished((filePath, duration) -> {
            // 发送语音消息
            Message message = Message.makeVoiceMsg(ownerAccount, withAccount, filePath, duration,isFriend);
            presenter.sendFileMsg(message);
            adapter.add(message);
        });

        // 底部更多功能的点击事件
        bottomMoreMenu.registerEvent();

        adapter.onClick((view, item, type, position) -> {
            switch (item.getContentType()) {
                case Message.TYPE_CHAT_IMAGE:{
                    if (!adapter.hasPhoto(item)) {
                        App.showToast("图片不存在！", false);
                        return;
                    }
                    ShowPhotoActivity.start(activity(), item.getId(), position);
                    break;
                }
                case Message.TYPE_CHAT_VOICE:{
                    String filePath = item.getFilePath();
                    String audioStatus = item.getAudioStatus();
                    if (Message.AUDIO_STATUS_NO.equals(audioStatus)) {
                        App.showToast("语音不存在！", false);
                        return;
                    }
                    if (Message.AUDIO_STATUS_DOWNLOADING.equals(audioStatus)) {
                        App.showToast("正在加载！", false);
                        return;
                    }
                    if (Message.AUDIO_STATUS_NEW.equals(audioStatus)) {
                        item.updateAudioStatus(Message.AUDIO_STATUS_OLD);
                        presenter.updateMsg(item);
                        updateRecyclerView();
                    }
                    if (filePath != null) {
                        AudioPlayer.playOrStop(filePath);
                    }
                    break;
                }
                case Message.TYPE_CHAT_FILE:{
                    if (!adapter.hasFile(item)) {
                        App.showToast("文件不存在！", false);
                        return;
                    }
                    ShowFileActivity.start(activity(), item.getId(), position);
                    break;
                }
                default:
                    break;

            }
        });

        appbarLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int
                    oldLeft, int oldTop, int oldRight, int oldBottom) {
                App.showToast("top:" + top, false);
            }
        });
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
            hideExtraBottomLayout();
        } else {
            Keyboard.close(getContext(), findInputText());
        }
    }


    // 发送按钮的点击事件
    private void onClickOfBtnSend(View view) {
        if (isFriend) {
            switch (friend.getStatus()) {
                case Friend.status_black_listed:
                    App.showToast("您已被对方拉入黑名单，不能发送消息！", false);
                    return;
                case Friend.status_deleted:
                    App.showToast("对方不是好友，不能发送消息！", false);
                    return;
            }
        } else {
            if (!group.isMyGroup()) {
                App.showToast("您非本群成员，不能发送信息！", false);
                return;
            }
            if (!selfMember.isAllowChat()) {
                App.showToast("您已被群主禁言，不能发送信息！", false);
                return;
            }
        }
        EditText inputText = findInputText();
        String content = inputText.getText().toString();
        inputText.setText("");
        Message message = Message.makeTextMsg(ownerAccount, withAccount, content, isFriend);
        adapter.add(message);
        presenter.sendTextMsg(message);
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
                total += dy;
                if (total > 300) {
                    total = 0;
                    showBar(recyclerView);
                }
                if (total < -300) {
                    total = 0;
                    hideBar(recyclerView);
                }
            }
        };

    }

    @Override
    public void fabAction(View view) {
        switch (view.getId()) {
            case R.id.fab:
                int position = adapter.getItemCount();
                if (position >= 0) {
                    recyclerView.smoothScrollToPosition(position);
                }
                showBar(recyclerView);
                break;
            default:
                break;
        }

    }

    //----------------------------------------------
    private View findSendBtn() {
        return btnMoreAndSend.findViewById(R.id.send_view);
    }


    private View findMoreBtn() {
        return btnMoreAndSend.findViewById(R.id.more_view);
    }

    @Override
    public void showSendBtn() {
        findSendBtn().setVisibility(View.VISIBLE);
        findMoreBtn().setVisibility(View.GONE);
    }

    @Override
    public void showMoreBtn() {
        findSendBtn().setVisibility(View.GONE);
        findMoreBtn().setVisibility(View.VISIBLE);
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
        Keyboard.open(getContext(), inputText);
        int position = adapter.getItemCount();
        if (position >= 0) {
            recyclerView.smoothScrollToPosition(position);
        }
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
    protected void onKeyboardEvent(boolean open) {
        if (!open) {

        }
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
        findInputText().clearFocus();
        findMoreLayout().setVisibility(View.VISIBLE);
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

    /**
     * DESC: 创建菜单
     * Created by jinphy, on 2018/1/8, at 20:52
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chat_fragment, menu);
    }

    // 菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_friend:
                if (withAccount.contains("G")) {
                    ModifyGroupActivity.start(activity(), withAccount);
                } else {
                    ModifyFriendInfoActivity.start(activity(), withAccount);
                }
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
     *             移动toolbar和bottomBar时，更新RecyclerView的margin值
     */
    @Override
    public void showBar(View view) {
        if (!isBarVisible) {
            isBarVisible = true;
            animateBar(view, 1, 0, true);

        }

    }


    /**
     * 隐藏toolbar和bottomBar，同时显示fab
     *
     * @param view 是一个RecyclerView，传该参数的目的是为了在
     *             移动toolbar和bottomBar时，更新RecyclerView的margin值
     */
    @Override
    public void hideBar(View view) {
        if (isBarVisible) {
            isBarVisible = false;
            animateBar(view, 0, 1, false);
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
                .setFloat(fromValue, toValue)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(IntConst.DURATION_500)
                .onStart(animator -> {
                    if (showBar) {
                        appbarLayout.setVisibility(View.VISIBLE);
                        bottomBar.setVisibility(View.VISIBLE);
                    } else {
                        fab.setVisibility(View.VISIBLE);
                    }
                })
                .onUpdateFloat(animator -> {
                    float value = (float) animator.getAnimatedValue();
                    float marginTop = appbarHeight * (1 - value);
                    float marginBottom = bottomBarHeight * (1 - value);
                    appbarLayout.setTranslationY(value * (-appbarHeight));
                    bottomBar.setTranslationY(value * bottomBarHeight);
                    ViewUtils.setScaleXY(fab, value);
                    // TODO: 2017/8/15 设置statusBar的颜色
                    setStatusBarColor(value);
                    setMargin(view, marginTop, marginBottom);
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
        ScreenUtils.setStatusBarColor(activity(), color);
    }

    //设置View的margin，用在移动toolbar和bottomBar时改变其他View
    //的margin
    private void setMargin(View view, float marginTop, float marginBottom) {
        if (view == null) {
            return;
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(view.getLayoutParams());
        lp.setMargins(lp.leftMargin, (int) marginTop, lp.rightMargin, (int) marginBottom);
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
                deltaX = event.getX() - oldX;
                oldX = event.getX();
                if (canMoveHorizontal()) {
                    float factor = getHorizontalMoveFactor(deltaX);
                    moveHorizontal(factor);
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (moveOrientation == HORIZONTAL) {
                    float factor = rootView.getTranslationX() / screenWidth;
                    if (factor < 1.0f / 3) {
                        animateHorizontal(factor, 0, false);
                    } else {
                        animateHorizontal(factor, 1f, true);
                    }
                    return true;
                }
                return false;
            default:
                return false;
        }

    }

    private boolean canMoveHorizontal() {
        return (downX < onThirdScreenWidth) && (rootView.getTranslationX() >= 0);
    }

    @Override
    public void moveHorizontal(float factor) {
        float transX = factor * screenWidth;
        rootView.setTranslationX(transX);
        appbarLayout.setTranslationX(transX);
        recyclerView.setAlpha(1 - factor);
        toolbar.setAlpha((1 - factor));
        bottomBar.setAlpha(1 - factor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rootView.setElevation((float) (maxElevation * (1 - factor * 0.5)));
        }
    }

    private float getHorizontalMoveFactor(float deltaX) {
        float transX = rootView.getTranslationX();
        transX += deltaX;

        if (deltaX < 0 && transX < 0) {
            // 向左滑动
            transX = 0;
        }

        float factor = transX / screenWidth;

        return factor;
    }

    @Override
    public void animateHorizontal(float fromFactor, float toFactor, boolean exit) {
        float deltaFactor = Math.abs(toFactor - fromFactor);
        AnimUtils.just()
                .setFloat(fromFactor, toFactor)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration((long) (IntConst.DURATION_500 * deltaFactor))
                .onUpdateFloat(animator -> {
                    float factor = (float) animator.getAnimatedValue();
                    moveHorizontal(factor);
                })
                .onEnd(animator -> {
                    if (exit) {
                        Keyboard.close(getContext(), findInputText());
                        getActivity().finish();
                    }
                })
                .animate();
    }

    private boolean exit = false;

    @Override
    public boolean onBackPressed() {
        if (exit) {
            return false;
        }
        exit = true;
        animateHorizontal(0, 1, true);
        return false;
    }


    /**
     * DESC: 消息发送开始时回调
     * Created by jinphy, on 2018/3/26, at 10:03
     */
    @Override
    public void whenSendStart(Message message) {
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
        msgMap.put(message.getId(), message);
    }

    /**
     * DESC: 消息发送结束时回调
     * Created by jinphy, on 2018/3/26, at 10:03
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void whenSendFinal(EBSendMsg msg) {
        Message finishedMsg = msgMap.remove(msg.data);
        finishedMsg.setStatus(msg.ok ? Message.STATUS_OK : Message.STATUS_NO);
        updateRecyclerView();
        if (Message.TYPE_CHAT_VOICE.equals(finishedMsg.getContentType())) {
            if (!AudioRecorder.isRecording()) {
                DeviceUtils.playRing(R.raw.ring_sent);
            }
        }
    }



    /**
     * DESC: 当在该聊天有新消息时更新界面
     * Created by jinphy, on 2018/3/4, at 16:48
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMsg(EBUpdateView msg) {
        if (msg.data != null && msg.data.contains(withAccount)) {
            if (!isFriend) {
                group = presenter.getGroup(withAccount);
                adapter.setGroup(group);
                adapter.setMembers(presenter.loadMembers(withAccount));
            } else {
                friend = presenter.getFriend(withAccount);
            }
        }
        updateView();

    }

    public void onPickPhoto(List<String> photoPaths) {
        Observable.fromIterable(photoPaths)
                .subscribeOn(Schedulers.io())
                .map(photoPath -> Message.makePhotoMsg(ownerAccount, withAccount, photoPath, isFriend))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(msg -> {
                    presenter.sendFileMsg(msg);
                    adapter.add(msg);
                })
                .subscribe();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateMsg(EBMessage msg) {
        switch (msg.what) {
            case EBMessage.what_updateMsg:
                int scrollY = recyclerView.getScrollY();
                adapter.update(msg.position, msg.data);
                recyclerView.scrollBy(0, scrollY);
                break;
            case EBMessage.what_downloadVoice:
                presenter.downloadVoice(msg.data);
                break;
            case EBMessage.what_downloadVoiceResult:
                Message message = msgMap.remove(msg.msgId);
                if (message != null) {
                    message.extra(Message.KEY_AUDIO_STATUS, msg.voiceStatus);
                    message.setExtra(null);
                    presenter.updateMsg(message);
                    updateRecyclerView();
                }
                break;
            case EBMessage.what_reloadMsg:
                Message newMsg = presenter.getMessage(msg.msgId);
                int scrollY1 = recyclerView.getScrollY();
                adapter.update(newMsg);
                recyclerView.scrollBy(0, scrollY1);
            default:
                break;
        }
    }

    @Override
    public void cacheMsg(Message message) {
        msgMap.put(message.getId(), message);
    }

    private void updateRecyclerView() {
        int scrollY = recyclerView.getScrollY();
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollBy(0, scrollY);
    }

    @Override
    public void onSelectFilesResult(List<String> filePaths) {
        Observable.fromIterable(filePaths)
                .subscribeOn(Schedulers.io())
                .map(photoPath -> Message.makeFileMsg(ownerAccount, withAccount, photoPath, isFriend))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(msg -> {
                    presenter.sendFileMsg(msg);
                    adapter.add(msg);
                })
                .subscribe();

    }
}
