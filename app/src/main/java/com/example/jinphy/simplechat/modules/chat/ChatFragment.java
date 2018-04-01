package com.example.jinphy.simplechat.modules.chat;

import android.animation.AnimatorSet;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.constants.IntConst;
import com.example.jinphy.simplechat.custom_libs.AudioPlayer;
import com.example.jinphy.simplechat.custom_libs.AudioRecorder;
import com.example.jinphy.simplechat.custom_view.dialog.file_selector.FileSelector;
import com.example.jinphy.simplechat.listener_adapters.TextListener;
import com.example.jinphy.simplechat.models.event_bus.EBMessage;
import com.example.jinphy.simplechat.models.event_bus.EBSendMsg;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.modules.group.group_detail.ModifyGroupActivity;
import com.example.jinphy.simplechat.modules.modify_friend_info.ModifyFriendInfoActivity;
import com.example.jinphy.simplechat.modules.show_file.ShowFileActivity;
import com.example.jinphy.simplechat.modules.show_photo.ShowPhotoActivity;
import com.example.jinphy.simplechat.utils.AnimUtils;
import com.example.jinphy.simplechat.utils.ColorUtils;
import com.example.jinphy.simplechat.utils.DeviceUtils;
import com.example.jinphy.simplechat.utils.Keyboard;
import com.example.jinphy.simplechat.utils.ScreenUtils;
import com.example.jinphy.simplechat.utils.ViewUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPicker;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an app of this fragment.
 */
public class ChatFragment extends BaseFragment<ChatPresenter> implements ChatContract.View {

    public static final String SAVE_KEY_WITH_ACCOUNT = "SAVE_KEY_WITH_ACCOUNT";

    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;

    private MyView myView;

    private MyData myData;

