package com.example.jinphy.simplechat.modules.chat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.model.Message;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jinphy on 2017/8/13.
 */

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter{

    private List<Message> messages;

    ChatRecyclerViewAdapter(@NonNull List<Message> messages) {
        this.messages = Preconditions.checkNotNull(messages);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int resourceId = 0;
        switch (Message.parseSourceType(viewType)) {
            case Message.TYPE_RECEIVE:
                resourceId = R.layout.chat_receive_msg_item;
                break;
            case Message.TYPE_SEND:
                resourceId = R.layout.chat_send_msg_item;
                break;
            default:
                throw new IllegalArgumentException(
                        "the resourceType of message item is illegal,sourceType="+
                Message.parseSourceType(viewType));
        }

        View itemView = LayoutInflater.from(parent.getContext()).inflate(resourceId, parent, false);

        return new ViewHolder(itemView,viewType);
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder tempHolder, int position) {
        ViewHolder holder = ((ViewHolder) tempHolder);
        Message message = messages.get(position);

        bindCommonView(holder,message,position);

        switch (Message.parseContentType(holder.type)) {
            case Message.TYPE_TEXT:
                bindTextMsgView(holder,message);
                break;
            case Message.TYPE_PHOTO:
                bindPhotoMsgView(holder,message);
                break;
            case Message.TYPE_VOICE:
                bindVoiceMsgView(holder,message);
                break;
            case Message.TYPE_VIDEO:
                bindVideoMsgView(holder,message);
                break;
            case Message.TYPE_FILE:
                bindFileMsgView(holder,message);
                break;
            default:
                throw new IllegalArgumentException(
                        "the contentType of message item is illegal,contentType="+
                                Message.parseContentType(holder.type));
        }
    }

    // 绑定item中公共部分的View
    private void bindCommonView(ViewHolder holder, Message message,int position) {
        // TODO: 2017/8/13 设置头像 ，时间，
        if (click != null) {
            holder.avatar.setOnClickListener(view ->
                    click.onClick(view,message,holder.type,position));
            holder.content.setOnClickListener(view ->
                    click.onClick(view, message, holder.type, position));
        }
        if (longClick != null) {
            holder.avatar.setOnLongClickListener(view ->
            longClick.onLongClick(view,message,holder.type,position));
            holder.content.setOnLongClickListener(view ->
            longClick.onLongClick(view,message,holder.type,position));
        }
    }

    private void bindTextMsgView(ViewHolder holder, Message message) {
        holder.textMsg_content.setVisibility(View.VISIBLE);
        // TODO: 2017/8/13
        //holder.textMsg_content.setText(message.getContent());

    }

    private void bindPhotoMsgView(ViewHolder holder, Message message) {
        holder.photoMsg_picture.setVisibility(View.VISIBLE);
        // TODO: 2017/8/13
    }

    private void bindVoiceMsgView(ViewHolder holder, Message message) {
        holder.voiceMsg_root.setVisibility(View.VISIBLE);
        // TODO: 2017/8/13
    }

    private void bindVideoMsgView(ViewHolder holder, Message message) {
        holder.videoMsg_root.setVisibility(View.VISIBLE);
        // TODO: 2017/8/13
    }

    private void bindFileMsgView(ViewHolder holder, Message message) {
        holder.fileMsg_root.setVisibility(View.VISIBLE);
        // TODO: 2017/8/13
    }




    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).combineType();
    }




    private OnClickListener click;
    private OnLongClickListener longClick;

    public ChatRecyclerViewAdapter onClick(OnClickListener listener) {
        this.click = listener;
        return this;
    }

    public ChatRecyclerViewAdapter onLongClick(OnLongClickListener listener) {
        this.longClick = listener;
        return this;
    }

    //==============================================================\\
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // 公共部分
        private TextView time;
        private CircleImageView avatar;//头像的设置和点击View
        private View content;// item的点击事件View
        private View itemView;// 整个item，这里暂无点击功能
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
            this.time = itemView.findViewById(R.id.time_text);
            this.avatar = itemView.findViewById(R.id.avatar_view);
            this.content = itemView.findViewById(R.id.content_layout);

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



    public interface OnClickListener {

        void onClick(View view, Message item,int type,int position);
    }

    public interface OnLongClickListener {

        boolean onLongClick(View view, Message item,int type,int position);
    }
}

