package com.bemetoy.bp.plugin.tasks.model;

import com.bemetoy.bp.autogen.protocol.Racecar;

import java.util.List;

/**
 * Created by tomliu on 16-9-7.
 */
public interface TaskCenterDataSource {
    interface LoadTaskCallback{
        void onTaskLoaded(List<Racecar.Task> tasks);

        void onTaskLoadedFailed(int errorCode);
    }

    void loadTasks(LoadTaskCallback callback);
}
