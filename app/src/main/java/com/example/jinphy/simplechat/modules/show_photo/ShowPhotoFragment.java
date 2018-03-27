package com.example.jinphy.simplechat.modules.show_photo;

import android.graphics.Bitmap;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.models.event_bus.EBMessage;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.ScreenUtils;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * DESC:
 * Created by jinphy, on 2018/3/22, at 19:35
 */
public class ShowPhotoFragment extends BaseFragment<ShowPhotoPresenter> implements ShowPhotoContract.View {

    public static final String TAG_MSG_ID = "MSG_ID";
    public static final String TAG_MSG_POSITION = "POSITION";

    private long msgId;

    private SubsamplingScaleImageView imageView;
    private TextView btnDownload;
    private NumberProgressBar progressBar;
    private Message message;
    private int msgPosition;

    /**
     * DESC: 图片总大小
     * Created by jinphy, on 2018/3/23, at 10:16
     */
    private double totalLength;


    public ShowPhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.unregisterDownloadListener();
    }


    @Override
    protected int getResourceId() {
        return R.layout.fragment_show_photo;
    }

    @Override
    protected void initData() {
        presenter.registerDownloadListener();
        message = presenter.getMessage(msgId);
        screenWidth = ScreenUtils.getScreenWidth(activity());
        totalLength = Long.valueOf(message.extra(Message.KEY_TOTAL_LENGTH));
        totalLength = totalLength / 1024.0 / 1024.0;// B->MB
    }

    @Override
    protected void findViewsById(View view) {
        imageView = view.findViewById(R.id.image_view);
        btnDownload = view.findViewById(R.id.btn_download);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    @Override
    protected void setupViews() {
        Bitmap bitmap = ImageUtil.getBitmap(message.extra(Message.KEY_FILE_PATH), 1500, 1500);
        LogUtils.e(message.extra(Message.KEY_FILE_PATH));
        if (bitmap != null) {
            imageView.setImage(ImageSource.bitmap(bitmap));
            btnDownload.setVisibility(View.GONE);
        } else {
            btnDownload.setVisibility(View.VISIBLE);
            bitmap = StringUtils.base64ToBitmap(message.extra(Message.KEY_THUMBNAIL));
            imageView.setImage(ImageSource.bitmap(bitmap));
        }

        imageView.setMaxScale(5.0f);
        btnDownload.setText(String.format("下载原图（%.2f MB）", totalLength));
    }

    @Override
    protected void registerEvent() {
        btnDownload.setOnClickListener(v -> {
            presenter.downloadPhoto(message);
            btnDownload.setEnabled(false);
        });
        imageView.setOnClickListener(v->{
            float scale = imageView.getScale();
            if (scale - 1f > 0.01) {
                imageView.animateScale(1f);
            } else {
                finishActivity();
            }
        });
    }

    public static ShowPhotoFragment newInstance(long msgId, int position) {
        ShowPhotoFragment fragment = new ShowPhotoFragment();
        fragment.msgId = msgId;
        fragment.msgPosition = position;
        return fragment;
    }


    // 菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        finishActivity();
        // 返回false，则activity将会不回调onBackPressed
        return false;
    }

    @Override
    public void whenDownloadStart() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
    }

    @Override
    public void whenUpdateProgress(int percent) {
        progressBar.setProgress(percent);
        btnDownload.setText(String.format("已下载：%d %s", percent,"%"));
    }

    @Override
    public void whenDownloadFinish() {
        btnDownload.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        imageView.setImage(ImageSource.uri(message.extra(Message.KEY_FILE_PATH)));
        App.showToast("原图下载成功！", false);
        presenter.removeThumbnail(message);

        EventBus.getDefault().post(EBMessage.updateMsg(message, msgPosition));
    }

    @Override
    public void whenDownloadError() {
        btnDownload.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        App.showToast("下载失败！", false);
    }

    //-----------------------------------------------------
    float downX;
    float upX;
    long downTime;
    long upTime;
    int screenWidth;

    @Override
    public boolean handleHorizontalTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                upX = event.getX();
                upTime = System.currentTimeMillis();
                float speed = (upX - downX) / (upTime - downTime);
                if (speed > 1.5f && downX < screenWidth / 3) {
                    finishActivity();
                }
                return true;
        }
        return false;
    }
}
