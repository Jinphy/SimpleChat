package com.example.jinphy.simplechat.modules.main.msg;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseRecyclerViewAdapter;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.Preconditions;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jinphy on 2017/8/10.
 */

public class MsgRecyclerViewAdapter extends BaseRecyclerViewAdapter<MsgRecyclerViewAdapter.ViewHolder> {

    private List<MessageRecord> messageRecords;

    public MsgRecyclerViewAdapter() {
        this.messageRecords = new LinkedList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_tab_msg_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageRecord messageRecord = messageRecords.get(position);

        holder.name.setText(messageRecord.getName());
        holder.lastMsg.setText(messageRecord.getMsg());
        holder.time.setText(messageRecord.getTime());

        if (Friend.system.equals(messageRecord.getWith())) {
            holder.avatar.setImageResource(R.drawable.ic_system_24dp);
        } else {
            Bitmap bitmap = ImageUtil.loadAvatar(messageRecord.getWith(),
                    holder.avatar.getMeasuredWidth(), holder.avatar.getMeasuredHeight());
            if (bitmap != null) {
                holder.avatar.setImageBitmap(bitmap);
            } else if (messageRecord.getWith().contains("G")) {
                holder.avatar.setImageResource(R.drawable.ic_group_chat_white_24dp);
            } else {
                holder.avatar.setImageResource(R.drawable.ic_person_48dp);
            }

        }


        // 设置未读消息数
        int count = messageRecord.getNewMsgCount();
        if (count == 0) {
            holder.count.setVisibility(View.GONE);
        } else {
            holder.count.setVisibility(View.VISIBLE);
            if (count < 100) {
                holder.count.setText(count+"");
            } else {
                holder.count.setText("99+");
            }
        }
        // 设置是否有置顶
        holder.top.setVisibility(messageRecord.getToTop() == 1 ? View.VISIBLE : View.GONE);

        if (click != null) {
//            holder.avatar.setOnClickListener(view -> click.onClick(view, messageRecord,0,position));
            holder.itemView.setOnClickListener(view -> click.onClick(view, messageRecord,0,position));
        }
        if (longClick != null) {
//            holder.avatar.setOnLongClickListener(view -> longClick.onLongClick(view, messageRecord,0,position));
            holder.itemView.setOnLongClickListener(view -> longClick.onLongClick(view, messageRecord,0,position));
        }

    }


    @Override
    public int getItemCount() {
        return messageRecords.size();
    }

    public void update(List<MessageRecord> records) {
        messageRecords.clear();
        messageRecords.addAll(records);
        notifyDataSetChanged();
    }


    //===================================================================\\
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView avatar;
        private TextView name;
        private TextView lastMsg;
        private TextView time;
        private View itemView;
        private TextView count;
        private View top;


        public ViewHolder(View itemView) {
            super(itemView);
            this.avatar = itemView.findViewById(R.id.avatar);
            this.name = itemView.findViewById(R.id.name);
            this.lastMsg = itemView.findViewById(R.id.last_msg);
            this.time = itemView.findViewById(R.id.time);
            this.itemView = itemView;
            this.count = itemView.findViewById(R.id.new_count);
            this.top = itemView.findViewById(R.id.top);
        }
    }

    /**
     * DESC: 跟新消息记录中对应的好友依赖
     * Created by jinphy, on 2018/3/3, at 13:23
     */
    public void updateFriend(Friend friend) {
        for (MessageRecord messageRecord : messageRecords) {
            String account = messageRecord.getFriend().getAccount();
            if (StringUtils.equal(account, friend.getAccount())) {
                messageRecord.setWith(friend);
            }
        }
        notifyDataSetChanged();
    }

    public void deleteRecord(Friend friend) {
        for (MessageRecord messageRecord : messageRecords) {
            String account = messageRecord.getFriend().getAccount();
            if (StringUtils.equal(account, friend.getAccount())) {
                messageRecords.remove(messageRecord);
                break;
            }
        }
        notifyDataSetChanged();
    }
    public void clear() {
        messageRecords.clear();
        notifyDataSetChanged();
    }

}

