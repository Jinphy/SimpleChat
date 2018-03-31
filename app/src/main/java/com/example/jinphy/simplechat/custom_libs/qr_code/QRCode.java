package com.example.jinphy.simplechat.custom_libs.qr_code;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;

import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;
import com.yzq.zxinglibrary.decode.BitmapLuminanceSource;

import java.util.HashMap;
import java.util.Map;

/**
 * DESC:
 * Created by jinphy on 2018/3/29.
 */

public final class QRCode {

    private static final int DEFAULT_WIDTH = 1500;

    private static final int DEFAULT_HEIGHT = 1500;

    private static final int DEFAULT_COLOR = Color.BLACK;

    // 缩放大小默认为二维码的大小的1/4
    private static final float DEFAULT_SCALE_RATIO = 0.25F;

    /**
     * DESC: 扫描二维码的请求码
     * Created by jinphy, on 2018/3/30, at 15:42
     */
    public static final int REQUEST_CODE_SCAN_QR_CODE = 1;


    private Builder builder;

    /**
     * DESC: 私有化构造函数
     * Created by jinphy, on 2018/3/31, at 12:39
     */
    private QRCode(Builder builder) {
        this.builder = builder;
    }

    /**
     * DESC: 生成二维码
     * Created by jinphy, on 2018/3/31, at 13:48
     */
    private Bitmap createQRCodeBitmap(String content) {
        return createQRCodeBitmap(content, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * DESC: 生成二维码
     * Created by jinphy, on 2018/3/31, at 13:48
     */
    private Bitmap createQRCodeBitmap(String content, int width, int height) {
        return createQRCodeBitmap(content, width, height, DEFAULT_COLOR);
    }

    /**
     * DESC: 生成二维码
     * Created by jinphy, on 2018/3/31, at 13:48
     */
    private Bitmap createQRCodeBitmap(String content, int width, int height, int color) {
        if (content == null || content.length() == 0) {
            return null;
        }
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (encode.get(x, y)) {
                        pixels[y * width + x] = color;
                    } else {
                        pixels[y * width + x] = Color.WHITE;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DESC: 为二维码添加logo
     * Created by jinphy, on 2018/3/29, at 16:23
     */
    private Bitmap addLogo(Bitmap qrCode, Bitmap logo) {
        return addLogo(qrCode, logo, DEFAULT_SCALE_RATIO);
    }

    /**
     * DESC:
     *
     * @param qrCode     二维码
     * @param logo       二维码上面的logo
     * @param scaleRatio logo图标相对于二维码的缩放比例,取值 0~1
     *                   Created by jinphy, on 2018/3/29, at 16:01
     */
    private Bitmap addLogo(Bitmap qrCode, Bitmap logo, float scaleRatio) {
        if (qrCode == null) {
            return null;
        }
        if (logo == null) {
            return null;
        }
        if (scaleRatio < 0 || scaleRatio > 1) {
            throw new IllegalArgumentException("scaleRatio 必须在0~1之间取值");
        }
        int qrCodeW = qrCode.getWidth();
        int qrCodeH = qrCode.getHeight();
        int logoW = logo.getWidth();
        int logoH = logo.getHeight();

        //新建空白bitmap
        Bitmap resultBitmap = Bitmap.createBitmap(qrCodeW, qrCodeH, Bitmap.Config.ARGB_8888);
        // 基于空白bitmap创建画布
        Canvas canvas = new Canvas(resultBitmap);

        // 绘制二维码的新的bitmap上
        canvas.drawBitmap(qrCode, 0, 0, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);

        // 缩放画布到指定的大小
        canvas.scale(scaleRatio, scaleRatio, qrCodeW >> 1, qrCodeH >> 1);


        // 指定logo的bitmap绘制区域
        Rect logoSrcRect = new Rect(0, 0, logoW, logoH);
        // 指定画布的canvas绘制区域
        RectF logoDestRect = new RectF(0,0,canvas.getWidth(),canvas.getHeight());

        // 绘制logo到画布上,destination
        canvas.drawBitmap(logo, logoSrcRect, logoDestRect, null);

        canvas.restore();
        return resultBitmap;
    }


    /**
     * DESC: 扫描二维码或者条形码
     * Created by jinphy, on 2018/3/30, at 15:55
     */
    public static void scan(BaseActivity activity, OnScanFinish onScanFinish) {
        if (activity == null) {
            return;
        }
        activity.addQRCodeCallback(onScanFinish);

        ZxingConfig config = new ZxingConfig();
        config.setShowbottomLayout(true);//底部布局（包括闪光灯和相册）
        config.setPlayBeep(true);//是否播放提示音
        config.setShake(true);//是否震动
        config.setShowAlbum(true);//是否显示相册
        config.setShowFlashLight(true);//是否显示闪光灯

        Intent intent = new Intent(activity, CaptureActivity.class);
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        activity.startActivityForResult(intent, QRCode.REQUEST_CODE_SCAN_QR_CODE);
    }

    /**
     * DESC: 解析二维码
     * Created by jinphy, on 2018/3/31, at 13:18
     */
    public static Content parse(Bitmap bitmap) {
        QRCodeReader reader = new QRCodeReader();
        Map<DecodeHintType, Object> hints = new HashMap<>(2);
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        hints.put(DecodeHintType.PURE_BARCODE, BarcodeFormat.QR_CODE);

        Result rawResult = null;
        try {
            rawResult = reader.decode(new BinaryBitmap(new HybridBinarizer(new BitmapLuminanceSource(bitmap))),hints);
        } catch (Exception var9) {
            var9.printStackTrace();
        }
        if (rawResult == null) {
            return new Content();
        }
        return Content.parse(rawResult.getText());
    }

    public static Content content() {
        return Content.create();
    }

    /**
     * DESC: 根据要生成二维码的字符串创建一个配置对象builder
     * Created by jinphy, on 2018/3/31, at 12:38
     */
    public static Builder from(String content) {
        if (content == null || content.length() == 0) {
            throw new IllegalArgumentException("text is null or empty");
        }
        return new Builder(content);
    }

    /**
     * DESC:
     * Created by jinphy, on 2018/3/31, at 12:38
     */
    public Bitmap make() {
        Bitmap qrCode = createQRCodeBitmap(builder.content, builder.width, builder.height, builder.color);
        if (builder.logo == null) {
            return qrCode;
        }

        // 裁剪logo形状
        Bitmap logo = ImageUtil.cropBitmap(builder.logo, builder.logoShape, builder.logoCornerRadius);

        // 添加边框
        logo = ImageUtil.addBorder(
                logo,
                builder.logoShape,
                builder.logoCornerRadius,
                builder.logoBorderSize,
                builder.logoBorderColor);

        return addLogo(qrCode, logo, builder.logoSizeRatio);
    }




    /**
     * DESC: 一个用来生成二维码的配置类builder
     * Created by jinphy, on 2018/3/31, at 12:40
     */
    public static class Builder{

        /**
         * DESC: 二维码的内容
         * Created by jinphy, on 2018/3/30, at 14:29
         */
        String content;

        /**
         * DESC: 二维码的宽度
         * Created by jinphy, on 2018/3/30, at 14:29
         */
        int width = 1500;

        /**
         * DESC: 二维码的高度
         * Created by jinphy, on 2018/3/30, at 14:30
         */
        int height = 1500;

        /**
         * DESC: 二维码的颜色
         * Created by jinphy, on 2018/3/30, at 14:30
         */
        int color = Color.BLACK;

        /**
         * DESC: 二维码中间的logo
         * Created by jinphy, on 2018/3/30, at 14:30
         */
        Bitmap logo;

        /**
         * DESC: 二维码中间logo的形状
         * Created by jinphy, on 2018/3/30, at 14:30
         */
        ImageUtil.Shape logoShape = ImageUtil.Shape.RECTANGLE;

        /**
         * DESC: 二维码中间logo的边框颜色
         * Created by jinphy, on 2018/3/30, at 14:31
         */
        int logoBorderColor = Color.WHITE;


        /**
         * DESC: 二维码中间logo的边框宽度
         * Created by jinphy, on 2018/3/30, at 14:31
         */
        int logoBorderSize = -1;

        /**
         * DESC: 二维码中间logo的圆角半径（logo为矩形时有效）
         * Created by jinphy, on 2018/3/30, at 14:32
         */
        int logoCornerRadius = -1;

        /**
         * DESC: 辅助变量，logo大小占二维码的比例
         * Created by jinphy, on 2018/3/30, at 14:33
         */
        private float logoSizeRatio = 0.15f;
        /**
         * DESC: 辅助变量，logo边框宽度占logo的比例
         * Created by jinphy, on 2018/3/30, at 14:33
         */
        private float logoBorderSizeRatio = 0.05f;
        /**
         * DESC: 辅助变量， logo的圆角半径占logo的比例（logo形状为矩形时有效）
         * Created by jinphy, on 2018/3/30, at 14:34
         */
        private float logoCornerRadiusRatio = 0.1f;

        private Builder(String content) {
            this.content = content;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder size(int size) {
            this.width = this.height = size;
            return this;
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder logo(Bitmap logo) {
            this.logo = logo;
            return this;
        }

        public Builder logo(Context context, int resId) {
            this.logo = BitmapFactory.decodeResource(context.getResources(), resId);
            return this;
        }

        public Builder logo(String filePath) {
            this.logo = BitmapFactory.decodeFile(filePath);
            return this;
        }

        /**
         * DESC: logo的大小
         *
         * @param ratioOfQRCode 取值为占二维码大小的比例
         * Created by jinphy, on 2018/3/30, at 10:14
         */
        public Builder logoSize(float ratioOfQRCode) {
            this.logoSizeRatio = ratioOfQRCode;
            return this;
        }

        public Builder logoShape(ImageUtil.Shape logoShape) {
            this.logoShape = logoShape;
            return this;
        }

        /**
         * DESC: logo的大小
         *
         * @param ratioOfLoge 取值为占logo大小的比例
         * Created by jinphy, on 2018/3/30, at 10:14
         */
        public Builder logoBorderSize(float ratioOfLoge) {
            this.logoBorderSizeRatio = ratioOfLoge;
            return this;
        }

        public Builder logoBorderSize(int logoBorderSize) {
            this.logoBorderSize = logoBorderSize;
            return this;
        }

        public Builder logoBorderColor(int logoBorderColor) {
            this.logoBorderColor = logoBorderColor;
            return this;
        }

        /**
         * DESC: logo形状为矩形时的圆角半径
         * Created by jinphy, on 2018/3/30, at 10:33
         */
        public Builder logoCornerRadius(float ratioOfLogo) {
            this.logoCornerRadiusRatio = ratioOfLogo;
            return this;
        }

        /**
         * DESC: logo形状为矩形时的圆角半径
         * Created by jinphy, on 2018/3/30, at 10:33
         */
        public Builder logoCornerRadius(int logoCornerRadius) {
            this.logoCornerRadius = logoCornerRadius;
            return this;
        }

        public Bitmap make() {
            // 处理参数
            handleParams();
            return new QRCode(this).make();
        }

        public Bitmap into(ImageView imageView) {
            Bitmap qrCode = make();
            if (imageView != null) {
                imageView.setImageBitmap(qrCode);
            }
            return qrCode;
        }


        private void handleParams() {
            if (logo == null) {
                return;
            }
            if (logoBorderSize == -1) {
                logoBorderSize = (int) (logo.getWidth() * logoBorderSizeRatio);
            }
            if (logoCornerRadius == -1) {
                logoCornerRadius = (int) (logo.getWidth() * logoCornerRadiusRatio);
            }
        }
    }

    /**
     * DESC: 扫描二维码成功的回调接口
     * Created by jinphy, on 2018/3/31, at 12:40
     */
    public interface OnScanFinish{
        void call(Content content);
    }

    /**
     * DESC: 一个存放二维码内容的类
     * Created by jinphy, on 2018/3/31, at 12:41
     */
    public static final class Content {

        public static final int SIMPLE_CHAT = 1;

        public static final int TYPE_GROUP = 1;

        public static final int TYPE_USER = 2;


        private int app;

        private int type;

        private String text;

        public int getApp() {
            return app;
        }

        public Content setApp(int app) {
            this.app = app;
            return this;
        }

        public int getType() {
            return type;
        }

        public Content setType(int type) {
            this.type = type;
            return this;
        }

        public String getText() {
            return text;
        }

        public Content setText(String text) {
            this.text = text;
            return this;
        }

        public boolean isMyApp() {
            return app == SIMPLE_CHAT;
        }

        public static Content create() {
            return new Content().setApp(SIMPLE_CHAT);
        }

        @Override
        public String toString() {
            return GsonUtils.toJson(this);
        }

        /**
         * DESC:
         * Created by jinphy, on 2018/3/31, at 13:35
         */
        public static Content parse(String qrCodeContent) {
            try {
                return GsonUtils.toBean(qrCodeContent, Content.class);
            } catch (Exception e) {
                e.printStackTrace();
                return new Content();
            }
        }
    }
}
