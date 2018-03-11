package com.example.jinphy.simplechat.modules.main.friends;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseRecyclerViewAdapter;
import com.example.jinphy.simplechat.models.event_bus.EBFriend;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jinphy on 2017/8/10.
 */

public class FriendsRecyclerViewAdapter extends BaseRecyclerViewAdapter<FriendsRecyclerViewAdapter.ViewHolder> {

    private List<Friend> friends;

    public FriendsRecyclerViewAdapter() {
        this.friends = new ArrayList<>();
    }

    @Override
    public FriendsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_tab_friends_item, parent, false);

        return new FriendsRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Friend friend = friends.get(position);
        holder.remark.setText(friend.getRemark());
        holder.account.setText(friend.getAccount());
        holder.address.setText(friend.getAddress());
        if (!TextUtils.isEmpty(friend.getDate())) {
            holder.date.setText(StringUtils.formatDate(Long.valueOf(friend.getDate())));
        }

        Bitmap bitmap = ImageUtil.loadAvatar(
                friend.getAccount(),
                holder.avatar.getMeasuredWidth(),
                holder.avatar.getMeasuredHeight());
        if (bitmap != null) {
            holder.avatar.setImageBitmap(bitmap);
        }

        if (click != null) {
            holder.itemView.setOnClickListener(view -> click.onClick(view,friend,0,position));
        }
        if (longClick != null) {
            holder.itemView.setOnLongClickListener(view -> longClick.onLongClick(view,friend,0,position));
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }


    public void update(List<Friend> friends) {
        this.friends.clear();
        if (friends == null) {
            return;
        }
        this.friends.addAll(friends);
        notifyDataSetChanged();
    }

    //===============================================================\\
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView avatar;
        private TextView remark;
        private TextView account;
        private TextView address;
        private TextView date;
        private View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.avatar = itemView.findViewById(R.id.avatar);
            this.remark = itemView.findViewById(R.id.remark);
            this.account = itemView.findViewById(R.id.account);
            this.address = itemView.findViewById(R.id.address);
            this.date = itemView.findViewById(R.id.date);
        }
    }

    public void updateFriend(Friend friend) {
        for (int i = 0; i < friends.size(); i++) {
            LogUtils.e(friend.getAccount());
            LogUtils.e(friends.get(i).getAccount());
            if (StringUtils.equal(friend.getAccount(), friends.get(i).getAccount())) {
                friends.set(i, friend);
                notifyDataSetChanged();
                return;
            }
        }
        friends.add(friend);
        notifyDataSetChanged();
    }

    public void clear() {
        friends.clear();
        notifyDataSetChanged();
    }
}

