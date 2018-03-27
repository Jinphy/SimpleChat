package com.example.jinphy.simplechat.custom_libs;

import android.media.MediaRecorder;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.ThreadPoolUtils;

import java.io.File;
import java.io.IOException;

/**
 * DESC: 录制音频
 * <p>
 * Created by jinphy on 2018/3/26.
 */

public class AudioRecorder {
    private MediaRecorder recorder;

    private String filePath;

    private static class InstanceHolder {
        static final AudioRecorder DEFAULT = new AudioRecorder();
    }

    private AudioRecorder() {
        recorder = new MediaRecorder();
    }

    public static AudioRecorder getInstance() {
        return InstanceHolder.DEFAULT;
    }

    /**
     * DESC: 重置录音器
     * Created by jinphy, on 2018/3/26, at 15:04
     */
    private void reset() {
        if (recorder == null) {
            recorder = new MediaRecorder();
        }
        recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(initFilePath());

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String initFilePath() {
        filePath = ImageUtil.AUDIO_PATH + File.separator + System.currentTimeMillis();
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }


    //-------------------------------------------------------------------------

    /**
     * DESC: 开始录制
     * Created by jinphy, on 2018/3/26, at 14:57
     */
    public synchronized void start() {
        reset();
        recorder.start();
    }

    /**
     * DESC: 停止录制
     * Created by jinphy, on 2018/3/26, at 14:58
     */
    public synchronized void stop() {
        try {
            recorder.stop();
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    /**
     * DESC: 取消录制
     * Created by jinphy, on 2018/3/26, at 14:58
     */
    public synchronized void cancel() {
        File file = new File(filePath);
        file.delete();
        this.stop();
    }


    /**
     * DESC: 释放录音器
     * Created by jinphy, on 2018/3/26, at 15:28
     */
    public synchronized void release() {
        if (recorder == null) {
            return;
        }
        recorder.reset();
        recorder.release();
        recorder = null;
    }

    public synchronized String getFilePath() {
        return filePath;
    }

}
