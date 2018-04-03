package com.example.jinphy.simplechat.modules.system_msg.new_member;

import android.graphics.Bitmap;

import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.utils.EncryptUtils;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.StringUtils;

/**
 * DESC:
 * Created by jinphy on 2018/4/2.
 */

public class NewMember{
    public Message message;
    public Member member;
    public Group group;
    public String extraMsg;
    public String account;
    public String status;

    public static NewMember create(Message message, Group group, String account, String status, String extraMsg) {
        NewMember newMember = new NewMember();
        newMember.message = message;
        newMember.group = group;
        newMember.account = account;
        newMember.status = status;
        newMember.extraMsg = extraMsg;
        return newMember;
    }

    public Bitmap getAvatar() {
        return ImageUtil.loadAvatar(group.getGroupNo(), 100, 100);
    }

    public String getName() {
        return group.getName();
    }

    public String getGroupNo() {
        return group.getGroupNo();
    }

    public boolean isNew() {
        return message.isNew();
    }

    public String getTime() {
        return StringUtils.formatDate(Long.valueOf(message.getCreateTime()));
    }

    public String getAccount() {
        return account;
    }

    public String getExtraMsg() {
        return extraMsg;
    }

    public String getStatus() {
        return status;
    }

    public String getCreator() {
        return group.getCreator();
    }

    public void reject() {
        status = Group.STATUS_NO;
        message.setExtra(
                group.getGroupNo()
                        + "@" + account
                        + "@" + status
                        + "@" + EncryptUtils.aesEncrypt(extraMsg));
    }
    public void agree() {
        status = Group.STATUS_OK;
        message.setExtra(
                group.getGroupNo()
                        + "@" + account
                        + "@" + status
                        + "@" + EncryptUtils.aesEncrypt(extraMsg));
    }

}
