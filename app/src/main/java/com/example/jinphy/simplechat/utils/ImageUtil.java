package com.example.jinphy.simplechat.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.jinphy.simplechat.utils.Preconditions.checkNotNull;
/**
 * Created by jinphy on 2017/8/9.
 */

public class ImageUtil {
    public static final String AVATAR_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/simple_chat/avatar" ;
    public static final String PHOTO_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/simple_chat/photo" ;
    public static final String AUDIO_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/simple_chat/audio";


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
                ((ImageView) view).setImageBitmap(bitmap);
            } else {
                view.setBackground(new BitmapDrawable(resources,bitmap));
            }
        }

    }

    /**
     * DESC: 从资源中获取bitmap
     *
     *
     * @param resources bitmap 所在的资源
     * @param reqWidth 指定输出bitmap的宽
     * @param reqHeight 指定输出bitmap的高
     * Created by jinphy, on 2018/1/10, at 17:25
     */
    public static Bitmap getBitmap(Resources resources, @DrawableRes int id,int reqWidth,int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, id, options);

        if (reqWidth == 0 || reqHeight == 0) {
            options.inSampleSize = 4;
        } else {
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        }

        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, id, options);
    }

    /**
     * DESC: 从文件路劲中获取bitmap
     *
     * @param filePath bitmap所在的文件绝对路径路径
     * @param reqWidth 指定输出bitmap的宽
     * @param reqHeight 指定输出bitmap的高
     * Created by jinphy, on 2018/1/10, at 17:26
     */
    public static Bitmap getBitmap(String filePath,int reqWidth,int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        if (reqWidth == 0 || reqHeight == 0) {
            options.inSampleSize = 4;
        } else {
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        }

        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * DESC: 从uri中获取bitmap
     *
     *
     * @param uri bitmap 所在的uri路径
     * @param reqWidth 指定输出bitmap的宽
     * @param reqHeight 指定输出bitmap的高
     * Created by jinphy, on 2018/1/10, at 17:23
     */
    public static Bitmap getBitmap(Uri uri, int reqWidth, int reqHeight) {
        BaseActivity activity = App.activity();
        if (activity == null) {
            return null;
        }
        try {
            ContentResolver resolver = activity.getContentResolver();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(resolver.openInputStream(uri), null, options);

            if (reqWidth == 0 || reqHeight == 0) {
                options.inSampleSize = 4;
            } else {
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            }

            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(resolver.openInputStream(uri), null, options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

    public static Drawable getDrawable(Context context, @DrawableRes int resourceId,@ColorInt int...color) {

        Drawable drawable = ContextCompat.getDrawable(context, resourceId);
        if (color.length>0&& drawable != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setTint(color[0]);
        }
        return drawable;
    }

    public static BitmapDrawable getDrawable(Resources resources, @DrawableRes int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);
        return new BitmapDrawable(resources, bitmap);
    }


    /**
     * DESC: 从相册中选择图片
     * Created by Jinphy, on 2017/12/27, at 11:29
     */
    public static void choosePhoto(Activity activity,int requestCode) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(intent, requestCode);
        } else {
            App.showToast("您的设备没有图库程序！", false);
        }
    }

    /**
     * DESC: 从相机中拍照获取图片，只获取缩略图获取缩略图的方式
     *
     *      注意：这种方式一般用于头像设置
     *
     * Created by Jinphy, on 2017/12/27, at 17:38
     */
    public static boolean takePhotoThumbnail(Activity activity, int requestCode){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(intent, requestCode);
            return true;
        }
        return false;
    }

    /**
     * DESC: 从相机中拍照获取图片，并且需要获取原图的方式
     *
     *      注意：这种方式可以获取原图，但是比较复杂，你需要进行一些配置
     *      步骤如下：
     *          1、在 AndroidManifest.xml 文件中加入如下代码<p>{@code
     *              </application>
     *                  <provider
     *                      android:name="android.support.v4.content.FileProvider"
     *                      android:authorities="{替换成自己的包名}.fileprovider"
     *                      android:exported="false"
     *                      android:grantUriPermissions="true">
     *                  <meta-data
     *                      android:name="android.support.FILE_PROVIDER_PATHS"
     *                      android:resource="@xml/file_paths">
     *                  </meta-data>
     *                  </provider>
     *              </application>
     *          }</p>
     *          2、在xml目录下新建一个file_paths.xml 文件（文件名可以自定义，但是必须与上面{@code android:resource="@xml/file_paths"}）
     *              相同，内容如下：<p>{@code
     *              <paths xmlns:android="http://schemas.android.com/apk/res/android">
     *                  <external-path name="my_files" path="Android/data/{替换成自己的包名}/files/Pictures" />
     *              </paths>
     *          }</p>
     *
     *          3、然后执行该函数
     *
     *
     *
     * @param activity      Activity 对象
     * @param authority     authority值，必须与上面说明的provider标签下的 authorities的值一致
     * @param filePrefix    文件名的前缀, 例如 image
     * @param fileSuffix    文件名的后缀  例如 .png
     * @param requestCode   请求码
     * @return              返回目标文件的绝对路径
     * Created by Jinphy, on 2017/12/27, at 17:44
     */
    public static String takePhotoFullSize(
            Activity activity,
            String authority,
            String filePrefix,
            String fileSuffix,
            int requestCode) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 确保有拍照的应用程序可以接受该intent
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                // 获取存储目录
                File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                // 创建保存图片的文件
                File image = File.createTempFile(filePrefix, fileSuffix, dir);

                // 如果文件为null则抛出异常
                if (image == null) {
                    throw new Exception();
                }

                // 获取文件的url
                Uri uri = FileProvider.getUriForFile(activity, authority, image);

                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                activity.startActivityForResult(intent, requestCode);
                return image.getAbsolutePath();
            } else {
                App.showToast("您的相机没有相机程序！", false);
                return null;
            }
        } catch (Exception e) {
            App.showToast("没有找到储存目录", false);
            return null;
        }
    }


    /**
     * DESC: 获取拍照的缩略图结果
     *
     * Created by Jinphy, on 2017/12/27, at 18:02
     */
    public static Bitmap getThumbnailPhotoFromCamera(Intent data){

        return (Bitmap) data.getExtras().get("data");
    }

    /**
     * DESC: 保存头像到文件
     * Created by jinphy, on 2018/3/1, at 10:01
     */
    public static void storeAvatar(String account, Bitmap bitmap) {
        if (TextUtils.isEmpty(account) || bitmap == null) {
            return;
        }
        File file = new File(AVATAR_PATH,account);
        file.getParentFile().mkdirs();
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * DESC: 从文件中获取图片
     *
     *
     * @return 返回对应账号的头像，如果不存在则返回 null
     * Created by jinphy, on 2018/3/1, at 10:01
     */
    public static Bitmap loadAvatar(String account, int w, int h) {
        File file = new File(AVATAR_PATH, account);
        if (!file.exists()) {
            return null;
        }
        if (w <= 0) {
            w = 50;
        }
        if (h <= 0) {
            h = 50;
        }
        return getBitmap(file.getAbsolutePath(), w, h);
    }

}
