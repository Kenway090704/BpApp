package com.bemetoy.bp.plugin.tasks;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.tasks.model.TaskCenterDataSource;
import com.bemetoy.bp.protocols.ProtocolConstants;

import junit.framework.Assert;

import java.util.List;

/**
 * Created by tomliu on 16-9-7.
 */
public class TaskPresent implements TaskContract.Present {

    private TaskCenterDataSource dataSource;
    private TaskContract.View view;

    public TaskPresent(TaskCenterDataSource dataSource, TaskContract.View view) {
        Assert.assertNotNull(dataSource);
        Assert.assertNotNull(view);
        this.dataSource = dataSource;
        this.view = view;
    }

    @Override
    public void loadTask(boolean showView) {
        if(view.isActive() && showView) {
            view.showLoading(true);
        }
        this.dataSource.loadTasks(new TaskCenterDataSource.LoadTaskCallback(){
            @Override
            public void onTaskLoaded(List<Racecar.Task> tasks) {
                view.showLoading(false);
                if(!view.isActive()) {
                    return;
                }
                view.onTaskLoaded(tasks);
            }

            @Override
            public void onTaskLoadedFailed(int errorCode) {
                view.showLoading(false);
                if(!view.isActive()) {
                    return;
                }

                if(errorCode == ProtocolConstants.ErrorType.NETWORK_LOCAL_ERROR) {
                    view.showLocalNetworkError();
                } else if(errorCode == ProtocolConstants.ErrorType.NETWORK_SVR_ERROR){
                    view.showNetworkConnectionError();
                }
            }
        });
    }

    @Override
    public void start() {
        loadTask(true);
    }

    @Override
    public void stop() {

    }
}
