package com.example.jinphy.simplechat.custom_view.dialog.file_selector;

import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.utils.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/28.
 */

public class MyFile implements Comparable<MyFile>{

    private String path;

    private String name;

    private String size;

    private boolean isDir;

    private boolean isSelect;

    private MyFile() {

    }

    public static List<MyFile> create(File[] files) {
        List<MyFile> myFiles = new LinkedList<>();
        if (files == null) {
            return myFiles;
        }
        for (File file : files) {
            myFiles.add(MyFile.create(file));
        }
        return myFiles;
    }

    /**
     * DESC: 排序
     * Created by jinphy, on 2018/3/28, at 14:19
     */
    public static List<MyFile> sort(List<MyFile> files) {
        if (files == null || files.size() == 0) {
            return files;
        }
        MyFile[] myFiles = files.toArray(new MyFile[files.size()]);
        Arrays.sort(myFiles);
        files.clear();
        files.addAll(Arrays.asList(myFiles));
        return files;
    }


    public static MyFile create(File file) {
        if (file == null) {
            return null;
        }
        MyFile myFile = new MyFile();
        myFile.setPath(file.getAbsolutePath());
        myFile.setName(file.getName());
        myFile.setSize(FileUtils.formatSize(file.length()));
        myFile.setIsDir(file.isDirectory());
        return myFile;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setIsDir(boolean isDir) {
        this.isDir = isDir;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }


    @Override
    public int compareTo(@NonNull MyFile other) {
        if (this.isDir && !other.isDir) {
            return -1;
        }
        if (!this.isDir && other.isDir) {
            return 1;
        }
        return this.name.compareTo(other.name);
    }
}
