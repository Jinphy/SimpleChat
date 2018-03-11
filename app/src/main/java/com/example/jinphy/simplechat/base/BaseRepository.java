package com.example.jinphy.simplechat.base;

import com.example.jinphy.simplechat.models.api.common.ApiInterface;
import com.example.jinphy.simplechat.models.api.common.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * DESC:
 *  泛型解释：
 *      1、T，使用该类时调用者需要的数据类型，一般情况下是Presenter类需要的类型，因为该类一般由Presenter使用
 *
 *      2、U,是网络请求中返回的具体数据类型，即Response<U>，所以是Response类中data字段的类型
 *
 *      @see Response#data
 * Created by jinphy on 2018/1/6.
 */
abstract public  class BaseRepository {

    /**
     * DESC: 数据仓库返回数据错误的类型是网络请求返回码不正确
     * Created by jinphy, on 2018/1/6, at 17:56
     */
    public static final String TYPE_CODE = "TYPE_CODE";

    /**
     * DESC: 数据仓库返回数据错误的类型是获取数据时发生异常导致
     * Created by jinphy, on 2018/1/6, at 17:57
     */
    public static final String TYPE_ERROR = "TYPE_ERROR";


    /**
     * DESC: 新建一个任务
     * Created by jinphy, on 2018/1/6, at 12:43
     */
    public<T> Task<T> newTask(Map<String, Object> params) {
        return new Task<>(params);
    }

    public<T> Task<T> newTask() {
        return new Task<>(new HashMap<>());
    }

    protected<T> void handleBuilder(ApiInterface<Response<T>> api, Task<T> task) {
        api.showProgress(task.isShowProgress())
                .useCache(task.isUseCache())
                .autoShowNo(task.isAutoShowNo())
                .showProgress(task.isShowProgress())
                .params(task.getParams())
                .onResponseYes(task.getOnDataOk()==null?null: response -> task.getOnDataOk().call(response))
                .onResponseNo(task.getOnDataNo()==null?null: response -> task.getOnDataNo().call(TYPE_CODE))
                .onError(task.getOnDataNo()==null?null: e-> task.getOnDataNo().call(TYPE_ERROR))
                .onFinal(task.getOnFinal()==null?null: task.getOnFinal()::call);
    }


    /**
     * DESC: 获取数据的任务
     * Created by jinphy, on 2018/1/6, at 12:43
     */
    public static class Task<T>{

        private OnDataOk<Response<T>> onDataOk;

        private OnDataNo onDataNo;

        private OnStart onStart;

        private OnFinal onFinal;

        private Map<String,Object> params;

        private boolean showProgress=true;

        private boolean useCache;

        private boolean autoShowNo = true;

        public Task(Map<String, Object> params) {
            this.params = params;
        }

        public Task<T> showProgress(boolean... show) {
            if (show.length == 0 || show[0]) {
                this.showProgress = true;
            } else {
                this.showProgress = false;
            }
            return this;
        }

        public Task<T> useCache(){
            this.useCache = true;
            return this;
        }

        public Task<T> param(String key, Object value) {
            this.params.put(key, value);
            return this;
        }

        public Task<T> doOnStart(OnStart onStart) {
            this.onStart = onStart;
            return this;
        }

        public Task<T> doOnDataOk(OnDataOk<Response<T>> onDataOk) {
            this.onDataOk = onDataOk;
            return this;
        }

        public Task<T> doOnDataNo(OnDataNo onDataNo) {
            this.onDataNo = onDataNo;
            return this;
        }

        public Task<T> doOnFinal(OnFinal onFinal) {
            this.onFinal = onFinal;
            return this;
        }


        public Task<T> autoShowNo(boolean autoShowNo) {
            this.autoShowNo = autoShowNo;
            return this;
        }


        public OnDataOk<Response<T>> getOnDataOk() {
            return onDataOk;
        }

        public OnDataNo getOnDataNo() {
            return onDataNo;
        }

        public OnStart getOnStart() {
            return onStart;
        }

        public OnFinal getOnFinal() {
            return onFinal;
        }

        public Map<String, Object> getParams() {
            return params;
        }


        public boolean isShowProgress() {
            return showProgress;
        }

        public boolean isUseCache() {
            return useCache;
        }

        public boolean isAutoShowNo() {
            return autoShowNo;
        }

        /**
         * DESC: 提交该对象，可以通过该回调链式最终的设置结果
         *
         *
         * Created by jinphy, on 2018/1/6, at 15:44
         */
        public void submit(TaskCallback<T> commit) {
            if (commit != null) {
                commit.accept(this);
            }
        }
    }



    /**
     * DESC: 获取数据成功时回调
     * Created by jinphy, on 2018/1/6, at 12:41
     */
    public interface OnDataOk<T>{
        void call(T okData);
    }

    /**
     * DESC: 获取数据失败是回调
     * Created by jinphy, on 2018/1/6, at 12:41
     */
    public interface OnDataNo{
        void call(String reason);
    }

    /**
     * DESC: 开始获取数据时回调
     * Created by jinphy, on 2018/1/6, at 12:41
     */
    public interface OnStart{
        void call();
    }

    /**
     * DESC: 获取结束时回调
     * Created by jinphy, on 2018/1/6, at 12:42
     */
    public interface OnFinal{
        void call();
    }

    public interface TaskCallback<T>{
        void accept(Task<T> task);
    }
}
