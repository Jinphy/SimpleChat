package com.example.jinphy.simplechat.custom_view.dialog;

import android.view.View;

/**
 * DESC:
 * Created by jinphy on 2018/3/14.
 */

public interface Dialog {



    Dialog width(int valueDp);

    Dialog height(int valueDp);

    Dialog view(View view);

    Dialog view(int resourceId);

    MyDialog.Holder display();

    void dismiss();

}
