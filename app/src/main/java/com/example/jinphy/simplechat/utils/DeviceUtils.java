package com.example.jinphy.simplechat.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.TelephonyManager;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseApplication;

/**
 * Created by jinphy on 2017/11/19.
 */

public class DeviceUtils {

    /**
     * DESC: 获取设备的IMEI，需要运行时权限
     *
     *  权限：Manifest.permission.READ_PHONE_STATE
     *
     * Created by jinphy, on 2017/12/31, at 13:28
     */
    @SuppressLint("MissingPermission")
    public static String deviceId(){
        String IMEI = null;
        Activity activity = App.activity();
        if (activity != null) {
            TelephonyManager telephonyManager=(TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            IMEI=telephonyManager.getDeviceId();
        }
        return IMEI;
    }


    /**
     * DESC: 获取设备的IMEI并使用md5加密，需要运行时权限
     *
     *  权限：Manifest.permission.READ_PHONE_STATE
     *
     * Created by jinphy, on 2017/12/31, at 13:28
     */
    @SuppressLint("MissingPermission")
    public static String deviceIdMD5(){
        return EncryptUtils.md5(deviceId());
    }

    /**
     * DESC: 获取手机号码，需要运行时权限
     *
     *  权限：Manifest.permission.READ_PHONE_NUMBERS
     * Created by jinphy, on 2017/12/31, at 13:32
     */
    @SuppressLint("MissingPermission")
    public static String phone(){
        String phone = null;

        Activity activity = BaseApplication.activity();
        if (activity != null) {
            TelephonyManager telephonyManager=(TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            phone=telephonyManager.getLine1Number();
        }
        return phone;
    }


    /**
     * DESC: 震动一次
     *
     * @param milliseconds 单次震动时长
     * @param amplitude 振幅，即每次震动的强度 取值：1~255
     * Created by jinphy, on 2018/4/1, at 9:55
     */
    public static void vibrate(long milliseconds, int amplitude) {
        Vibrator vibrator = getVibrator();
        if (!vibrator.hasVibrator() || milliseconds <= 0) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, amplitude));
        } else {
            vibrator.vibrate(milliseconds);
        }
    }

    /**
     * DESC: 震动多次
     *
     *
     * @param timings 震动时长，与amplitude 一一对应，所有取值为0的元素在amplitude中对应的振幅将会被忽略
     * @param amplitude 震动，即震动强度，与timings一一对应，取值范围0~255，可以使用0来控制震动与不震动
     * @param repeat 重复次数下标，0则重复一次，一次类推，-1则不重复
     * Created by jinphy, on 2018/4/1, at 10:05
     */
    public static void vibrate(long[] timings, int[] amplitude, int repeat) {
        Vibrator vibrator = getVibrator();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitude, repeat));
        } else {
            vibrator.vibrate(timings, repeat);

        }
    }

    /**
     * DESC: 震动多次，默认震动强度
     *
     *
     * @param timings 震动时长，所有取值为0的元素在amplitude中对应的振幅将会被忽略
     *                第一个元素对应的时长表示不震动，第二个元素对应的时长表示震动，以此类推
     * @param repeat 重复次数下标，0则重复一次，一次类推，-1则不重复
     * Created by jinphy, on 2018/4/1, at 10:05
     */
    public static void vibrate(long[] timings, int repeat) {
        Vibrator vibrator = getVibrator();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(timings, repeat));
        } else {
            vibrator.vibrate(timings, repeat);
        }
    }


    private static MediaPlayer player;


    /**
     * DESC: 播放铃声
     * Created by jinphy, on 2018/4/1, at 14:16
     */
    public static void playRing(int resId) {
        playRing(resId, null);
    }


    /**
     * DESC: 播放铃声
     * Created by jinphy, on 2018/4/1, at 11:02
     */
    public static void playRing(int resId, Runnable onComplete) {
        if (player != null) {
            try {
                player.stop();
                player.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        player = MediaPlayer.create(App.app(), resId);
        if (player == null) {
            return;
        }
        player.setOnCompletionListener(mp -> {
            mp.release();
            player = null;
            if (onComplete != null) {
                onComplete.run();
            }
        });
        player.setOnErrorListener((mp, what, extra) -> {
            if (mp != null) {
                mp.release();
            }
            player = null;
            return false;
        });
        player.start();
    }

    /**
     * DESC: 停止播放铃声
     * Created by jinphy, on 2018/4/1, at 14:31
     */
    public static void stopRing() {
        if (player != null) {
            try {
                player.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                player.release();
                player = null;
            }
        }
    }


    /**
     * DESC: 获取振动器
     * Created by jinphy, on 2018/4/1, at 9:47
     */
    public static Vibrator getVibrator() {
        return (Vibrator) App.app().getSystemService(Context.VIBRATOR_SERVICE);
    }

}
