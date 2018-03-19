package com.example.jinphy.simplechat.modules.group.group_list;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseAdapter;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.utils.ImageUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * DESC:
 * Created by jinphy on 2018/3/11.
 */

public class GroupListAdapter extends BaseAdapter<Group, GroupListAdapter.ViewHolder> {


    @Override
    public void onBindViewHolder(ViewHolder holder, Group item, int position) {
        Bitmap bitmap = ImageUtil.loadAvatar(item.getGroupNo(), 100, 100);
        if (bitmap != null) {
            holder.avatar.setImageBitmap(bitmap);
        } else {
            holder.avatar.setImageResource(R.drawable.ic_group_chat_white_24dp);
        }
        holder.name.setText(item.getName());
        holder.groupNo.setText(item.getGroupNo());

        setClick(item, position, 0, holder.itemView, holder.qRCode);

        setLongClick(item, position, 0, holder.itemView);
    }

    @Override
    protected int getResourceId(int viewType) {
        return R.layout.layout_group_list_item;
    }

    @Override
    protected ViewHolder onCreateViewHolder(View itemView) {
        return new ViewHolder(itemView);
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
