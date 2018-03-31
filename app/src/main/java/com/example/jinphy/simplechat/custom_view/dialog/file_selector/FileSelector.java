package com.example.jinphy.simplechat.custom_view.dialog.file_selector;

import android.content.Context;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.custom_libs.SChain;
import com.example.jinphy.simplechat.custom_libs.my_adapter.MyAdapter;
import com.example.jinphy.simplechat.custom_view.dialog.my_dialog.MyDialog;
import com.example.jinphy.simplechat.modules.chat.ChatFragment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * DESC:
 * Created by jinphy on 2018/3/28.
 */

public class FileSelector {


    private final Context context;
    private OnSelect onSelect;
    private MyDialog.Holder dialogHolder;


    private TextView btnConfirm;
    private View btnCancel;
    private View btnBackward;
    private View btnForward;
    private TextView filePathView;
    private TextView currentDirView;
    private RecyclerView recyclerView;


    private MyFile currentDir;
    private MyAdapter<MyFile> adapter;
    private FileSelectorPresenter presenter;
    private LinearLayoutManager linearLayoutManager;

    /**
     * DESC: 缓存当前的遍历过的所有文件
     * Created by jinphy, on 2018/3/28, at 13:58
     */
    private Map<String, List<MyFile>> filesMap;

    /**
     * DESC: 保存向前遍历过的文件夹
     * Created by jinphy, on 2018/3/28, at 13:57
     */
    private Stack<MyFile> preFileStack;

    /**
     * DESC: 保存向后遍历过的文件夹
     * Created by jinphy, on 2018/3/28, at 13:57
     */
    private Stack<MyFile> nextFileStack;

    /**
     * DESC: 当前所选的文件数
     * Created by jinphy, on 2018/3/28, at 13:58
     */
    private int selectedCount = 0;


    public static FileSelector from(Context context) {
        return new FileSelector(context);
    }

    public FileSelector onSelect(OnSelect onSelect){
        this.onSelect = onSelect;
        return this;
    }

    public FileSelector make() {
        // TODO: 2018/3/28
        return this;
    }

    //----------------------------------------------------------------------------------------------

    private FileSelector(Context context) {
        this.context = context;
        initData();
        initDialog();
        findViews();
        setupView();
        presenter.loadFiles(currentDir.getPath());
    }

    private void initData() {
        currentDir = MyFile.create(Environment.getExternalStorageDirectory());
        presenter = new FileSelectorPresenter(this);
        filesMap = new HashMap<>();
        preFileStack = new Stack<>();
        nextFileStack = new Stack<>();
    }


    private void initDialog() {
        dialogHolder = MyDialog.create(context)
                .width(350)
                .cancelable(false)
                .view(R.layout.layout_file_selector_dialog)
                .display();
    }

    private void findViews() {
        btnConfirm = dialogHolder.view.findViewById(R.id.btn_confirm);
        btnCancel = dialogHolder.view.findViewById(R.id.btn_cancel);
        btnBackward = dialogHolder.view.findViewById(R.id.btn_backward);
        btnForward = dialogHolder.view.findViewById(R.id.btn_forward);
        filePathView = dialogHolder.view.findViewById(R.id.file_path_view);
        currentDirView = dialogHolder.view.findViewById(R.id.title_view);
        recyclerView = dialogHolder.view.findViewById(R.id.recycler_view);
    }


