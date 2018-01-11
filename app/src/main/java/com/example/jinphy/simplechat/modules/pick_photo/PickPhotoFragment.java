package com.example.jinphy.simplechat.modules.pick_photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.custom_libs.SChain;
import com.example.jinphy.simplechat.custom_view.MenuItemView;
import com.example.jinphy.simplechat.models.event_bus.EBBitmap;
import com.example.jinphy.simplechat.models.event_bus.EBIntent;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.example.jinphy.simplechat.utils.StringUtils;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

public class PickPhotoFragment extends BaseFragment<PickPhotoPresenter>
        implements PickPhotoContract.View{
    public static final String OPTION = "OPTION";
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;

    public static final String AUTHORITIES = "com.example.jinphy.simplechat.fileprovider";
    private final String IMG_FILE_NAME_PREFIX = "user_avatar";
    private final String IMG_FILE_NAME_SUFFIX = ".png";

    private PickPhotoActivity.Option option;
    private String filePath;

    private TextView btnRecapture;
    private TextView btnFinish;
    private CropImageView cropper;
    private TextView rotateDegreeView;
    private FloatingActionButton fab;


    /**
     * DESC: 判断是否是顺时针方向
     * Created by jinphy, on 2018/1/10, at 13:38
     */
    private boolean isRotateClockwise=true;
    private int degreeForStep = 90;
    private int currentDegree;
    private boolean isRecapture;


    public PickPhotoFragment() {
        // Required empty public constructor
    }


    public static PickPhotoFragment newInstance(String option) {
        PickPhotoFragment fragment = new PickPhotoFragment();
        Bundle args = new Bundle();
        args.putString(OPTION,option);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments == null) {
            ObjectHelper.throwRuntime("arguments cannot be null!");
        }
        String option = arguments.getString(OPTION);
        if (PickPhotoActivity.Option.TAKE_PHOTO.get().equals(option)) {
            this.option = PickPhotoActivity.Option.TAKE_PHOTO;
        } else {
            this.option = PickPhotoActivity.Option.CHOOSE_PHOTO;
        }
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_pick_photo;
    }

    /**
     * DESC: 启动该Fragmen时，先获取图片然后在进行裁剪
     * Created by jinphy, on 2018/1/10, at 17:03
     */
    @Override
    protected void initData() {
        if (option == PickPhotoActivity.Option.TAKE_PHOTO) {
            // 相机
            filePath = ImageUtil.takePhotoFullSize(
                    activity(),
                    AUTHORITIES,
                    IMG_FILE_NAME_PREFIX,
                    IMG_FILE_NAME_SUFFIX,
                    TAKE_PHOTO);
        } else {
            // 相册
            ImageUtil.choosePhoto(activity(), CHOOSE_PHOTO);
        }
    }

    @Override
    protected void findViewsById(View view) {
        btnRecapture = view.findViewById(R.id.btn_recapture);
        btnFinish = view.findViewById(R.id.btn_finish);
        cropper = view.findViewById(R.id.crop_image_view);
        rotateDegreeView = view.findViewById(R.id.rotate_degree_view);
        fab = view.findViewById(R.id.fab_rotate);
    }

    @Override
    protected void setupViews() {
        if (option == PickPhotoActivity.Option.TAKE_PHOTO) {
            btnRecapture.setText("重拍");
        } else {
            btnRecapture.setText("重选");
        }
        rotateDegreeView.setText(StringUtils.formatDegree(currentDegree));
    }

    @Override
    protected void registerEvent() {
        fab.setOnClickListener(view->{
            updateDegree();
        });
        fab.setOnLongClickListener(v -> {
            isRotateClockwise = !isRotateClockwise;
            if (isRotateClockwise) {
                fab.setImageResource(R.drawable.ic_rotate_right_24dp);
            } else {
                fab.setImageResource(R.drawable.ic_rotate_left_24dp);
            }
            return true;
        });
        btnRecapture.setOnClickListener(view->{
            initData();
            isRecapture = true;
        });
        btnFinish.setOnClickListener(view->{
            EventBus.getDefault().post(new EBBitmap(true, cropper.getCroppedImage(200,200)));
            finishActivity();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

    private void updateDegree() {
        if (isRotateClockwise) {
            currentDegree = (currentDegree + degreeForStep) % 360;
        } else {
            currentDegree = (currentDegree - degreeForStep) % 360;
        }
        rotateDegreeView.setText(StringUtils.formatDegree(currentDegree));
        cropper.setRotatedDegrees(currentDegree);
    }

    @Override
    public boolean onBackPressed() {
        new MaterialDialog.Builder(activity())
                .title("提示")
                .titleColor(colorPrimary())
                .icon(ImageUtil.getDrawable(activity(), R.drawable.ic_warning_24dp, colorPrimary()))
                .content("退出前是否保存图片！")
                .positiveText("是")
                .negativeText("否")
                .neutralText("取消")
                .cancelable(false)
                .contentGravity(GravityEnum.CENTER)
                .positiveColor(colorPrimary())
                .negativeColorRes(R.color.color_red_D50000)
                .neutralColorRes(R.color.half_alpha_gray)
                .onPositive((dialog, which) -> {
                    // 保存图片并退出
                    EventBus.getDefault().post(new EBBitmap(true, cropper.getCroppedImage()));
                    finishActivity();
                })
                .onNegative((dialog, which) -> {
                    // 不保存图片且退出
                    EventBus.getDefault().post(new EBBitmap(false, null));
                    finishActivity();
                })
                .onNeutral((dialog, which)->{
                    // 取消退出，无操作
                })
                .show();
        // activity不用处理，则返回false
        return false;
    }

    /**
     * DESC: 接收来之{@link PickPhotoActivity#onActivityResult(int, int, Intent)} 方法发送的图片获取结果
     *
     * Created by jinphy, on 2018/1/10, at 11:30
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveBitmapFromActivity(EBIntent result) {
        if (!result.ok) {
            if (!isRecapture) {
                finishActivity();
            }
            return;
        }
        Bitmap bitmap=null;
        switch (result.requestCode) {
            case TAKE_PHOTO:
                bitmap = ImageUtil.getBitmap(
                        filePath,
                        500,
                        500);
                break;
            case CHOOSE_PHOTO:
                bitmap = getBitmapFromAlbum(result.data, 500, 500);
                break;
            default:
                break;
        }
        cropper.setImageBitmap(bitmap);
    }

    /**
     * DESC: 获取从相册中选择的图片
     * Created by jinphy, on 2018/1/10, at 17:15
     */
    private Bitmap getBitmapFromAlbum(Intent data, int reqWidth, int reqHeight){
        Uri uri = data.getData();
        if (uri == null) {
            return data.getParcelableExtra("data");
        } else {
            return ImageUtil.getBitmap(uri, reqWidth, reqHeight);
        }
    }
}
