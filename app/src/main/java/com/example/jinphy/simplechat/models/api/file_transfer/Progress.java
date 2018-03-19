package com.example.jinphy.simplechat.models.api.file_transfer;

/**
 * DESC: 进度条
 * Created by jinphy on 2018/1/19.
 */

public class Progress {

    /**
     * DESC: 当前进度的百分比
     * Created by jinphy, on 2018/1/19, at 12:41
     */
    float percentage;

    /**
     * DESC: 总大小
     * Created by jinphy, on 2018/1/19, at 12:41
     */
    long total;

    /**
     * DESC: 当前进度
     * Created by jinphy, on 2018/1/19, at 12:41
     */
    long current;

    /**
     * DESC: 判断上传是否正常
     * Created by jinphy, on 2018/1/19, at 15:48
     */
    boolean ok = true;

    /**
     * DESC: 上传文件成功时，服务器返回当前文件对应的url
     * Created by jinphy, on 2018/1/19, at 15:15
     */
    String url;




    public static Progress error() {
        Progress progress = new Progress();
        progress.ok = false;
        return progress;
    }


    /**
     * DESC: 获取当前完成的百分比
     * Created by jinphy, on 2018/1/19, at 12:46
     */
    public float percentage() {
        return percentage;
    }

    /**
     * DESC: 获取任务总大小
     * Created by jinphy, on 2018/1/19, at 12:47
     */
    public long total() {
        return total;
    }

    /**
     * DESC: 获取当前完成的大小
     * Created by jinphy, on 2018/1/19, at 12:47
     */
    public long current() {
        return current;
    }

    public boolean ok() {
        return ok;
    }

    public String url(){
        return this.url;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
