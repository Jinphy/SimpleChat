package com.example.jinphy.simplechat.modules.show_photo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.custom_libs.qr_code.QRCode;
import com.example.jinphy.simplechat.custom_view.menu.MyMenu;
import com.example.jinphy.simplechat.models.event_bus.EBMessage;
import com.example.jinphy.simplechat.models.event_bus.EBQRCodeContent;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.utils.AnimUtils;
import com.example.jinphy.simplechat.utils.FileUtils;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.ScreenUtils;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * DESC:
 * Created by jinphy, on 2018/3/22, at 19:35
 */
public class ShowPhotoFragment extends BaseFragment<ShowPhotoPresenter> implements ShowPhotoContract.View {

    public static final String SAVE_KEY_MSG_ID = "SAVE_KEY_MSG_ID";
    public static final String SAVE_KEY_MSG_POSITION = "SAVE_KEY_MSG_POSITION";

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
    private long totalLength;

    private QRCode.Content qrCodeContent = new QRCode.Content();

    public ShowPhotoFragment() {
        // Required empty public constructor
    }

    public static ShowPhotoFragment newInstance(long msgId, int position) {
        ShowPhotoFragment fragment = new ShowPhotoFragment();
        fragment.msgId = msgId;
        fragment.msgPosition = position;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            msgId = savedInstanceState.getLong(SAVE_KEY_MSG_ID);
            msgPosition = savedInstanceState.getInt(SAVE_KEY_MSG_POSITION);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(SAVE_KEY_MSG_ID, msgId);
        outState.putInt(SAVE_KEY_MSG_POSITION, msgPosition);
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
    }

    @Override
    protected void findViewsById(View view) {
        imageView = view.findViewById(R.id.image_view);
        btnDownload = view.findViewById(R.id.btn_download);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    @Override
    protected void setupViews() {
        Bitmap bitmap = ImageUtil.getBitmap(message.extra(Message.KEY_FILE_PATH), 1000, 1000);
        if (bitmap != null) {
            imageView.setImage(ImageSource.bitmap(bitmap));
            btnDownload.setVisibility(View.GONE);
            parseQRCode(bitmap);
        } else {
            btnDownload.setVisibility(View.VISIBLE);
            bitmap = StringUtils.base64ToBitmap(message.extra(Message.KEY_THUMBNAIL));
            imageView.setImage(ImageSource.bitmap(bitmap));
        }
        progressBar.setVisibility(View.GONE);
        progressBar.setProgress(0);

        imageView.setMaxScale(5.0f);
        btnDownload.setText("下载原图（" + FileUtils.formatSize(totalLength) + "）");
    }

    @Override
    protected void registerEvent() {
        btnDownload.setOnClickListener(v -> {
            presenter.downloadPhoto(message);
            btnDownload.setEnabled(false);
        });
        imageView.setOnLongClickListener(v -> {
            showMenu();
            return true;
        });
    }

    /**
     * DESC: 显示菜单
     * Created by jinphy, on 2018/3/31, at 21:00
     */
    private void showMenu() {
        MyMenu.create(activity())
                .width(ScreenUtils.px2dp(activity(), ScreenUtils.getScreenWidth(activity())))
                .gravity(Gravity.BOTTOM)
                .item("删除图片", (menu, item) -> {
                    FileUtils.deleteFile(message.extra(Message.KEY_FILE_PATH));
                    EventBus.getDefault().post(EBMessage.updateMsg(message, msgPosition));
                    App.showToast("图片已删除！", false);
                    finishActivity();
                })
                .item("识别图中二维码", (menu, item) -> {
                    EventBus.getDefault().postSticky(new EBQRCodeContent(qrCodeContent));
                })
                .onDisplay(menu -> {
                    menu.item(1).setVisibility(qrCodeContent.isMyApp() ? View.VISIBLE : View.GONE);
                })
                .display();

    }

    /**
     * DESC: 解析二维码
     * Created by jinphy, on 2018/3/31, at 17:02
     */
    private void parseQRCode(Bitmap bitmap) {
        Observable.just(bitmap)
                .subscribeOn(Schedulers.computation())
                .map(QRCode::parse)
                .doOnNext(content -> qrCodeContent = content)
                .subscribe();
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
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(percent);
        btnDownload.setText(String.format("已下载：%d %s", percent,"%"));
    }

    @Override
    public void whenDownloadFinish() {
        btnDownload.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        Bitmap bitmap = ImageUtil.getBitmap(message.extra(Message.KEY_FILE_PATH), 1500, 1500);
        imageView.setImage(ImageSource.bitmap(bitmap));
        parseQRCode(bitmap);

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