    int startStatusColor;
    // 隐藏 appBar 后的statusBar的 最终颜色，为colorAccent
    int endStatusColor;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            myData = MyData.init(savedInstanceState.getString(SAVE_KEY_WITH_ACCOUNT));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_KEY_WITH_ACCOUNT, myData.with);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.updateRecord(myData.adapter.getLast());
        EventBus.getDefault().post(new EBUpdateView());
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
        fragment.myData = MyData.init(withAccount);
        return fragment;
    }

    @Override
    protected void findViewsById(View view) {
        myView = MyView.init((ChatActivity) activity(), view);
    }

    @Override
    protected void initData() {
        myData.update(presenter);
    }


    @Override
    protected void setupViews() {
        myView.toolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        if (myData.isChatWithFriend) {
            activity().setTitle(myData.withFriend.getShowName());
        } else {
            activity().setTitle(myData.withGroup.getName());
        }
        myView.recyclerView.setLayoutManager(myView.layoutManager);
        myView.recyclerView.setAdapter(myData.adapter);

        Observable.just(myData.with)
                .subscribeOn(Schedulers.io())
                .map(with -> presenter.loadMessages(with))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(messages -> {
                    myData.adapter.update(messages);
                    if (!myData.isChatWithFriend) {
                        myData.adapter.setGroup(myData.withGroup);
                        myData.adapter.setMembers(presenter.loadMembers(myData.with));
                    }

                    int position = myData.adapter.getItemCount() - 1;
                    if (position >= 0) {
                        myView.recyclerView.scrollToPosition(position);
                    }
                })
                .subscribe();
    }


    @Override
    public void updateView() {
        int last = myView.layoutManager.findLastVisibleItemPosition();
        int size = myData.adapter.getItemCount();
        int scrollY = myView.recyclerView.getScrollY();

        Observable.<List<Message>>create(e -> {
            e.onNext(presenter.loadNewMessages(myData.with));
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .map(newMessages -> newMessages.toArray(new Message[newMessages.size()]))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(newMessages -> {
                    myData.adapter.add(newMessages);
                })
                .doOnComplete(()->{
                    if (size - last < 5) {
                        myView.recyclerView.smoothScrollToPosition(myData.adapter.getItemCount() - 1);
                    } else {
                        myView.recyclerView.scrollBy(0, scrollY);
                    }
                })
                .subscribe();
    }

    @Override
    protected void registerEvent() {
        myView.fab.setOnClickListener(v -> {
            int position = myData.adapter.getItemCount();
            if (position >= 0) {
                myView.recyclerView.smoothScrollToPosition(position);
            }
            showBar(myView.recyclerView);
        });

        myView.bottomBar.btnKeyboard.setOnClickListener(v -> {
            myView.showVoiceBtn();
            myView.showTextInput(myData);
        });
        myView.bottomBar.btnVoice.setOnClickListener(v -> {
            myView.showKeyboardBtn();
            myView.showVoiceInput();
        });

        myView.bottomBar.btnMore.setOnClickListener(v -> myView.showMoreLayout());

        myView.bottomBar.btnSend.setOnClickListener(this::sendTextMsg);

        myView.bottomBar.btnDown.setOnClickListener(v -> {
            myView.hideMoreLayout();
        });

        myView.bottomBar.voiceInput.onRecordFinished((filePath, duration) -> {
            // 发送语音消息
            Message message = Message.makeVoiceMsg(
                    myData.owner,
                    myData.with,
                    filePath,
                    duration,
                    myData.isChatWithFriend
            );
            presenter.sendFileMsg(message);
            myData.adapter.add(message);
        });

        myView.bottomBar.textInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                myView.hideExtraBottomLayout();
            } else {
                Keyboard.close(getContext(), myView.bottomBar.textInput);
            }
        });

        myView.bottomBar.textInput.addTextChangedListener((TextListener.After) editable -> {
            if (editable.length() == 0) {
                myView.showMoreBtn();
            } else {
                myView.showSendBtn();
            }
        });

        myView.bottomBar.moreMenu.photoItem.setOnClickListener(v -> {
            PhotoPicker.builder()
                    .setPhotoCount(9)
                    .setShowCamera(true)
                    .setShowGif(true)
                    .setPreviewEnabled(true)
                    .start(activity(), PhotoPicker.REQUEST_CODE);
        });

        myView.bottomBar.moreMenu.fileItem.setOnClickListener(v -> {{
            FileSelector.from(activity())
                    .onSelect(this::onSelectFilesResult)
                    .make();
        }

        });

        myData.adapter.onClick((view, item, type, position) -> {
            switch (item.getContentType()) {
                case Message.TYPE_CHAT_IMAGE:{
                    if (!myData.adapter.hasPhoto(item)) {
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
                    if (!myData.adapter.hasFile(item)) {
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

    }


    /**
     * DESC: 发送文本信息
     * Created by jinphy, on 2018/4/1, at 21:24
     */
    private void sendTextMsg(View view) {
        if (myData.isChatWithFriend) {
            switch (myData.withFriend.getStatus()) {
                case Friend.status_black_listed:
                    App.showToast("您已被对方拉入黑名单，不能发送消息！", false);
                    return;
                case Friend.status_deleted:
                    App.showToast("对方不是好友，不能发送消息！", false);
                    return;
            }
        } else {
            if (!myData.withGroup.isMyGroup()) {
                App.showToast("您非本群成员，不能发送信息！", false);
                return;
            }
            if (!myData.memberOfMe.isAllowChat()) {
                App.showToast("您已被群主禁言，不能发送信息！", false);
                return;
            }
        }
        EditText inputText = myView.bottomBar.textInput;
        String content = inputText.getText().toString();
        inputText.setText("");
        Message message = Message.makeTextMsg(myData.owner, myData.with, content, myData.isChatWithFriend);
        myData.adapter.add(message);
        presenter.sendTextMsg(message);
    }



    //----------------------------------------------

    @Override
    protected void onKeyboardEvent(boolean open) {
        if (!open) {

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
                if (myData.isChatWithFriend) {
                    ModifyFriendInfoActivity.start(activity(), myData.with);
                } else {
                    ModifyGroupActivity.start(activity(), myData.with);
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

        int appbarHeight = myView.toolbar.getHeight();
        int bottomBarHeight = myView.bottomBar.rootView.getMeasuredHeight();

        animatorSet = AnimUtils.just()
                .setFloat(fromValue, toValue)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(IntConst.DURATION_500)
                .onStart(animator -> {
                    if (showBar) {
                        myView.toolbar.setVisibility(View.VISIBLE);
                        myView.bottomBar.rootView.setVisibility(View.VISIBLE);
                    } else {
                        myView.fab.setVisibility(View.VISIBLE);
                    }
                })
                .onUpdateFloat(animator -> {
                    float value = (float) animator.getAnimatedValue();
                    float marginTop = appbarHeight * (1 - value);
                    float marginBottom = bottomBarHeight * (1 - value);
                    myView.toolbar.setTranslationY(value * (-appbarHeight));
                    myView.bottomBar.rootView.setTranslationY(value * bottomBarHeight);
                    ViewUtils.setScaleXY(myView.fab, value);
                    // TODO: 2017/8/15 设置statusBar的颜色
                    setStatusBarColor(value);
                    setMargin(view, marginTop, marginBottom);
                })
                .onEnd(animator -> {
                    if (showBar) {
                        myView.fab.setVisibility(View.GONE);
                    } else {
                        myView.toolbar.setVisibility(View.GONE);
                        myView.bottomBar.rootView.setVisibility(View.GONE);
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
        myView.toolbar.setTranslationX(transX);
        myView.recyclerView.setAlpha(1 - factor);
        myView.toolbar.setAlpha((1 - factor));
        myView.bottomBar.rootView.setAlpha(1 - factor);
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
                        Keyboard.close(getContext(), myView.bottomBar.textInput);
                        activity().finish();
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
        myView.recyclerView.smoothScrollToPosition(myData.adapter.getItemCount() - 1);
        myData.msgMap.put(message.getId(), message);
    }

    /**
     * DESC: 消息发送结束时回调
     * Created by jinphy, on 2018/3/26, at 10:03
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void whenSendFinal(EBSendMsg msg) {
        Message finishedMsg = myData.msgMap.remove(msg.data);
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
        if (msg.data != null && msg.data.contains(myData.with)) {
            if (!myData.isChatWithFriend) {
                myData.withGroup = presenter.getGroup(myData.with);
                myData.adapter.setGroup(myData.withGroup);
                myData.adapter.setMembers(presenter.loadMembers(myData.with));
            } else {
                myData.withFriend = presenter.getFriend(myData.with);
            }
        }
        updateView();

    }

    public void onPickPhoto(List<String> photoPaths) {
        Observable.fromIterable(photoPaths)
                .subscribeOn(Schedulers.io())
                .map(photoPath -> Message.makePhotoMsg(
                        myData.owner,
                        myData.with,
                        photoPath,
                        myData.isChatWithFriend)
                )
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(msg -> {
                    presenter.sendFileMsg(msg);
                    myData.adapter.add(msg);
                })
                .subscribe();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateMsg(EBMessage msg) {
        switch (msg.what) {
            case EBMessage.what_updateMsg:
                int scrollY = myView.recyclerView.getScrollY();
                myData.adapter.update(msg.position, msg.data);
                myView.recyclerView.scrollBy(0, scrollY);
                break;
            case EBMessage.what_downloadVoice:
                presenter.downloadVoice(msg.data);
                break;
            case EBMessage.what_downloadVoiceResult:
                Message message = myData.msgMap.remove(msg.msgId);
                if (message != null) {
                    message.extra(Message.KEY_AUDIO_STATUS, msg.voiceStatus);
                    message.setExtra(null);
                    presenter.updateMsg(message);
                    updateRecyclerView();
                }
                break;
            case EBMessage.what_reloadMsg:
                Message newMsg = presenter.getMessage(msg.msgId);
                int scrollY1 = myView.recyclerView.getScrollY();
                myData.adapter.update(newMsg);
                myView.recyclerView.scrollBy(0, scrollY1);
            default:
                break;
        }
    }

    @Override
    public void cacheMsg(Message message) {
        myData.msgMap.put(message.getId(), message);
    }

    private void updateRecyclerView() {
        int scrollY = myView.recyclerView.getScrollY();
        myData.adapter.notifyDataSetChanged();
        myView.recyclerView.smoothScrollBy(0, scrollY);
    }

    @Override
    public void onSelectFilesResult(List<String> filePaths) {
        Observable.fromIterable(filePaths)
                .subscribeOn(Schedulers.io())
                .map(photoPath -> Message.makeFileMsg(
                        myData.owner,
                        myData.with,
                        photoPath,
                        myData.isChatWithFriend)
                )
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(msg -> {
                    presenter.sendFileMsg(msg);
                    myData.adapter.add(msg);
                })
                .subscribe();

    }
}
