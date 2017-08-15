package com.example.jinphy.simplechat.modules.main.friends;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseRecyclerViewAdapter;
import com.example.jinphy.simplechat.model.Friend;
import com.example.jinphy.simplechat.model.MsgRecord;
import com.example.jinphy.simplechat.modules.main.msg.MsgRecyclerViewAdapter;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jinphy on 2017/8/10.
 */

public class FriendsRecyclerViewAdapter extends BaseRecyclerViewAdapter<FriendsRecyclerViewAdapter.ViewHolder> {

    private List<Friend> friends;

    public FriendsRecyclerViewAdapter(@NonNull List<Friend> friends) {

        this.friends = Preconditions.checkNotNull(friends);
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
        // TODO: 2017/8/10 设置avatar等信息

        if (click != null) {
            holder.avatar.setOnClickListener(view -> click.onClick(view,friend,0,position));
            holder.itemView.setOnClickListener(view -> click.onClick(view,friend,0,position));
        }
        if (longClick != null) {
            holder.avatar.setOnLongClickListener(view -> longClick.onLongClick(view,friend,0,position));
            holder.itemView.setOnLongClickListener(view -> longClick.onLongClick(view,friend,0,position));
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
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

