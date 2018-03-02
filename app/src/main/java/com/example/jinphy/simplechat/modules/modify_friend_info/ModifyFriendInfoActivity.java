package com.example.jinphy.simplechat.modules.modify_friend_info;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.Friend_;
import com.example.jinphy.simplechat.modules.modify_user_info.ModifyUserActivity;
import com.example.jinphy.simplechat.modules.modify_user_info.ModifyUserContract;
import com.example.jinphy.simplechat.modules.modify_user_info.ModifyUserFragment;
import com.example.jinphy.simplechat.modules.modify_user_info.ModifyUserPresenter;

public class ModifyFriendInfoActivity extends BaseActivity {

    /**
     * DESC: 启动修改用户信息Activity
     * Created by jinphy, on 2018/1/8, at 11:09
     */
    public static void start(Activity activity,String account) {
        Intent intent = new Intent(activity, ModifyFriendInfoActivity.class);
        intent.putExtra(Friend_.account.name, account);
        activity.startActivity(intent);
//        activity.overridePendingTransition(R.anim.alpha_in,R.anim.anim_no);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_friend_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(colorPrimary);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("好友信息");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left_24dp);

        getPresenter(addFragment(
                ModifyFriendInfoFragment.newInstance(
                        getIntent().getStringExtra(Friend_.account.name)), R.id.fragment));
    }

    @Override
    public ModifyFriendInfoPresenter getPresenter(Fragment fragment) {
        return new ModifyFriendInfoPresenter((ModifyFriendInfoContract.View) fragment);
    }


}
