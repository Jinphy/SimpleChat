package com.example.jinphy.simplechat.modules.group;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseRecyclerViewAdapter;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.utils.ImageUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * DESC:
 * Created by jinphy on 2018/3/9.
 */

public class GroupMemberAdapter extends BaseRecyclerViewAdapter<GroupMemberAdapter.ViewHolder> {

    private List<Friend> friends;
    private Map<Friend, Boolean> checks;

    public GroupMemberAdapter() {
        checks = new HashMap<>();
        friends = new LinkedList<>();
    }

    public void update(List<Friend> friends) {
        if (friends == null || friends.size() == 0) {
            return;
        }
        this.friends.clear();
        this.friends.addAll(friends);
        checks.clear();
        for (Friend friend : this.friends) {
            checks.put(friend, false);
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_group_member_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Friend friend = friends.get(position);
        Bitmap bitmap = ImageUtil.loadAvatar(friend.getAccount(), 100, 100);
        if (bitmap != null) {
            holder.avatar.setImageBitmap(bitmap);
        } else {
            holder.avatar.setImageResource(R.drawable.ic_person_48dp);
        }
        holder.name.setText(friend.getShowName());
        holder.check.setVisibility(checks.get(friend) ? View.VISIBLE : View.GONE);

        if (click != null) {
            holder.clickView.setOnClickListener(v -> click.onClick(v, friend, 0, position));
        }
    }

    /**
     * DESC: 改变选中状态
     * Created by jinphy, on 2018/3/9, at 23:34
     */
    public void updateCheckStatus(Friend friend,View view) {
        Boolean check = !checks.get(friend);
        checks.put(friend, check);
        view.findViewById(R.id.check_view).setVisibility(check ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView avatar;
        TextView name;
        View check;
        View clickView;

        public ViewHolder(View clickView) {
            super(clickView);
            this.clickView = clickView.findViewById(R.id.click_view);
            avatar = clickView.findViewById(R.id.avatar_view);
            name = clickView.findViewById(R.id.name_view);
            check = clickView.findViewById(R.id.check_view);
        }
    }

    /**
     * DESC: 获取所有勾选了的好友的账号
     * Created by jinphy, on 2018/3/10, at 8:59
     */
    public List<String> getCheckedAccount() {
        LinkedList<String> checkeds = new LinkedList<>();
        for (Map.Entry<Friend, Boolean> check : this.checks.entrySet()) {
            if (check.getValue()) {
                checkeds.add(check.getKey().getAccount());
            }
        }
        return checkeds;
    }
}
