package com.example.jinphy.simplechat.modules.system_msg.new_member;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseRecyclerViewAdapter;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.utils.EncryptUtils;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * DESC:
 * Created by jinphy on 2018/3/12.
 */

public class NewMemberAdapter extends BaseRecyclerViewAdapter<NewMemberAdapter.NewMember, NewMemberAdapter.ViewHolder> {



    @Override
    public void onBindViewHolder(ViewHolder holder, NewMember item, int position) {
        Bitmap avatar = item.getAvatar();
        if (avatar == null) {
            holder.avatar.setImageResource(R.drawable.ic_group_chat_white_24dp);
        } else {
            holder.avatar.setImageBitmap(avatar);
        }
        holder.name.setText(item.getName());
        holder.groupNo.setText(item.getGroupNo());
        holder.newMsg.setVisibility(item.isNew() ? View.VISIBLE : View.GONE);
        holder.time.setText(item.getTime());
        holder.account.setText(item.getAccount());
        holder.extraMsg.setText(item.getExtraMsg());

        switch (item.getStatus()) {
            case Group.STATUS_OK:
                holder.statusOk.setVisibility(View.VISIBLE);
                holder.statusNo.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
                holder.btnAgree.setVisibility(View.GONE);
                break;
            case Group.STATUS_NO:
                holder.statusOk.setVisibility(View.GONE);
                holder.statusNo.setVisibility(View.VISIBLE);
                holder.btnReject.setVisibility(View.GONE);
                holder.btnAgree.setVisibility(View.GONE);
                break;
            case Group.STATUS_WAITING:
                holder.statusOk.setVisibility(View.GONE);
                holder.statusNo.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.VISIBLE);
                holder.btnAgree.setVisibility(View.VISIBLE);
                holder.btnReject.setEnabled(true);
                holder.btnAgree.setEnabled(true);
                holder.btnReject.setTextColor(0xD50000);
                holder.btnAgree.setTextColor(0xff558B2F);
                break;
            case Group.STATUS_INVALIDATE:
                holder.statusOk.setVisibility(View.GONE);
                holder.statusNo.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.VISIBLE);
                holder.btnAgree.setVisibility(View.VISIBLE);
                holder.btnReject.setEnabled(false);
                holder.btnAgree.setEnabled(false);
                holder.btnReject.setTextColor(0x7faaaaaa);
                holder.btnAgree.setTextColor(0x7faaaaaa);
            default:
                break;
        }

        setClick(item, position, 0, holder.btnHead, holder.btnReject, holder.btnAgree);

    }

    @Override
    protected int getResourceId() {
        return R.layout.layout_new_member_item;
    }

    @Override
    protected ViewHolder onCreateViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public List<Message> getNewMsgAndSetOld() {
        List<Message> result = new LinkedList<>();
        for (NewMember newMember : data) {
            if (newMember.message.isNew()) {
                newMember.message.setNew(false);
                result.add(newMember.message);
            }
        }
        return result;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        
        CircleImageView avatar;
        TextView name;
        TextView groupNo;
        TextView time;
        View newMsg;
        TextView account;
        TextView extraMsg;
        View statusOk;
        View statusNo;
        View btnHead;
        TextView btnReject;
        TextView btnAgree;
        
        
        public ViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar_view);
            name = itemView.findViewById(R.id.name_view);
            groupNo = itemView.findViewById(R.id.group_no_view);
            time = itemView.findViewById(R.id.time_view);
            newMsg = itemView.findViewById(R.id.new_msg_view);
            account = itemView.findViewById(R.id.account_view);
            extraMsg = itemView.findViewById(R.id.extra_msg_view);
            statusOk = itemView.findViewById(R.id.status_ok_view);
            statusNo = itemView.findViewById(R.id.status_no_view);
            btnHead = itemView.findViewById(R.id.btn_head);
            btnReject = itemView.findViewById(R.id.btn_reject);
            btnAgree = itemView.findViewById(R.id.btn_agree);
        }
    }

    public static class NewMember{
        public Message message;
        public Member member;
        public Group group;
        public String extraMsg;
        public String account;
        public String status;

        public static NewMember create(Message message, Group group,String account,String status,String extraMsg) {
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
}

