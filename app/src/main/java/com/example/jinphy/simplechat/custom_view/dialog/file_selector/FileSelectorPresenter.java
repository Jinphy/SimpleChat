package com.example.jinphy.simplechat.custom_view.dialog.file_selector;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * DESC:
 * Created by jinphy on 2018/3/28.
 */

public class FileSelectorPresenter {

    private FileSelector fileSelector;


    public FileSelectorPresenter(FileSelector fileSelector) {
        this.fileSelector = fileSelector;
    }

    /**
     * DESC: 加载指定路径下的所有文件
     * Created by jinphy, on 2018/3/28, at 9:40
     */
    public void loadFiles(String dir) {
        Observable.just(dir)
                .subscribeOn(Schedulers.io())
                .map(File::new)
                .map(File::listFiles)
                .map(MyFile::create)
                .map(MyFile::sort)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(fileSelector::onUpdateFiles)
                .subscribe();
    }

}
