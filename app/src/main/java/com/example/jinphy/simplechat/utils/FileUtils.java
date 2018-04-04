package com.example.jinphy.simplechat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.example.jinphy.simplechat.application.App;

import java.io.File;

/**
 * DESC:
 * Created by jinphy on 2018/3/28.
 */

public class FileUtils {

    public static final float KB = 1024;

    public static final float MB = KB * 1024;

    public static final float GB = MB * 1024;


    private FileUtils() {

    }

    /**
     * DESC: 格式化文件大小
     * Created by jinphy, on 2018/3/28, at 9:22
     */
    public static String formatSize(long size) {
        if (size < KB) {
            return String.format("%d %s", size, "B");
        }
        if (size < MB) {
            return String.format("%.2f %s", size / KB, "KB");
        }
        if (size < GB) {
            return String.format("%.2f %s", size / MB, "MB");
        }
        return String.format("%.2f %s", size / GB, "GB");
    }

    /**
     * DESC: 根据指定的路径生成一个独一无二的文件路径，避免覆盖之前的文件
     * Created by jinphy, on 2018/3/28, at 15:33
     */
    public static String createUniqueFileName(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return filePath;
        }
        int i = 1;
        String parent = file.getParent();
        String name = file.getName();
        while (true) {
            filePath = parent + "/" + name + "(" + (i++) + ")";
            file = new File(filePath);
            if (!file.exists()) {
                break;
            }
        }
        return filePath;
    }

    /**
     * DESC: 删除文件
     * Created by jinphy, on 2018/3/31, at 17:09
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean exist(String filePath) {
        return new File(filePath).exists();
    }


    /**
     * DESC: 第三方应用打开文件
     * Created by jinphy, on 2018/4/2, at 22:17
     */
    public static void shareFile(Context context, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType(getMimeType(file.getAbsolutePath()));//此处可发送多种文件
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(Intent.createChooser(intent, "分享文件"));
            } else {
                App.showToast("没有应用程序可以打开该文件!", false);
            }
        } else {
            App.showToast("文件不存在", false);
        }
    }


    public static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }

        }
        return mime;
    }
}
