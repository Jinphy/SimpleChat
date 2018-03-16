package com.example.jinphy.simplechat.modules.group.member_list;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseRecyclerViewAdapter;
import com.example.jinphy.simplechat.custom_libs.SChain;
import com.example.jinphy.simplechat.models.member.CheckableMember;
import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * DESC:
 * Created by jinphy on 2018/3/14.
 */

public class MemberListAdapter extends BaseRecyclerViewAdapter<CheckableMember, MemberListAdapter.ViewHolder> {


    private boolean showCheckbox;

    private boolean selectAll;

    public MemberListAdapter() {
        super();
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, CheckableMember item, int position) {
        Bitmap bitmap = ImageUtil.loadAvatar(item.getAccount(), 100, 100);
        if (bitmap != null) {
            holder.avatar.setImageBitmap(bitmap);
        } else {
            holder.avatar.setImageResource(R.drawable.ic_person_48dp);
        }
        holder.name.setText(item.getName());

        if (item.isAllowChat()) {
            holder.notAllowChat.setVisibility(View.GONE);
        } else {
            holder.notAllowChat.setVisibility(View.VISIBLE);
        }
        holder.account.setText(item.getAccount());

        if (showCheckbox) {
            holder.btnMore.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(item.isChecked());
        } else {
            holder.btnMore.setVisibility(View.VISIBLE);
            holder.checkBox.setVisibility(View.GONE);
        }


        setClick(item, position, 0, holder.itemView, holder.btnMore);

        setLongClick(item, position, 0, holder.itemView);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
        });
    }

    @Override
    protected int getResourceId() {
        return R.layout.layout_member_list_item;
    }

    @Override
    protected ViewHolder onCreateViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }


    /**
     * DESC: 长按成员项时调用该方法
     * Created by jinphy, on 2018/3/15, at 19:29
     */
    public boolean showCheckBox() {
        this.showCheckbox = !showCheckbox;
        if (showCheckbox) {
            // 当显示CheckBox时需要重置所有的选中状态为false
            setAll(false);
        }
        notifyDataSetChanged();
        return showCheckbox;
    }

    private void setAll(boolean checked) {
        for (CheckableMember item : data) {
            item.setChecked(checked);
        }
    }

    /**
     * DESC: 设置是否全选
     * Created by jinphy, on 2018/3/15, at 21:32
     */
    public boolean setSelectAll() {
        if (showCheckbox) {
            selectAll = !selectAll;
            setAll(selectAll);
            notifyDataSetChanged();
        }
        return selectAll;
    }


    /**
     * DESC: 获取选中的成员项的account
     * Created by jinphy, on 2018/3/15, at 19:36
     */
    public List<String> getCheckedAccounts() {
        List<String> result = new LinkedList<>();
        for (CheckableMember item : data) {
            if (item.isChecked()) {
                result.add(item.getAccount());
            }
        }

        return result;
    }


    public boolean isShowCheckbox() {
        return showCheckbox;
    }

    public boolean check(View itemView) {
        CheckBox checkBox = itemView.findViewById(R.id.check_box);
        boolean checked = !checkBox.isChecked();
        checkBox.setChecked(checked);
        return checked;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView avatar;
        TextView name;
        TextView account;
        View btnMore;
        CheckBox checkBox;
        View notAllowChat;
        public ViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar_view);
            name = itemView.findViewById(R.id.name_view);
            account = itemView.findViewById(R.id.account_view);
            btnMore = itemView.findViewById(R.id.more_view);
            checkBox = itemView.findViewById(R.id.check_box);
            notAllowChat = itemView.findViewById(R.id.not_allow_chat_view);
        }
    }
}

