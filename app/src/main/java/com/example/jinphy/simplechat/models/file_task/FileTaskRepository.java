package com.example.jinphy.simplechat.models.file_task;

import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseRepository;

import io.objectbox.Box;

/**
 * DESC:
 * Created by jinphy on 2018/3/21.
 */

public class FileTaskRepository extends BaseRepository implements FileTaskDataSource{

    private Box<FileTask> fileTaskBox;


    private static class InstanceHolder{
        static final FileTaskRepository DEFAULT = new FileTaskRepository();
    }

    public static FileTaskRepository getInstance() {
        return InstanceHolder.DEFAULT;
    }

    private FileTaskRepository() {
        fileTaskBox = App.boxStore().boxFor(FileTask.class);
    }
    @Override
    public void save(FileTask fileTask) {
        if (fileTask == null) {
            return;
        }
        fileTask.setId(0);
        fileTaskBox.put(fileTask);
    }

    @Override
    public void update(FileTask fileTask) {
        if (fileTask == null) {
            return;
        }
        fileTaskBox.put(fileTask);
    }

    @Override
    public void remove(long id) {
        fileTaskBox.remove(id);
    }

    @Override
    public void remove(FileTask fileTask) {
        fileTaskBox.remove(fileTask);
    }

    @Override
    public FileTask get(long id) {
        return fileTaskBox.get(id);
    }
}
