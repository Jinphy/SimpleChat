package com.example.jinphy.simplechat.models.file_task;

/**
 * DESC:
 * Created by jinphy on 2018/3/21.
 */

public interface FileTaskDataSource {


    void save(FileTask fileTask);

    void update(FileTask fileTask);

    void remove(long id);

    void remove(FileTask fileTask);

    FileTask get(long id);
}
