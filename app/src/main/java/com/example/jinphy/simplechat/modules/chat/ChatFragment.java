package com.example.jinphy.simplechat.modules.chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.custom_libs.AudioPlayer;
import com.example.jinphy.simplechat.custom_libs.AudioRecorder;
import com.example.jinphy.simplechat.custom_view.dialog.file_selector.FileSelector;
import com.example.jinphy.simplechat.custom_view.dialog.friend_selector.FriendSelector;
import com.example.jinphy.simplechat.custom_view.menu.MyMenu;
import com.example.jinphy.simplechat.listener_adapters.TextListener;
import com.example.jinphy.simplechat.models.event_bus.EBMessage;
import com.example.jinphy.simplechat.models.event_bus.EBSendMsg;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.friend.CheckedFriend;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.modules.chat.models.MyData;
import com.example.jinphy.simplechat.modules.chat.models.MyView;
import com.example.jinphy.simplechat.modules.group.group_detail.ModifyGroupActivity;
import com.example.jinphy.simplechat.modules.modify_friend_info.ModifyFriendInfoActivity;
import com.example.jinphy.simplechat.modules.show_file.ShowFileActivity;
import com.example.jinphy.simplechat.modules.show_photo.ShowPhotoActivity;
import com.example.jinphy.simplechat.utils.DeviceUtils;
import com.example.jinphy.simplechat.utils.Keyboard;
import com.example.jinphy.simplechat.utils.ScreenUtils;
import com.example.jinphy.simplechat.utils.StringUtils;

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


    private MyView myView;

    private MyData myData;

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
        myView.appBar.setBackgroundColor(colorPrimary());
        if (myData.isChatWithFriend) {
            activity().setTitle(myData.withFriend.getShowName());
        } else {
            activity().setTitle(myData.withGroup.getName()+"("+presenter.getMemberCount(myData.with)+")");
        }
        myView.recyclerView.setAdapter(myData.adapter);
        myView.bottomBar.bottomExtraView.moreMenu.setAdapter(myData.bottomMenuAdapter);

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
                    for (Message message : messages) {
                        if (StringUtils.equal(Message.STATUS_SENDING, message.getStatus())) {
                            myData.msgMap.put(message.getId(), message);
                        }
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
            myView.showBar();
            int position = myData.adapter.getItemCount();
            if (position >= 0) {
                myView.recyclerView.smoothScrollToPosition(position);
            }
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

        myView.bottomBar.btnSend.setOnClickListener(v -> {
            if (!checkIfCanSend()) {
                return;
            }
            EditText inputText = myView.bottomBar.textInput;
            String content = inputText.getText().toString();
            inputText.setText("");
            Message message = Message.makeTextMsg(myData.owner, myData.with, content, myData.isChatWithFriend);
            myData.adapter.add(message);
            sendTextMsg(message);
        });

        myView.bottomBar.btnDown.setOnClickListener(v -> myView.hideExtraBottomLayout());

        myView.bottomBar.voiceInput.onRecordFinished((filePath, duration) -> {
            if (!checkIfCanSend()) {
                return;
            }
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

        myView.recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                // velocityY < 0 时，手指向下滑动
                if (velocityY < 0 && myView.isBarShowing()) {
                    int velocityYDp = ScreenUtils.px2dp(activity(), velocityY);
                    if (velocityYDp < -1000 && myData.adapter.getItemCount() > 20) {
                        myView.hideBar();
                    }
                }
                return false;
            }
        });

        myData.bottomMenuAdapter.onClick((v, item, holder, type, position) -> {
            if (!checkIfCanSend()) {
                return;
            }
            switch (item.icon) {
                case R.drawable.ic_photo_24dp:
                    // 发送图片
                    PhotoPicker.builder()
                            .setPhotoCount(9)
                            .setShowCamera(true)
                            .setShowGif(true)
                            .setPreviewEnabled(true)
                            .start(activity(), PhotoPicker.REQUEST_CODE);

                    break;
                case R.drawable.ic_folder_2_24dp:
                    // 发送文件
                    FileSelector.from(activity())
                            .onSelect(this::onSelectFilesResult)
                            .make();

                    break;
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

        myData.adapter.onLongClick((view, item, type, position) -> {
            MyMenu.create(activity())
                    .width(200)
                    .item("复制文本", (menu, menuItem) -> {
                        DeviceUtils.copyText(item.getContent());
                    })
                    .item("重新发送", (menu, menuItem) -> {
                        if (!checkIfCanSend()) {
                            return;
                        }
                        myData.adapter.remove(item);
                        item.setStatus(Message.STATUS_SENDING);
                        item.setCreateTime(System.currentTimeMillis() + "");
                        switch (item.getContentType()) {
                            case Message.TYPE_CHAT_TEXT:
                                sendTextMsg(item);
                                break;
                            default:
                                presenter.sendFileMsg(item);
                                break;
                        }
                        myData.adapter.add(item);
                    })
                    .item("转发", (menu, menuItem) -> {
                        if (Message.TYPE_CHAT_IMAGE.equals(item.getContentType())
                                && !myData.adapter.hasPhoto(item)) {
                            App.showToast("图片不存在！", false);
                            return;
                        }
                        FriendSelector.create(activity())
                                .title("选择转发好友")
                                .exclude(myData.with)
                                .titleColor(colorPrimary())
                                .onSelect(friends -> {
                                    for (CheckedFriend friend : friends) {
                                        Message newMsg = item.copyToSend();
                                        newMsg.setStatus(Message.STATUS_SENDING);
                                        newMsg.setCreateTime(System.currentTimeMillis() + "");
                                        newMsg.setWith(friend.getAccount());
                                        newMsg.extra(Message.KEY_SENDER, myData.owner);
                                        // 只有发送成功的消息或者接收的消息才能转发，所有只需简单的将
                                        // 消息发送即可，图片、语音等消息资源在服务器上已经有了
                                        sendTextMsg(newMsg);
                                        presenter.updateRecord(newMsg);
                                    }
                                })
                                .display();
                    })
                    .item("删除", (menu, item14) -> {
                        int scrollY = myView.recyclerView.getScrollY();
                        myData.adapter.remove(item);
                        presenter.removeMsg(item);
                        myView.recyclerView.scrollBy(0, scrollY);
                        App.showToast("已删除！", false);
                    })
                    .onDisplay(menu -> {
                        if (Message.TYPE_CHAT_TEXT.equals(item.getContentType())) {
                            menu.item(0).setVisibility(View.VISIBLE);
                        } else {
                            menu.item(0).setVisibility(View.GONE);
                        }

                        if (StringUtils.notEqual(Message.STATUS_OK, item.getStatus())
                                || Message.TYPE_CHAT_VOICE.equals(item.getContentType())) {
                            menu.item(2).setVisibility(View.GONE);
                        } else {
                            menu.item(2).setVisibility(View.VISIBLE);
                        }

                        if (Message.SEND == item.getSourceType()
                                && Message.STATUS_NO.equals(item.getStatus())) {
                            menu.item(1).setVisibility(View.VISIBLE);
                        } else {
                            menu.item(1).setVisibility(View.GONE);
                        }
                    })
                    .display();
            return true;
        });
    }


    /**
     * DESC: 发送文本信息
     * <p>
     * Created by jinphy, on 2018/4/1, at 21:24
     */
    private void sendTextMsg(Message msg) {
        presenter.sendTextMsg(msg);
    }

    /**
     * DESC: 判断是否能够发送消息
     * Created by jinphy, on 2018/5/8, at 9:35
     */
    private boolean checkIfCanSend() {
        if (myData.isChatWithFriend) {
            //与好友聊天
            switch (myData.withFriend.getStatus()) {
                case Friend.status_black_listed:
                    App.showToast("您已被对方拉入黑名单，不能发送消息！", false);
                    return false;
                case Friend.status_deleted:
                    App.showToast("对方不是好友，不能发送消息！", false);
                    return false;
            }
            return true;
        } else {
            // 群聊
            if (!myData.withGroup.isMyGroup()) {
                App.showToast("您非本群成员，不能发送信息！", false);
                return false;
            }

            if (!myData.memberOfMe.isAllowChat()) {
                App.showToast("您已被群主禁言，不可以发送消息！", false);
                return false;
            }
            return true;
        }
    }

    //----------------------------------------------

    @Override
    protected void onKeyboardEvent(boolean open) {
        if (open) {
            myView.showMoreBtn();
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
        if (!myData.isChatWithFriend) {
            menu.getItem(0).setIcon(R.drawable.ic_group_chat_white_24dp);
        }
    }

    /**
     * DESC: 菜单点击事件
     * Created by jinphy, on 2018/4/2, at 8:23
     */
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


    @Override
    public boolean handleHorizontalTouchEvent(MotionEvent event) {
        return myView.handleHorizontalEvent(event);
    }


    @Override
    public boolean onBackPressed() {
        if (myData.exit) {
            return false;
        }
        myData.exit = true;
        myView.exit();
        return false;
    }


    /**
     * DESC: 消息发送开始时回调
     * Created by jinphy, on 2018/3/26, at 10:03
     */
    @Override
    public void whenSendStart(Message message) {
        cacheMsg(message);

        if (StringUtils.notEqual(myData.with, message.getWith())) {
            // 这种消息时转发的消息
            return;
        }
        myView.recyclerView.smoothScrollToPosition(myData.adapter.getItemCount() - 1);
    }

    /**
     * DESC: 消息发送结束时回调
     * Created by jinphy, on 2018/3/26, at 10:03
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void whenSendFinal(EBSendMsg msg) {
        Message finishedMsg = myData.msgMap.remove(msg.data);
        if (StringUtils.notEqual(myData.with, finishedMsg.getWith())) {
            // 这种消息为转发的消息
            App.showToast("消息已转发！", false);
            return;
        }
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

    /**
     * DESC: 获取图片后的回调
     * Created by jinphy, on 2018/4/2, at 8:31
     */
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
                break;
            case EBMessage.what_update_group:
                myData.withGroup = presenter.getGroup(myData.with);
                myData.adapter.setGroup(myData.withGroup);
                myData.adapter.setMembers(presenter.loadMembers(myData.with));
                updateRecyclerView();
            default:
                break;
        }
    }

    /**
     * DESC: 缓存需要进一步处理的信息
     * Created by jinphy, on 2018/4/2, at 8:30
     */
    @Override
    public void cacheMsg(Message message) {
        myData.msgMap.put(message.getId(), message);
    }

    /**
     * DESC: 更新recyclerView界面
     * Created by jinphy, on 2018/4/2, at 8:30
     */
    private void updateRecyclerView() {
        int scrollY = myView.recyclerView.getScrollY();
        myData.adapter.notifyDataSetChanged();
        myView.recyclerView.smoothScrollBy(0, scrollY);
    }

    /**
     * DESC: 选择文件回调
     * Created by jinphy, on 2018/4/2, at 8:29
     */
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
