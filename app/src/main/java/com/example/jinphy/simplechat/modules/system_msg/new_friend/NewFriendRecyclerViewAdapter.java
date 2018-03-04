package com.example.jinphy.simplechat.modules.system_msg.new_friend;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseRecyclerViewAdapter;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * DESC:
 * Created by jinphy on 2018/3/2.
 */

public class NewFriendRecyclerViewAdapter extends BaseRecyclerViewAdapter<NewFriendRecyclerViewAdapter.ViewHolder> {

    private List<NewFriend> newFriends;

    public NewFriendRecyclerViewAdapter() {
        this.newFriends = new LinkedList<>();
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
        NewFriend newFriend = newFriends.get(position);
        if (!"无".equals(newFriend.friend.getAvatar())) {
            Bitmap bitmap = ImageUtil.loadAvatar(
                    newFriend.friend.getAccount(),
                    holder.avatar.getMeasuredWidth(),
                    holder.avatar.getMeasuredHeight());
            if (bitmap != null) {
                holder.avatar.setImageBitmap(bitmap);
            }
        }

        String name = "暂无昵称";
        if (!TextUtils.isEmpty(newFriend.friend.getRemark())) {
            name = newFriend.friend.getRemark();
        } else if (!TextUtils.isEmpty(newFriend.friend.getName())) {
            name = newFriend.friend.getName();
        }
        holder.name.setText(name);
        holder.msg.setText(newFriend.message.getContent());
        holder.time.setText(StringUtils.formatDate(Long.valueOf(newFriend.message.getCreateTime())));

        if (newFriend.message.isNew()) {
            holder.newMsgView.setVisibility(View.VISIBLE);
        } else {
            holder.newMsgView.setVisibility(View.GONE);
        }


        if (click != null) {
            holder.itemView.setOnClickListener(v -> click.onClick(v, newFriend,0,position));
        }
        if (longClick != null) {
            holder.itemView.setOnLongClickListener(v -> longClick.onLongClick(v, newFriend,0,position));
        }
    }

    @Override
    public int getItemCount() {
        return newFriends.size();
    }


    public static class NewFriend{
        public Friend friend;
        public Message message;

        public static NewFriend create(Friend friend, Message message) {
            NewFriend newFriend = new NewFriend();
            newFriend.friend = friend;
            newFriend.message = message;
            return newFriend;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView avatar;
        private TextView name;
        private TextView msg;
        private TextView time;
        private View itemView;
        private View newMsgView;


        public ViewHolder(View itemView) {
            super(itemView);
            this.avatar = itemView.findViewById(R.id.avatar);
            this.name = itemView.findViewById(R.id.name);
            this.time = itemView.findViewById(R.id.time);
            this.msg = itemView.findViewById(R.id.last_msg);
            this.newMsgView = itemView.findViewById(R.id.top);
            this.itemView = itemView;
        }
    }

    public void clear() {
        newFriends.clear();
        notifyDataSetChanged();
    }

    public void update(List<NewFriend> newFriends) {
        this.newFriends.addAll(newFriends);
        notifyDataSetChanged();
    }

    public List<Message> getNewMsgAndSetOld() {
        List<Message> result = new LinkedList<>();
        for (NewFriend newFriend : newFriends) {
            if (newFriend.message.isNew()) {
                newFriend.message.setNew(false);
                result.add(newFriend.message);
            }
        }
        return result;
    }


}
