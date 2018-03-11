package com.example.jinphy.simplechat.modules.group.group_list;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseRecyclerViewAdapter;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.modules.group.GroupMemberAdapter;
import com.example.jinphy.simplechat.utils.ImageUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * DESC:
 * Created by jinphy on 2018/3/11.
 */

public class GroupListAdapter extends BaseRecyclerViewAdapter<GroupListAdapter.ViewHolder> {

    private List<Group> groups;

    public GroupListAdapter() {
        groups = new ArrayList<>();
    }

    public void update(List<Group> groups) {
        this.groups.clear();
        if (groups == null || groups.size() == 0) {
            return;
        }
        this.groups.addAll(groups);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_group_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Group group = groups.get(position);
        Bitmap bitmap = ImageUtil.loadAvatar(group.getGroupNo(), 100, 100);
        if (bitmap != null) {
            holder.avatar.setImageBitmap(bitmap);
        } else {
            holder.avatar.setImageResource(R.drawable.ic_group_chat_white_24dp);
        }
        holder.name.setText(group.getName());
        holder.groupNo.setText(group.getGroupNo());

        if (click != null) {
            holder.itemView.setOnClickListener(v -> click.onClick(v, group, 0, position));
            holder.qRCode.setOnClickListener(v -> click.onClick(v, group, 0, position));
        }

        if (longClick != null) {
            holder.itemView.setOnLongClickListener(v -> longClick.onLongClick(v, group, 0, position));
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView avatar;
        TextView name;
        TextView groupNo;
        View qRCode;

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar_view);
            name = itemView.findViewById(R.id.name_view);
            groupNo = itemView.findViewById(R.id.group_no_view);
            qRCode = itemView.findViewById(R.id.qr_code_view);
        }
    }
}
