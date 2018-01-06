package com.example.jinphy.simplechat.api;

/**
 * DESC: 一个泛型网络请求回调接口。
 *  泛型T：
 *      1、当网络请求是单挑接口时，则T 为Response<U>
 *
 *      2、当网络请求是多条接口合并时，则T 为Response[] 数组，
 * Created by Jinphy on 2017/12/7.
 */

public interface ApiCallback<T> {

    /**
     * DESC: 网络请求开始前回调，可以用来显示刷新控件等
     * Created by Jinphy, on 2017/12/7, at 13:28
     */
    void doOnStart();

    /**
     * DESC: 网络请求成功时回调
     * Created by Jinphy, on 2017/12/7, at 13:25
     */
    void doOnResponse(T response);

    /**
     * DESC: 网络请求成功时判断结果数据是否正确时的回调
     * Created by Jinphy, on 2017/12/7, at 19:19
     */
    void doCheckResponse(T response);

    /**
     * DESC: 网络请求取消时回调
     * Created by Jinphy, on 2017/12/7, at 13:28
     */
    void doOnCancel();

    /**
     * DESC: 网络请求异常时回调
     * Created by Jinphy, on 2017/12/7, at 13:28
     */
    void doOnError(Throwable e);



    /**
     * DESC: 网络请求结束时回调，无论成功或是失败该方法都会在最后执行
     * Created by Jinphy, on 2017/12/26, at 9:30
     */
    void doOnFinal();


    //=================各个回调接口接口=============================================

    /**
     * DESC: 网络请求成功时的回调接口
     * Created by Jinphy, on 2017/12/4, at 9:17
     */
    interface OnResponse<T>{
        void call(T response);
    }
//
//    /**
//     * DESC: 可观察的网络请求的回调接口
//     * Created by Jinphy, on 2017/12/4, at 9:19
//     */
//    interface OnObservableNext<U>{
//        void call(Observable<U> observable);
//    }

    /**
     * DESC: 请求开始是回调
     *
     * Created by Jinphy, on 2017/12/4, at 9:21
     */
    interface OnStart{
        void call();
    }

    /**
     * DESC: 在取消是执行的回调
     * Created by Jinphy, on 2017/12/4, at 9:21
     */
    interface OnCancel{
        void call();
    }

    /**
     * DESC: 网络请求异常回调
     * Created by Jinphy, on 2017/12/4, at 9:22
     */
    interface OnError{
        void call(Throwable throwable);
    }

    /**
     * DESC: 网络请求成功并且数据正确时的回调接口
     * Created by Jinphy, on 2017/12/4, at 9:17
     */
    interface OnResponseYes<T>{
        void call(T response);
    }

    /**
     * DESC: 网络请求成功并且数据错误时的回调接口
     * Created by Jinphy, on 2017/12/4, at 9:17
     */
    interface OnResponseNo<T>{
        void call(T response);
    }

    /**
     * DESC: 网络请求结束时回调
     *
     *  注：该回调无论网络成功是否成功都会回调，所以可以用来关闭刷新控件等操作
     * Created by Jinphy, on 2017/12/26, at 9:28
     */
    interface OnFinal{
        void call();
    }

    /**
     * DESC: 该回调是用来进行特殊需求设置的，有了该回调可以通过方法传入的Api设置对象进行额外统一设置而不会使得
     *      链式调用无法进行
     *
     *      T 的泛型一般是ApiInterface 类的对象
     * Created by jinphy, on 2018/1/6, at 14:01
     */
    interface Setup<T>{
        void call(T api);
    }
}
