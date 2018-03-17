package com.example.jinphy.simplechat.modules.system_msg.new_friend;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseAdapter;
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

public class NewFriendRecyclerViewAdapter extends BaseAdapter<NewFriendRecyclerViewAdapter.NewFriend,NewFriendRecyclerViewAdapter.ViewHolder> {



    @Override
    public void onBindViewHolder(ViewHolder holder, NewFriend item, int position) {
        if (!"无".equals(item.friend.getAvatar())) {
            Bitmap bitmap = ImageUtil.loadAvatar(
                    item.friend.getAccount(),
                    holder.avatar.getMeasuredWidth(),
                    holder.avatar.getMeasuredHeight());
            if (bitmap != null) {
                holder.avatar.setImageBitmap(bitmap);
            }
        }

        String name = "暂无昵称";
        if (!TextUtils.isEmpty(item.friend.getRemark())) {
            name = item.friend.getRemark();
        } else if (!TextUtils.isEmpty(item.friend.getName())) {
            name = item.friend.getName();
        }
        holder.name.setText(name);
        holder.msg.setText(item.message.getContent());
        holder.time.setText(StringUtils.formatDate(Long.valueOf(item.message.getCreateTime())));

        if (item.message.isNew()) {
            holder.newMsgView.setVisibility(View.VISIBLE);
        } else {
            holder.newMsgView.setVisibility(View.GONE);
        }


        setClick(item, position, 0, holder.itemView);

        setLongClick(item, position, 0, holder.itemView);

    }

    @Override
    protected int getResourceId(int viewType) {
        return R.layout.main_tab_msg_item;
    }

    @Override
    protected ViewHolder onCreateViewHolder(View itemView) {
        return new ViewHolder(itemView);
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
        data.clear();
        notifyDataSetChanged();
    }

    public void update(List<NewFriend> newFriends) {
        this.data.addAll(newFriends);
        notifyDataSetChanged();
    }

    public List<Message> getNewMsgAndSetOld() {
        List<Message> result = new LinkedList<>();
        for (NewFriend newFriend : data) {
            if (newFriend.message.isNew()) {
                newFriend.message.setNew(false);
                result.add(newFriend.message);
            }
        }
        return result;
    }


}
