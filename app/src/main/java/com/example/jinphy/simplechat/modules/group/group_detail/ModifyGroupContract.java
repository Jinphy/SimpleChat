package com.example.jinphy.simplechat.modules.group.group_detail;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.group.Group;

/**
 * DESC:
 * Created by jinphy on 2018/3/12.
 */

public class ModifyGroupContract {


    interface View extends BaseView<Presenter> {

        void pickPhoto();

        void whenModifyAvatarOk();

        void whenModifyNameOk();

        void whenModifyMaxCountOk(int maxCount);

        void whenModifyAutoAddOk(boolean autoAdd);

        void whenModifyShowMemberNameOk(boolean showMemberName);

        void whenModifyKeepSilentOk(boolean keepSilent);

        void whenModifyRejectMsgOk(boolean rejectMsg);

        void setAutoAdd();

        void setShowMemberName();

        void setKeepSilent();

        void setRejectMsg();

    }



    interface Presenter extends BasePresenter{

        Group getGroup(String groupNo);

        void updateGroup(Group group);

        void modifyAvatar(Context context, Group group, Bitmap bitmap);

        void modifyName(Context context, Group group, String name);

        void modifyMaxCount(Context context, Group group, int maxCount);

        void modifyAutoAdd(Context context, Group group, boolean autoAdd);

        void modifyShowMemberName(Context context, Group group, boolean showMemberName);

        void modifyKeepSilent(Context context, Group group, boolean keepSilent);

        void modifyRejectMsg(Context context, Group group, boolean rejectMsg);

        void joinGroup(Context context, Group group);
    }
}
