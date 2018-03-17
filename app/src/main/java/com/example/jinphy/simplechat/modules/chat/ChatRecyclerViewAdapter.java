package com.example.jinphy.simplechat.modules.chat;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseAdapter;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jinphy on 2017/8/13.
 */

public class ChatRecyclerViewAdapter extends BaseAdapter<Message, ChatRecyclerViewAdapter.ViewHolder> {


    private Bitmap ownerAvatar;
    private Bitmap friendAvatar;
    private String withAccount;

    ChatRecyclerViewAdapter(String ownerAvatar,String withAccount) {
        super();
        this.withAccount = withAccount;
        this.ownerAvatar = StringUtils.base64ToBitmap(ownerAvatar);
        if (!withAccount.contains("G")) {
            friendAvatar = ImageUtil.loadAvatar(withAccount, 500, 500);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int resourceId = 0;
        switch (viewType) {
            case Message.RECEIVE:
                resourceId = R.layout.chat_receive_msg_item;
                break;
            case Message.SEND:
                resourceId = R.layout.chat_send_msg_item;
                break;
            default:
                ObjectHelper.throwRuntime("you must specify the message type!");
        }

        View itemView = LayoutInflater.from(parent.getContext()).inflate(resourceId, parent, false);

        return new ViewHolder(itemView,viewType);
    }

    @Override
    protected int getResourceId(int viewType) {
        return 0;
    }

    @Override
    protected ViewHolder onCreateViewHolder(View itemView) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Message item, int position) {

        bindCommonView(holder, item, position);
        switch (item.getContentType()) {
            case Message.TYPE_CHAT_TEXT:
                bindTextMsgView(holder, item);
                break;
            case Message.TYPE_CHAT_IMAGE:
                bindPhotoMsgView(holder, item);
                break;
            case Message.TYPE_CHAT_VOICE:
                bindVoiceMsgView(holder, item);
                break;
            case Message.TYPE_CHAT_VIDEO:
                bindVideoMsgView(holder, item);
                break;
            case Message.TYPE_CHAT_FILE:
                bindFileMsgView(holder, item);
                break;
            default:
                ObjectHelper.throwRuntime("unknown item type!");
        }
    }

    // 绑定item中公共部分的View
    private void bindCommonView(ViewHolder holder, Message message,int position) {
        // TODO: 2017/8/13 设置头像 ，时间，
        if (Message.SEND == message.getSourceType()) {
            if (ownerAvatar != null) {
                holder.avatarView.setImageBitmap(ownerAvatar);
            }
            if (Message.STATUS_NO.equals(message.getStatus())) {
                holder.statusView.setVisibility(View.VISIBLE);
            } else {
                holder.statusView.setVisibility(View.GONE);
            }

        } else {
            if (withAccount.contains("G")) {
                Bitmap bitmap = ImageUtil.loadAvatar(message.getExtra(), 50, 50);
                if (bitmap != null) {
                    holder.avatarView.setImageBitmap(bitmap);
                }
            } else {
                if (friendAvatar != null) {
                    holder.avatarView.setImageBitmap(friendAvatar);
                }
            }

        }
        if (position == 0) {
            holder.timeView.setVisibility(View.VISIBLE);
            holder.timeView.setText(StringUtils.formatDate(Long.valueOf(message.getCreateTime())));
        } else {
            Message preMsg = data.get(position - 1);
            Long preTime = Long.valueOf(preMsg.getCreateTime());
            Long time = Long.valueOf(message.getCreateTime());
            if (time - preTime > 300_000) {
                // 时间间隔超过5分钟，则显示时间
                holder.timeView.setVisibility(View.VISIBLE);
                holder.timeView.setText(
                        StringUtils.formatDate(Long.valueOf(message.getCreateTime())));
            } else {
                holder.timeView.setVisibility(View.GONE);
            }
        }
        if (click != null) {
            holder.avatarView.setOnClickListener(view ->
                    click.onClick(view,message,holder.type, position));
            holder.contentView.setOnClickListener(view ->
                    click.onClick(view, message, holder.type, position));
        }
        if (longClick != null) {
            holder.avatarView.setOnLongClickListener(view ->
            longClick.onLongClick(view,message,holder.type,position));
            holder.contentView.setOnLongClickListener(view ->
            longClick.onLongClick(view,message,holder.type,position));
        }

    }

    private void bindTextMsgView(ViewHolder holder, Message message) {
        holder.textMsg_content.setVisibility(View.VISIBLE);
        holder.photoMsg_picture.setVisibility(View.GONE);
        holder.voiceMsg_root.setVisibility(View.GONE);
        holder.videoMsg_root.setVisibility(View.GONE);
        holder.fileMsg_root.setVisibility(View.GONE);
        // TODO: 2017/8/13
        holder.textMsg_content.setText(message.getContent());

    }

