package com.example.jinphy.simplechat.models.message;

import android.support.annotation.IntRange;

import com.example.jinphy.simplechat.utils.MathUtils;

/**
 * Created by jinphy on 2017/8/13.
 */

public class Message {

    public static final int TYPE_SEND = 0x00000001;
    public static final int TYPE_RECEIVE = 0x00000002;
    public static final int TYPE_TEXT = 0x00000003;
    public static final int TYPE_PHOTO = 0x00000004;
    public static final int TYPE_VOICE = 0x00000005;
    public static final int TYPE_VIDEO = 0x00000006;
    public static final int TYPE_FILE = 0X00000007;

    private int sourceType;//消息来源类型，有发送和接收两种
    private int contentType;// 消息内容类型，有文本等
    private String content;//消息的内容，可以是文本，也可以是图片等的url
    private String createTime;//消息创建时间

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(String senderAccount) {
        this.senderAccount = senderAccount;
    }

    public String getReceiverAccount() {
        return receiverAccount;
    }

    public void setReceiverAccount(String receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    private String senderAccount;
    private String receiverAccount;

    public void setSourceType(@IntRange(from = TYPE_SEND, to = TYPE_RECEIVE) int sourceType) {
        if (!MathUtils.within(sourceType,TYPE_SEND,TYPE_RECEIVE)) {
            throw new IllegalArgumentException(
                    "the title sourceType is neither Message.TYPE_SEND or TYPE_RECEIVE");
        }
        this.sourceType = sourceType;
    }

    public int getSourceType() {
        return this.sourceType;
    }

    public void setContentType(@IntRange(from = TYPE_TEXT, to = TYPE_FILE) int contentType) {
        if (!MathUtils.within(contentType,TYPE_TEXT,TYPE_FILE)) {
            throw new IllegalArgumentException(
                    "the title sourceType must be Message.TYPE_TEXT etc.");
        }
        this.contentType = contentType;
    }

    /**
     * 获取contentType
     * */
    public int getContentType() {
        return this.contentType;
    }

    /**
     * 解析type的高16位，获得sourceType
     * 逻辑右移，高位补零
     * @param type 合并后的sourceType和contentType
     * */
    public static int parseSourceType(int type) {
        return type>>>16;
    }


    /**
     * 解析type的低16位，获得contentType
     *
     * @param type 合并后的sourceType和contentType
     * */
    public static int parseContentType(int type) {
        return type & 0xffff;
    }


    /**
     * 将sourceType和contentType结合在一起
     * 高16位表示sourceType，
     * 低16位表示contentType
     *
     * */
    public int combineType() {
        return  (sourceType<<16) | contentType;
    }



}
