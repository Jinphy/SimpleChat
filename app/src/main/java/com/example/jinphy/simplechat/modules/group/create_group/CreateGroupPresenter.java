package com.example.jinphy.simplechat.modules.group.create_group;

import android.content.Context;

import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.group.GroupRepository;
import com.example.jinphy.simplechat.models.user.UserRepository;

import java.util.Map;

/**
 *
 *
 * Created by Jinphy on 2018/3/6.
 */

public class CreateGroupPresenter implements CreateGroupContract.Presenter {


    private final CreateGroupContract.View view;
    private GroupRepository groupRepository;
    private UserRepository userRepository;
    public CreateGroupPresenter(CreateGroupContract.View view) {
        this.view = view;
        groupRepository = GroupRepository.getInstance();
        userRepository = UserRepository.getInstance();
    }

    @Override
    public void start() {
    }

    @Override
    public String getAccessToken() {
        return userRepository.currentUser().getAccessToken();
    }

    @Override
    public void createGroup(Context context, Map<String, Object> params) {
        groupRepository.<Map<String, String>>newTask(params)
                .doOnDataOk(okData -> {
                    Group group = Group.parse(okData.getData());
                    groupRepository.save(group);
                    view.whenCreateGroupOk(group);
                })
                .submit(task -> groupRepository.createGroup(context, task));
    }
}