    private void setupView() {
        currentDirView.setText(currentDir.getName());
        filePathView.setText(currentDir.getPath());


        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = MyAdapter.<MyFile>newInstance()
                .onGetItemViewType(item -> 0)
                .onInflate(viewType -> R.layout.layout_file_selector_item)
                .onCreateView(holder -> {
                    // 图标
                    holder.imageViews(R.id.icon_view);
                    // 文件名、文件大小
                    holder.textViews(R.id.name_view, R.id.size_view);
                    // 选择框
                    holder.checkBoxes(R.id.check_box);
                })
                .onBindView((holder, item, position) -> {
                    holder.textView[0].setText(item.getName());
                    holder.checkBox[0].setChecked(item.isSelect());
                    if (item.isDir()) {
                        holder.imageView[0].setImageResource(R.drawable.ic_folder_24dp);
                        holder.textView[1].setText("");
                        holder.checkBox[0].setVisibility(View.GONE);
                    } else {
                        holder.imageView[0].setImageResource(R.drawable.ic_file_24dp);
                        holder.textView[1].setText(item.getSize());
                        holder.checkBox[0].setVisibility(View.VISIBLE);
                    }
                    holder.setClickedViews(holder.item);
                })
                .onClick((v, item, holder, type, position) -> {
                    if (item.isDir()) {
                        preFileStack.push(currentDir);
                        if (nextFileStack.size() > 0) {
                            if (nextFileStack.peek().getPath().equals(item.getPath())) {
                                nextFileStack.pop();
                            } else {
                                nextFileStack.clear();
                            }
                        }
                        gotoDir(item);
                    } else {
                        item.setSelect(!item.isSelect());
                        holder.checkBox[0].setChecked(item.isSelect());
                        if (item.isSelect()) {
                            selectedCount++;
                        } else {
                            selectedCount--;
                        }
                        if (selectedCount == 0) {
                            btnConfirm.setText("确定");
                        } else {
                            SChain.with("确定(")
                                    .append(selectedCount)
                                    .colorForText(ContextCompat.getColor(context, R.color.colorAccent))
                                    .append(")")
                                    .into(btnConfirm);
                        }
                    }
                })
                .into(recyclerView);



        btnCancel.setOnClickListener(v->{
            dialogHolder.dialog.dismiss();
        });

        btnConfirm.setOnClickListener(v -> {
            List<String> filePaths = new LinkedList<>();
            dialogHolder.dialog.dismiss();

            Observable.fromIterable(filesMap.entrySet())          // 遍历filesMap中保存的文件夹
                    .subscribeOn(Schedulers.io())
                    .map(Map.Entry::getValue)                     // 获取当前遍历的文件夹中的所有文件
                    .flatMap(Observable::fromIterable)            // 遍历当前文件夹下的所有文件
                    .filter(MyFile::isSelect)
                    .map(MyFile::getPath)
                    .doOnNext(filePaths::add)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> {
                        if (onSelect != null) {
                            onSelect.call(filePaths);
                        }
                    })
                    .subscribe();
        });

        btnBackward.setOnClickListener(v -> {
            if (preFileStack.size() == 0) {
                return;
            }
            nextFileStack.push(currentDir);
            MyFile myFile = preFileStack.pop();
            gotoDir(myFile);
        });

        btnForward.setOnClickListener(v -> {
            if (nextFileStack.size() == 0) {
                return;
            }
            preFileStack.push(currentDir);
            MyFile myFile = nextFileStack.pop();
            gotoDir(myFile);
        });

    }

    /**
     * DESC: 到指定的文件目录下
     * Created by jinphy, on 2018/3/28, at 11:12
     */
    private void gotoDir(MyFile myFile) {
        currentDir = myFile;
        currentDirView.setText(myFile.getName());
        filePathView.setText(myFile.getPath());
        if (filesMap.containsKey(currentDir.getPath())) {
            adapter.update(filesMap.get(currentDir.getPath()));
        } else {
            presenter.loadFiles(currentDir.getPath());
        }
    }


    /**
     * DESC: 文件加载完成时回调该方法
     * Created by jinphy, on 2018/3/28, at 9:43
     */
    void onUpdateFiles(List<MyFile> myFiles) {
        adapter.update(myFiles);
        filesMap.put(currentDir.getPath(), myFiles);
    }

    /**
     * DESC: 文件选择结果回调
     * Created by jinphy, on 2018/3/28, at 11:12
     */
    public interface OnSelect{
        void call(List<String> filePaths);
    }
}
