package com.example.jinphy.simplechat.models.event_bus;

/**
 * DESC:
 * Created by jinphy on 2018/1/18.
 */

public class EBUpdateView extends EBBase<String> {
    public static final int ALL = 0;
    public static final int MAIN_MSG = 1;// 主界面中的消息记录页
    public static final int MAIN_FRIEND = 2;// 主界面中的好友页

    // 更新那个界面
    public int which = ALL;



    public EBUpdateView() {
        super(true, null);
    }

    public EBUpdateView(String data) {
        super(true, data);
    }

    public EBUpdateView(int which) {
        super(true, null);
        switch (which) {
            case MAIN_MSG:
            case MAIN_FRIEND:
                this.which = which;
                break;
        }
    }
}
