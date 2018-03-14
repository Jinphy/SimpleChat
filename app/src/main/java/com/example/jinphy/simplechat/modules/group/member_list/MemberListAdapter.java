package com.example.jinphy.simplechat.modules.group.member_list;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseRecyclerViewAdapter;
import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.utils.ImageUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * DESC:
 * Created by jinphy on 2018/3/14.
 */

public class MemberListAdapter extends BaseRecyclerViewAdapter<Member, MemberListAdapter.ViewHolder> {



    @Override
    protected void onBindViewHolder(ViewHolder holder, Member item, int position) {
        Bitmap bitmap = ImageUtil.loadAvatar(item.getAccount(), 100, 100);
        if (bitmap != null) {
            holder.avatar.setImageBitmap(bitmap);
        } else {
            holder.avatar.setImageResource(R.drawable.ic_person_48dp);
        }

        holder.name.setText(item.getName());
        holder.account.setText(item.getAccount());

        setClick(item, position, 0, holder.itemView, holder.btnMore);

        setLongClick(item, position, 0, holder.itemView, holder.btnMore);
    }

    @Override
    protected int getResourceId() {
        return R.layout.layout_member_list_item;
    }

    @Override
    protected ViewHolder onCreateViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView avatar;
        TextView name;
        TextView account;
        View btnMore;
        public ViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar_view);
            name = itemView.findViewById(R.id.name_view);
            account = itemView.findViewById(R.id.account_view);
            btnMore = itemView.findViewById(R.id.more_view);
        }
    }
}

