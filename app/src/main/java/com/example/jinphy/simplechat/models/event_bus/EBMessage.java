package com.example.jinphy.simplechat.models.event_bus;

import com.example.jinphy.simplechat.models.message.Message;

/**
 * DESC:
 * Created by jinphy on 2018/3/24.
 */

public class EBMessage extends EBBase<Message> {

    public static final int what_updateMsg = 1;
    public static final int what_downloadVoice = 2;
    public static final int what_downloadVoiceResult = 3;
    public static final int what_reloadMsg = 4;



    public final int what;
    public int position;
    public String voiceStatus;
    public long msgId;



    public static EBMessage updateMsg(Message data, int position) {
        EBMessage result = new EBMessage(data, what_updateMsg);
        result.position = position;
        return result;
    }

    public static EBMessage reloadMsg(long msgId) {
        EBMessage result = new EBMessage(null, what_reloadMsg);
        result.msgId = msgId;
        return result;
    }


    public static EBMessage downloadVoice(Message data) {
        return new EBMessage(data, what_downloadVoice);
    }

    public static EBMessage downloadVoiceResult(String voiceStatus, long msgId) {
        EBMessage result = new EBMessage(null, what_downloadVoiceResult);
        result.voiceStatus = voiceStatus;
        result.msgId = msgId;
        return result;
    }


    private EBMessage(Message data, int what) {
        super(true, data);
        this.what = what;
    }
}
