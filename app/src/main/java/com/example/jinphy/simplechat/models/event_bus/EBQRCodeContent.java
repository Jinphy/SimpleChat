package com.example.jinphy.simplechat.models.event_bus;

import com.example.jinphy.simplechat.custom_libs.qr_code.QRCode;

/**
 * DESC:
 * Created by jinphy on 2018/3/31.
 */

public class EBQRCodeContent extends EBBase<QRCode.Content> {

    public EBQRCodeContent(QRCode.Content data) {
        super(true, data);
    }
}
