package com.example.jinphy.simplechat.modules.system_msg;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.modules.system_msg.new_friend.NewFriendsActivity;
import com.example.jinphy.simplechat.modules.system_msg.notice.NoticeActivity;

/**
 *
 *
 */
public class SystemMsgFragment extends BaseFragment<SystemMsgPresenter> implements SystemMsgContract.View{

    private View itemNewFriend;
    private View itemNotice;


    public SystemMsgFragment() {
        // Required empty public constructor
    }

    public static SystemMsgFragment newInstance() {
        SystemMsgFragment fragment = new SystemMsgFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (presenter == null) {
            this.presenter = getPresenter();
        }

    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_system_msg;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void findViewsById(View view) {
        itemNewFriend = view.findViewById(R.id.item_new_friend);
        itemNotice = view.findViewById(R.id.item_notice);
    }

    @Override
    protected void setupViews() {

    }

    @Override
    protected void registerEvent() {
        itemNewFriend.setOnClickListener(v->{
            NewFriendsActivity.start(activity());
        });
        
        itemNotice.setOnClickListener(v -> {
            NoticeActivity.start(activity());
        });
    }



    /**
     * DESC: 创建菜单
     * Created by jinphy, on 2018/1/8, at 20:52
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_chat_fragment,menu);
    }

    // 菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onBackPressed() {
        finishActivity();
        return false;
    }
}
