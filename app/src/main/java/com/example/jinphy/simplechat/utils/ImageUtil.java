package com.example.jinphy.simplechat.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import static com.example.jinphy.simplechat.utils.Preconditions.checkNotNull;
/**
 * Created by jinphy on 2017/8/9.
 */

public class ImageUtil {

    public static Builder from(@NonNull Context context) {
        checkNotNull(context);
        return new Builder(context);
    }

    public static class  Builder{
        private Resources resources;
        private int id = -1;
        private String filePath;

        private Builder(Context context){
            this.resources = context.getResources();
        }

        public Builder with(@DrawableRes int resourceId) {
            if (resourceId < 0) {
                throw new IllegalArgumentException("resourceId cannot be negative");
            }
            this.id = resourceId;
            return this;
        }

        public Builder width(@NonNull String filePath) {
            this.filePath = checkNotNull(filePath);
            return this;
        }

        public void into(View view) {
            if (id < 0 && TextUtils.isEmpty(filePath)) {
                return;
            }
            Bitmap bitmap;
            if (id > 0) {
                bitmap = getBitmap(resources, id, view.getMeasuredWidth() , view.getMeasuredHeight());
            } else {
                bitmap = getBitmap(filePath, view.getMeasuredWidth() , view.getMeasuredHeight());
            }
            if (view instanceof ImageView) {
                ((ImageView) view)
                        .setImageBitmap(bitmap);
            } else {
                view.setBackground(new BitmapDrawable(resources,bitmap));
            }
        }

    }

    public static Bitmap getBitmap(Resources resources, @DrawableRes int id,int reqWidth,int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, id, options);

        if (reqWidth == 0 || reqHeight == 0) {
            options.inSampleSize = 4;
        } else {
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        }

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, id, options);
    }

    public static Bitmap getBitmap(String filePath,int reqWidth,int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(
            int reqWidth,
            int reqHeight,
            int rawWidth,
            int rawHeight) {
        int inSampleSize = 1;
        if (rawWidth > reqWidth || rawHeight > reqHeight) {
            final int halfWidth = rawWidth/2;
            final int halfHeight = rawHeight/2;
            while ( (halfWidth / inSampleSize > reqWidth)
                    && (halfHeight / inSampleSize > reqHeight)) {
                inSampleSize *=2;
            }
        }
        return inSampleSize;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        int rawWidth = options.outWidth;
        int rawHeight = options.outHeight;
        return calculateInSampleSize(reqWidth, reqHeight, rawWidth, rawHeight);
    }

    public static BitmapDrawable getDrawable(Context context, @DrawableRes int resourceId) {
        return getDrawable(context.getResources(), resourceId);
    }

    public static BitmapDrawable getDrawable(Resources resources, @DrawableRes int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);
        return new BitmapDrawable(resources, bitmap);
    }


}
