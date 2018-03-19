package com.example.jinphy.simplechat.custom_view.dialog.my_dialog;

import android.view.View;

/**
 * DESC:
 * Created by jinphy on 2018/3/14.
 */

public interface MyDialogInterface {



    MyDialogInterface width(int valueDp);

    MyDialogInterface height(int valueDp);

    MyDialogInterface view(View view);

    MyDialogInterface view(int resourceId);

    MyDialogInterface cancelable(boolean cancelable);

    MyDialog.Holder display();

    void dismiss();



}
