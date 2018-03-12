package com.example.jinphy.simplechat.modules.system_msg.new_member;

/**
 * DESC:
 * Created by jinphy on 2018/3/12.
 */

public class NewMemberPresenter implements NewMemberContract.Presenter {

    private NewMemberContract.View view;


    public NewMemberPresenter(NewMemberContract.View view) {
        this.view = view;
    }



    @Override
    public void start() {

    }
}
