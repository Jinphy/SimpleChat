package com.example.jinphy.simplechat.modules.group.group_detail;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.modules.modify_user_info.ModifyUserFragment;

public class ModifyGroupActivity extends BaseActivity {

    public static void start(Activity activity, String groupNo) {
        Intent intent = new Intent(activity, ModifyGroupActivity.class);
        intent.putExtra(ModifyGroupFragment.GROUP_NO, groupNo);
        activity.startActivity(intent);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_group);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(colorPrimary);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("群聊详情");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left_24dp);

        getPresenter(addFragment(
                ModifyGroupFragment.newInstance(
                        getIntent().getStringExtra(ModifyGroupFragment.GROUP_NO)),
                R.id.fragment));
    }

    @Override
    public ModifyGroupPresenter getPresenter(Fragment fragment) {
        return new ModifyGroupPresenter((ModifyGroupContract.View) fragment);
    }
}
