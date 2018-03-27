package com.example.jinphy.simplechat.custom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.custom_libs.AudioRecorder;
import com.example.jinphy.simplechat.custom_view.dialog.my_dialog.MyDialog;
import com.example.jinphy.simplechat.utils.ScreenUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * DESC:
 * Created by jinphy on 2018/3/26.
 */

public class AudioRecordButton extends android.support.v7.widget.AppCompatTextView implements View.OnTouchListener {

    private MyDialog.Holder dialogHolder;

    private final AudioRecorder recorder = AudioRecorder.getInstance();
    private Disposable disposable;
    private OnRecordFinishListener onFinish;


    public AudioRecordButton(Context context) {
        super(context);
        init();
    }

    public AudioRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AudioRecordButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }



    @SuppressWarnings("all")
    private void init() {
        super.setOnTouchListener(this);
    }

    private float downY;
    private float upY;
    private long startTime;
    private long endTime;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initDialog();
                downY = event.getY();
                startTime = System.currentTimeMillis();
                startRecord();
                break;
            case MotionEvent.ACTION_MOVE:
                changeAlpha(event.getY());
                break;
            case MotionEvent.ACTION_UP:
                upY = event.getY();
                endTime = System.currentTimeMillis();
                // 向上滑动取消录音
                if (downY - upY > 50) {
                    cancelRecord();
                    return true;
                }
                if (endTime - startTime < 1000) {
                    // 时间小于 1 秒，取消录音
                    App.showToast("录音时间过短！", false);
                    cancelRecord();
                    return true;
                }
                // 录音超过时长以提前结束
                if (disposable == null) {
                    return true;
                }
                // 结束录音
                stopRecord();
                break;
            default:
                break;
        }
        return true;
    }

    private void initDialog() {
        dialogHolder = MyDialog.create(getContext())
                .cancelable(false)
                .hasFocus(false)
                .width(250)
                .height(250)
                .y(-100)
                .view(R.layout.layout_record_audio_dialog)
                .display();
    }

    private void changeAlpha(float currentY) {
        float alpha;
        if (Math.abs(currentY-downY)<50) {
            alpha = 0;
        } else {
            int deltaY = ScreenUtils.px2dp(getContext(), (downY - currentY));
            if (deltaY > 150) {
                alpha = 1f;
            } else {
                alpha = deltaY / 150.0f;
            }
        }
        dialogHolder.view.findViewById(R.id.cancel_layout).setAlpha(alpha);
        dialogHolder.view.findViewById(R.id.record_audio_layout).setAlpha(1-alpha);
    }

    private long recordedTime;

    /**
     * DESC: 开始录音
     * Created by jinphy, on 2018/3/26, at 20:37
     */
    private void startRecord() {
        long period = 50;
        synchronized (recorder) {
            TextView timeView = dialogHolder.view.findViewById(R.id.time_view);
            // 从一开始数1200 次， 首次延时50 毫秒， 以后每个50 毫秒数一次。1200次刚好一分钟，录音时长限制为一分钟
            Observable.intervalRange(1, 1200, period, period, TimeUnit.MILLISECONDS)
                    .doOnSubscribe(disposable -> {
                        this.disposable = disposable;
                        recordedTime = 0;
                        recorder.start();
                    })
                    .subscribeOn(Schedulers.computation())
                    .map(now -> {
                        recordedTime += period;
                        return formatTime();
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(timeView::setText)
                    .doOnComplete(()->{
                        stopRecord();
                        App.showToast("录音已超过一分钟！", false);
                    })
                    .subscribe();
        }
    }

    /**
     * DESC: 完成录音
     * Created by jinphy, on 2018/3/26, at 20:54
     */
    private void stopRecord() {
        if (disposable == null) {
            return;
        }
        synchronized (recorder) {
            recorder.stop();
            dialogHolder.dialog.dismiss();
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
            disposable = null;
            if (onFinish != null) {
                onFinish.call(recorder.getFilePath(), formatTime());
            }
        }
    }

    /**
     * DESC: 取消录音
     * Created by jinphy, on 2018/3/26, at 20:54
     */
    private void cancelRecord() {
        synchronized (recorder) {
            recorder.cancel();
            dialogHolder.dialog.dismiss();
            if (disposable != null) {
                disposable.dispose();
                disposable = null;
            }
        }
    }

    /**
     * DESC: 格式化时间
     * Created by jinphy, on 2018/3/26, at 21:58
     */
    private String formatTime() {
        int minute = (int) (recordedTime / 1000 / 60);
        int second = (int) (recordedTime / 1000 % 60);
        int millisecond = (int) (recordedTime % 1000);
        return String.format("%01d%s%02d%s%03d", minute,"' ",  second,"'' ", millisecond);
    }


    @Override
    public void setOnTouchListener(OnTouchListener l) {
        // no op
    }

    public void onRecordFinished(OnRecordFinishListener onFinish) {
        this.onFinish = onFinish;
    }

    public interface OnRecordFinishListener{

        /**
         * DESC: 录音成功时回调
         *
         *
         * @param filePath 录音的文件路径
         * @param duration 录音时长
         * Created by jinphy, on 2018/3/26, at 22:10
         */
        void call(String filePath, String duration);
    }
}
