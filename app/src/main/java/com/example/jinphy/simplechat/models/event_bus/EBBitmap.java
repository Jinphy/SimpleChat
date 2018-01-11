package com.example.jinphy.simplechat.models.event_bus;

import android.graphics.Bitmap;

/**
 * DESC:
 * Created by jinphy on 2018/1/10.
 */

public class EBBitmap extends EBBase<Bitmap> {

    public EBBitmap(boolean ok, Bitmap data) {
        super(ok, data);
    }
}
