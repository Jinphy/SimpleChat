package com.example.jinphy.simplechat.modules.show_file;

import android.view.View;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.models.event_bus.EBFileTask;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.utils.FileUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

/**
 * DESC:
 * Created by jinphy, on 2018/3/28, at 16:10
 */
public class ShowFileFragment extends BaseFragment<ShowFilePresenter> implements ShowFileContract.View {

    public static final String TAG_MSG_ID = "MSG_ID";
    public static final String TAG_MSG_POSITION = "POSITION";

    private long msgId;
    private int msgPosition;
    private long totalLength;
    private Message message;

    private TextView filePathView;
    private TextView btnDownload;
    private NumberProgressBar progressBar;

    public ShowFileFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_show_file;
    }

    @Override
    protected void initData() {
        message = presenter.getMessage(msgId);
        totalLength = Long.valueOf(message.extra(Message.KEY_TOTAL_LENGTH));
    }

    @Override
    protected void findViewsById(View view) {
        filePathView = view.findViewById(R.id.file_path_view);
        btnDownload = view.findViewById(R.id.btn_download);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    @Override
    protected void setupViews() {
        filePathView.setText(message.extra(Message.KEY_FILE_PATH));
        if (Message.SEND == message.getSourceType()) {
            btnDownload.setVisibility(View.GONE);
        } else {
            switch (message.extra(Message.KEY_FILE_STATUS)) {
                case Message.FILE_STATUS_DOWNLOADED:
                    btnDownload.setVisibility(View.GONE);
                    break;
                case Message.FILE_STATUS_DOWNLOADING:
                    btnDownload.setVisibility(View.VISIBLE);
                    btnDownload.setText("已下载： %");
                    btnDownload.setEnabled(false);
                    break;
                case Message.FILE_STATUS_NO_DOWNLOAD:
                    btnDownload.setVisibility(View.VISIBLE);
                    btnDownload.setText("下载文件：" + FileUtils.formatSize(totalLength));
                    btnDownload.setEnabled(true);
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    protected void registerEvent() {
        btnDownload.setOnClickListener(v -> {
            btnDownload.setEnabled(false);
            presenter.downloadFile(message);
        });
    }

    public static ShowFileFragment newInstance(long msgId, int msgPosition) {
        ShowFileFragment fragment = new ShowFileFragment();
        fragment.msgId = msgId;
        fragment.msgPosition = msgPosition;
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownload(EBFileTask msg) {
        if (msg.msgId != message.getId()) {
            return;
        }
        switch (msg.data) {
            case "onStart":
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
                break;
            case "onUpdate":
                progressBar.setProgress(msg.percent);
                btnDownload.setText("已下载：" + msg.percent + " %");
                break;
            case "onError":
                App.showToast("下载失败！", false);
                progressBar.setProgress(0);
                btnDownload.setText("下载文件：" + FileUtils.formatSize(totalLength));
                btnDownload.setEnabled(true);
                break;
            case "onFinish":
                App.showToast("文件下载成功！", false);
                progressBar.setVisibility(View.GONE);
                btnDownload.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

}
