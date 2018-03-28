package com.example.jinphy.simplechat.modules.chat;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseAdapter;
import com.example.jinphy.simplechat.custom_libs.SChain;
import com.example.jinphy.simplechat.models.event_bus.EBMessage;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.utils.FileUtils;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jinphy on 2017/8/13.
 */

public class ChatAdapter extends BaseAdapter<Message, ChatAdapter.ViewHolder> {


    private Bitmap ownerAvatar;
    private Bitmap friendAvatar;
    private String withAccount;

    private Map<String, Member> memberMap = new HashMap<>();
    private Map<String, SoftReference<Bitmap>> avatarMap = new HashMap<>();

    private Map<String, SoftReference<Bitmap>> thumbnailMap = new HashMap<>();

    private Group group;



    ChatAdapter(String ownerAvatar, String withAccount) {
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
        if (Message.SEND == message.getSourceType()) {
            if (ownerAvatar != null) {
                holder.avatarView.setImageBitmap(ownerAvatar);
            }
            if (Message.STATUS_NO.equals(message.getStatus())) {
                holder.statusView.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.GONE);
            } else if (Message.STATUS_OK.equals(message.getStatus())) {
                holder.statusView.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.GONE);
            } else {
                holder.statusView.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.VISIBLE);
            }

        } else {
            if (withAccount.contains("G")) {
                // 群聊
                // 设置头像
                String sender = message.extra(Message.KEY_SENDER);
                Bitmap bitmap = getAvatar(sender);
                if (bitmap != null) {
                    holder.avatarView.setImageBitmap(bitmap);
                }

                // 设置名字
                if (group.isShowMemberName() && memberMap != null) {
                    holder.nameView.setVisibility(View.VISIBLE);
                    Member member = memberMap.get(sender);
                    if (member != null) {
                        holder.nameView.setText(member.getName());
                    } else {
                        holder.nameView.setText("");
                    }
                } else {
                    holder.nameView.setVisibility(View.GONE);
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
        setClick(message, position, holder.type, holder.avatarView, holder.contentView);
        setLongClick(message, position, holder.type, holder.avatarView, holder.contentView);
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
        Bitmap bitmap = getThumbnail(message);
        if (bitmap != null) {
            holder.photoMsg_picture.setImageBitmap(bitmap);
        } else {
            holder.photoMsg_picture.setImageResource(R.drawable.ic_photo_24dp);
        }
    }

    private void bindVoiceMsgView(ViewHolder holder, Message message) {
        holder.textMsg_content.setVisibility(View.GONE);
        holder.photoMsg_picture.setVisibility(View.GONE);
        holder.voiceMsg_root.setVisibility(View.VISIBLE);
        holder.videoMsg_root.setVisibility(View.GONE);
        holder.fileMsg_root.setVisibility(View.GONE);
        // TODO: 2017/8/13
        holder.voiceMsg_seconds.setText(message.extra(Message.KEY_DURATION));
        if (Message.RECEIVE == message.getSourceType()) {
            String extra = message.extra(Message.KEY_AUDIO_STATUS);
            switch (extra) {
                case Message.AUDIO_STATUS_NO:
                    EventBus.getDefault().post(EBMessage.downloadVoice(message));
                    holder.voiceMsg_root.setBackgroundColor(0x7FC2AC19);
                    break;
                case Message.AUDIO_STATUS_DOWNLOADING:
                    holder.voiceMsg_root.setBackgroundColor(0x7FC2AC19);
                    break;
                case Message.AUDIO_STATUS_NEW:
                    holder.voiceMsg_root.setBackgroundColor(0x7F3F921B);
                    break;
                case Message.AUDIO_STATUS_OLD:
                    holder.voiceMsg_root.setBackgroundColor(Color.TRANSPARENT);
                    break;
            }
        }

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
        holder.fileMsg_fileName.setText(message.extra(Message.KEY_ORIGINAL_FILE_NAME));
        String fileSize = message.extra(Message.KEY_TOTAL_LENGTH);
        if (ObjectHelper.isEmpty(fileSize)) {
            fileSize = "0";
        }
        holder.fileMsg_fileSize.setText(FileUtils.formatSize(Long.valueOf(fileSize)));
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


    public void update(int position, Message message) {
        thumbnailMap.remove(message.extra(Message.KEY_THUMBNAIL_ID));
        getThumbnail(message);
        data.set(position, message);
        notifyDataSetChanged();
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
        private TextView nameView;
        private ProgressBar progressBar;

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
        TextView fileMsg_fileSize;




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
                progressBar = itemView.findViewById(R.id.progress_bar);
            } else {
                this.nameView = itemView.findViewById(R.id.name_view);
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
            this.fileMsg_fileSize = itemView.findViewById(R.id.file_size_text);

        }
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public synchronized void add(Message... messages) {
        if (messages.length > 0) {

            for (Message message : messages) {
                if (message != null) {
                    data.add(message);
                }
            }
            notifyDataSetChanged();
        }
    }

    public Message getLast() {
        int index = data.size() - 1;
        if (index >= 0) {
            return data.get(index);
        } else {
            return null;
        }
    }


    public void setMembers(List<Member> members) {
        if (ObjectHelper.isEmpty(members)) {
            return;
        }
        memberMap.clear();
        for (Member member : members) {
            memberMap.put(member.getAccount(), member);
        }
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Bitmap getAvatar(String account) {
        SoftReference<Bitmap> avatar = avatarMap.get(account);
        if (ObjectHelper.reference(avatar)) {
            return avatar.get();
        }
        Bitmap bitmap = ImageUtil.loadAvatar(account, 30, 30);
        if (bitmap != null) {
            avatarMap.put(account, new SoftReference<>(bitmap));
        }
        return bitmap;
    }

    public Bitmap getThumbnail(Message message) {
        String thumbnailId = message.extra(Message.KEY_THUMBNAIL_ID);
        SoftReference<Bitmap> bitmapSoft = thumbnailMap.get(thumbnailId);
        if (ObjectHelper.reference(bitmapSoft)) {
            return bitmapSoft.get();
        }
        Bitmap bitmap = ImageUtil.getBitmap(message.extra(Message.KEY_FILE_PATH), 200, 200);
        if (bitmap != null) {
            thumbnailMap.put(thumbnailId, new SoftReference<>(bitmap));
            return bitmap;
        }

        String thumbnail = message.extra(Message.KEY_THUMBNAIL);
        if (thumbnail != null) {
            bitmap = StringUtils.base64ToBitmap(thumbnail);
            if (bitmap != null) {
                thumbnailMap.put(thumbnailId, new SoftReference<>(bitmap));
            }
        }
        return bitmap;
    }

    /**
     * DESC: 判断是否有图片，如果图片之前下载过，但是被删除了则返回false
     * Created by jinphy, on 2018/3/24, at 15:29
     */
    public boolean hasPhoto(Message message) {
        String thumbnailId = message.extra(Message.KEY_THUMBNAIL_ID);
        return thumbnailMap.get(thumbnailId) != null;
    }

    /**
     * DESC: 判断文件是否存在
     * Created by jinphy, on 2018/3/28, at 17:20
     */
    public boolean hasFile(Message message) {
        File file = new File(message.extra(Message.KEY_FILE_PATH));
        if (Message.SEND == message.getSourceType()) {
            return file.exists();
        }
        if (Message.FILE_STATUS_DOWNLOADED.equals(message.extra(Message.KEY_FILE_STATUS))
                && !file.exists()) {
            return false;
        }
        return true;
    }


    public List<Message> getData() {
        return data;
    }

    public void forEach(SChain.Consumer<Message> each) {
        for (Message message : data) {
            each.accept(message);
        }
    }

    public void update(Message newMsg) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId() == newMsg.getId()) {
                data.set(i, newMsg);
                notifyDataSetChanged();
                break;
            }
        }
    }
}

