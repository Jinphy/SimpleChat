package com.example.jinphy.simplechat.models.file_task;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.utils.DeviceUtils;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.io.File;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * DESC:
 * Created by jinphy on 2018/3/21.
 */

@Entity
public class FileTask implements Parcelable{

    @Id(assignable = true)
    protected long id;

    /**
     * DESC: 文件路径
     * Created by jinphy, on 2018/3/21, at 17:36
     */
    protected String filePath;

    /**
     * DESC: 生成的保存在服务器中的文件名
     * Created by jinphy, on 2018/3/21, at 17:34
     */
    protected String generatedFileName;

    /**
     * DESC: 是否上传或者下载完成
     * Created by jinphy, on 2018/3/21, at 17:33
     */
    protected boolean hasFinished;

    /**
     * DESC: 文件的总大小
     * Created by jinphy, on 2018/3/21, at 17:33
     */
    protected long totalFileLength;

    /**
     * DESC: 完成的大小
     * Created by jinphy, on 2018/3/21, at 17:33
     */
    protected long finishedLength;


    protected String url;

    //---------------------------------------------------

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getGeneratedFileName() {
        return generatedFileName;
    }

    public void setGeneratedFileName(String generatedFileName) {
        this.generatedFileName = generatedFileName;
    }

    public boolean isHasFinished() {
        return hasFinished;
    }

    public void setHasFinished(boolean hasFinished) {
        this.hasFinished = hasFinished;
    }

    public long getTotalFileLength() {
        return totalFileLength;
    }

    public void setTotalFileLength(long totalFileLength) {
        this.totalFileLength = totalFileLength;
    }

    public long getFinishedLength() {
        return finishedLength;
    }

    public void setFinishedLength(long finishedLength) {
        this.finishedLength = finishedLength;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void addFinishedLength(long length) {
        this.finishedLength += length;
    }

    //-----------------------------------------------
    public FileTask() {

    }


    public static FileTask create(String filePath) {
        if (ObjectHelper.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        FileTask task = new FileTask();
        task.setFilePath(filePath);
        task.setTotalFileLength(file.length());
        long now = System.currentTimeMillis();
        String fileName = DeviceUtils.devceIdMD5() + "_" + now;
        if (filePath.contains(".")) {
            fileName += filePath.substring(filePath.lastIndexOf("."));
        }
        task.setGeneratedFileName(fileName);

        String url = StringUtils.generateURI(Api.BASE_URL, Api.FILE_PORT, "/" + now, "");
        task.setUrl(url);
        return task;
    }

    public static FileTask parse(Message message,String baseFilePath) {
        FileTask task = new FileTask();
        task.totalFileLength = Long.valueOf(message.extra(Message.KEY_TOTAL_LENGTH));
        task.generatedFileName = message.extra(Message.KEY_FILE_NAME);
        task.filePath = baseFilePath + "/" + task.generatedFileName;
        task.url = StringUtils.generateURI(Api.BASE_URL, Api.FILE_PORT, "/" + System.currentTimeMillis(), "");
        return task;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(filePath);
        out.writeString(generatedFileName);
        out.writeInt(hasFinished ? 1 : 0);
        out.writeLong(totalFileLength);
        out.writeLong(finishedLength);
        out.writeString(url);
    }

    private FileTask(Parcel in) {
        id = in.readLong();
        filePath = in.readString();
        generatedFileName = in.readString();
        hasFinished = in.readInt() == 1;
        totalFileLength = in.readLong();
        finishedLength = in.readLong();
        url = in.readString();
    }

    public static final Parcelable.Creator<FileTask> CREATOR = new Parcelable.Creator<FileTask>() {

        @Override
        public FileTask createFromParcel(Parcel source) {
            return new FileTask(source);
        }

        @Override
        public FileTask[] newArray(int size) {
            return new FileTask[size];
        }
    };
}
