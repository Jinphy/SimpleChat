package com.example.jinphy.simplechat.modules.main.friends;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseAdapter;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.StringUtils;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jinphy on 2017/8/10.
 */

public class FriendsRecyclerViewAdapter extends BaseAdapter<Friend, FriendsRecyclerViewAdapter.ViewHolder> {


    @Override
    public void onBindViewHolder(ViewHolder holder, Friend friend, int position) {
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

        setClick(friend, position, 0, holder.itemView);

        setLongClick(friend, position, 0, holder.itemView);



    }

    @Override
    protected int getResourceId(int viewType) {
        return R.layout.main_tab_friends_item;
    }

    @Override
    protected ViewHolder onCreateViewHolder(View itemView) {
        return new ViewHolder(itemView);
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
        for (int i = 0; i < data.size(); i++) {
            LogUtils.e(friend.getAccount());
            LogUtils.e(data.get(i).getAccount());
            if (StringUtils.equal(friend.getAccount(), data.get(i).getAccount())) {
                data.set(i, friend);
                notifyDataSetChanged();
                return;
            }
        }
        data.add(friend);
        notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }
}

