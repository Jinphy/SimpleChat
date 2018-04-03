package com.example.jinphy.simplechat.modules.chat.models;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.custom_view.AudioRecordButton;
import com.example.jinphy.simplechat.modules.chat.ChatActivity;

/**
 * DESC: 聊天界面底部功能的bottomBar
 * Created by jinphy on 2018/4/1.
 */

public class BottomBar {

    public final View rootView;

    /**
     * DESC: 语音按键
     * Created by jinphy, on 2018/4/1, at 19:49
     */
    public final ImageView btnVoice;

    /**
     * DESC: 键盘按键
     * Created by jinphy, on 2018/4/1, at 19:49
     */
    public final ImageView btnKeyboard;

    /**
     * DESC: 文本输入框
     * Created by jinphy, on 2018/4/1, at 19:49
     */
    public final EditText textInput;

    /**
     * DESC: 语音输入框
     * Created by jinphy, on 2018/4/1, at 19:50
     */
    public final AudioRecordButton voiceInput;

    /**
     * DESC: 更多功能按键
     * Created by jinphy, on 2018/4/1, at 19:52
     */
    public final ImageView btnMore;

    /**
     * DESC: 发送按键
     * Created by jinphy, on 2018/4/1, at 19:52
     */
    public final TextView btnSend;

    /**
     * DESC: 向下按键
     * Created by jinphy, on 2018/4/1, at 19:53
     */
    public final ImageView btnDown;

    /**
     * DESC: “更多”功能布局
     * Created by jinphy, on 2018/4/2, at 20:52
     */
    public final BottomExtraView bottomExtraView;


    public static BottomBar init(ChatActivity activity, View bottomBar) {
        return new BottomBar(activity, bottomBar);
    }

    private BottomBar(ChatActivity activity, View bottomBar) {
        this.rootView = bottomBar;
        this.btnVoice = bottomBar.findViewById(R.id.voice_View);
        this.btnKeyboard = bottomBar.findViewById(R.id.keyboard_view);
        this.textInput = bottomBar.findViewById(R.id.input_text);
        this.voiceInput = bottomBar.findViewById(R.id.input_voice);
        this.btnMore = bottomBar.findViewById(R.id.more_view);
        this.btnDown = bottomBar.findViewById(R.id.down_view);
        this.btnSend = bottomBar.findViewById(R.id.send_view);
        this.bottomExtraView = BottomExtraView.init(activity, bottomBar.findViewById(R.id.bottom_extra_view));
    }
}
