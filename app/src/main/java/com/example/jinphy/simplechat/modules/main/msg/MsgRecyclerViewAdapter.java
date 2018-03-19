package com.example.jinphy.simplechat.modules.main.msg;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseAdapter;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.StringUtils;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jinphy on 2017/8/10.
 */

public class MsgRecyclerViewAdapter extends BaseAdapter<MessageRecord ,MsgRecyclerViewAdapter.ViewHolder> {


    @Override
    public void onBindViewHolder(ViewHolder holder, MessageRecord item, int position) {

        holder.name.setText(item.getName());
        holder.lastMsg.setText(item.getMsg());
        holder.time.setText(item.getTime());

        if (Friend.system.equals(item.getWith())) {
            holder.avatar.setImageResource(R.drawable.ic_system_24dp);
        } else {
            Bitmap bitmap = ImageUtil.loadAvatar(item.getWith(),
                    holder.avatar.getMeasuredWidth(), holder.avatar.getMeasuredHeight());
            if (bitmap != null) {
                holder.avatar.setImageBitmap(bitmap);
            } else if (item.getWith().contains("G")) {
                holder.avatar.setImageResource(R.drawable.ic_group_chat_white_24dp);
            } else {
                holder.avatar.setImageResource(R.drawable.ic_person_48dp);
            }

        }


        // 设置未读消息数
        int count = item.getNewMsgCount();
        if (count == 0) {
            holder.count.setVisibility(View.GONE);
        } else {
            holder.count.setVisibility(View.VISIBLE);
            if (count < 100) {
                holder.count.setText(count+"");
            } else {
                holder.count.setText("99+");
            }
        }
        // 设置是否有置顶
        holder.top.setVisibility(item.getToTop() == 1 ? View.VISIBLE : View.GONE);

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


    //===================================================================\\
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView avatar;
        private TextView name;
        private TextView lastMsg;
        private TextView time;
        private View itemView;
        private TextView count;
        private View top;


        public ViewHolder(View itemView) {
            super(itemView);
            this.avatar = itemView.findViewById(R.id.avatar);
            this.name = itemView.findViewById(R.id.name);
            this.lastMsg = itemView.findViewById(R.id.last_msg);
            this.time = itemView.findViewById(R.id.time);
            this.itemView = itemView;
            this.count = itemView.findViewById(R.id.new_count);
            this.top = itemView.findViewById(R.id.top);
        }
    }

    /**
     * DESC: 跟新消息记录中对应的好友依赖
     * Created by jinphy, on 2018/3/3, at 13:23
     */
    public void updateFriend(Friend friend) {
        for (MessageRecord messageRecord : data) {
            String account = messageRecord.getFriend().getAccount();
            if (StringUtils.equal(account, friend.getAccount())) {
                messageRecord.setWith(friend);
            }
        }
        notifyDataSetChanged();
    }

    public void deleteRecord(Friend friend) {
        for (MessageRecord messageRecord : data) {
            String account = messageRecord.getFriend().getAccount();
            if (StringUtils.equal(account, friend.getAccount())) {
                data.remove(messageRecord);
                break;
            }
        }
        notifyDataSetChanged();
    }
    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

}

