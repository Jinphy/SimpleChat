package com.example.jinphy.simplechat.custom_libs;

import android.media.MediaPlayer;

import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.io.IOException;

/**
 * DESC:
 * Created by jinphy on 2018/3/27.
 */

public class AudioPlayer {

    MediaPlayer player;

    private String filePath;



    private static class InstanceHolder{
        static final AudioPlayer DEFAULT = new AudioPlayer();
    }

    public static AudioPlayer getInstance() {
        return InstanceHolder.DEFAULT;
    }

    private AudioPlayer() {
        player = new MediaPlayer();
    }


    /**
     * DESC: 静态方法，播放语音或停止播放
     * Created by jinphy, on 2018/3/27, at 18:12
     */
    public static synchronized void playOrStop(String filePath) {
        if (ObjectHelper.isEmpty(filePath)) {
            return;
        }
        AudioPlayer audioPlayer = AudioPlayer.getInstance();
        String oldFilePath = audioPlayer.filePath;
        audioPlayer.stop();
        if (StringUtils.notEqual(filePath, oldFilePath)) {
            audioPlayer.start(filePath);
        }
    }

    public synchronized void release() {
        if (player == null) {
            return;
        }
        if (player.isPlaying()) {
            this.player.stop();
        }
        this.player.release();
        this.player = null;
    }

    /**
     * DESC: 停止播放
     * Created by jinphy, on 2018/3/27, at 18:01
     */
    private synchronized void stop() {
        try {
            filePath = null;
            player.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * DESC: 开始播放
     * Created by jinphy, on 2018/3/27, at 18:12
     */
    private synchronized void start(String filePath) {
        this.filePath = filePath;
        this.reset();
        player.start();
    }


    /**
     * DESC: 重置播放器
     * Created by jinphy, on 2018/3/27, at 18:12
     */
    private synchronized boolean reset() {
        if (player == null) {
            player = new MediaPlayer();
        }
        player.reset();
        player.setOnCompletionListener(mp -> filePath = null);
        try {
            player.setDataSource(filePath);
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
