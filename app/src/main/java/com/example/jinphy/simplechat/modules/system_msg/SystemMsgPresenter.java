package com.example.jinphy.simplechat.modules.system_msg;

/**
 * DESC:
 * Created by jinphy on 2018/3/1.
 */

public class SystemMsgPresenter implements SystemMsgContract.Presenter {

    private final SystemMsgContract.View view;

    public SystemMsgPresenter(SystemMsgContract.View view) {
        this.view = view;
    }

    @Override
    public void start() {

    }
}