    private void bindPhotoMsgView(ViewHolder holder, Message message) {
        holder.textMsg_content.setVisibility(View.GONE);
        holder.photoMsg_picture.setVisibility(View.VISIBLE);
        holder.voiceMsg_root.setVisibility(View.GONE);
        holder.videoMsg_root.setVisibility(View.GONE);
        holder.fileMsg_root.setVisibility(View.GONE);
        // TODO: 2017/8/13
    }

    private void bindVoiceMsgView(ViewHolder holder, Message message) {
        holder.textMsg_content.setVisibility(View.GONE);
        holder.photoMsg_picture.setVisibility(View.GONE);
        holder.voiceMsg_root.setVisibility(View.VISIBLE);
        holder.videoMsg_root.setVisibility(View.GONE);
        holder.fileMsg_root.setVisibility(View.GONE);
        // TODO: 2017/8/13
    }

    private void bindVideoMsgView(ViewHolder holder, Message message) {
        holder.textMsg_content.setVisibility(View.GONE);
        holder.photoMsg_picture.setVisibility(View.GONE);
        holder.voiceMsg_root.setVisibility(View.GONE);
        holder.videoMsg_root.setVisibility(View.VISIBLE);
        holder.fileMsg_root.setVisibility(View.GONE);
        // TODO: 2017/8/13
    }

    private void bindFileMsgView(ViewHolder holder, Message message) {
        holder.textMsg_content.setVisibility(View.GONE);
        holder.photoMsg_picture.setVisibility(View.GONE);
        holder.voiceMsg_root.setVisibility(View.GONE);
        holder.videoMsg_root.setVisibility(View.GONE);
        holder.fileMsg_root.setVisibility(View.VISIBLE);
        // TODO: 2017/8/13
    }


    public List<Message> getSendingMsg() {
        List<Message> result = new LinkedList<>();
        for (Message message : data) {
            if (Message.STATUS_SENDING.equals(message.getStatus())) {
                result.add(message);
            }
        }
        return result;
    }


    @Override
    public int getItemViewType(int position) {
        return data.get(position).getSourceType();
    }


    //==============================================================\\
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // 公共部分
        private TextView timeView;
        private CircleImageView avatarView;//头像的设置和点击View
        private View contentView;// item的点击事件View
        private View itemView;// 整个item，这里暂无点击功能
        private View statusView;
        private int type;// 保存消息类型信息

        // 以下几种类型的View中，XXX_root 用来设置可见性，默认情况下都不可见


        // Text msg
        TextView textMsg_content;

        // Photo msg
        ImageView photoMsg_picture;

        // Voice msg
        View voiceMsg_root;
        ImageView voiceMsg_icon;
        TextView voiceMsg_seconds;

        // Video msg
        View videoMsg_root;
        ImageView videoMsg_firstFrame;

        // File msg
        View fileMsg_root;
        TextView fileMsg_fileName;




        public ViewHolder(View itemView,int type) {
            super(itemView);

            // 公共部分
            this.type = type;
            this.itemView = itemView;
            this.timeView = itemView.findViewById(R.id.time_text);
            this.avatarView = itemView.findViewById(R.id.avatar_view);
            this.contentView = itemView.findViewById(R.id.content_layout);
            if (type == Message.SEND) {
                statusView = itemView.findViewById(R.id.status_view);
            }

            // 文本消息
            this.textMsg_content = itemView.findViewById(R.id.text_view);

            // 图片消息
            this.photoMsg_picture = itemView.findViewById(R.id.image_view);

            // 语音消息
            this.voiceMsg_root = itemView.findViewById(R.id.voice_View);
            this.voiceMsg_icon = itemView.findViewById(R.id.icon_view);
            this.voiceMsg_seconds = itemView.findViewById(R.id.seconds_view);

            // 视频消息
            this.videoMsg_root = itemView.findViewById(R.id.video_view);
            this.videoMsg_firstFrame = itemView.findViewById(R.id.first_frame);

            // 文件消息
            this.fileMsg_root = itemView.findViewById(R.id.file_view);
            this.fileMsg_fileName = itemView.findViewById(R.id.file_name_text);

        }
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void add(Message message) {
        data.add(message);
        notifyDataSetChanged();
    }

    public Message getLast() {
        int index = data.size() - 1;
        if (index >= 0) {
            return data.get(index);
        } else {
            return null;
        }
    }

}

