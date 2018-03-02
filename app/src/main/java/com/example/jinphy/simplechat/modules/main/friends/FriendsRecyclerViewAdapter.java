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

        if (!"无".equals(friend.getAvatar())){
            int w = holder.avatar.getMeasuredWidth();
            int h = holder.avatar.getMeasuredHeight();
            Bitmap bitmap = ImageUtil.loadAvatar(friend.getAccount(), w, h);
            if (bitmap != null) {
                holder.avatar.setImageBitmap(bitmap);
            } else {
                if (!"loading".equals(friend.getAvatar())) {
                    friend.setAvatar("loading");
                    EventBus.getDefault().post(new EBFriend(friend));
                }

            }
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

}

