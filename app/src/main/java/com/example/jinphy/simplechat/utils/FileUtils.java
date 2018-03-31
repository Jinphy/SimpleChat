package com.example.jinphy.simplechat.utils;

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
}
