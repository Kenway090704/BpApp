package com.bemetoy.bp.plugin.tasks.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.stub.network.NetSceneResponseCallback;

/**
 * Created by tomliu on 16-9-7.
 */
public class TaskCenterDataSourceRemote implements TaskCenterDataSource {

    private int type;

    public TaskCenterDataSourceRemote(int type) {
        this.type = type;
    }

    @Override
    public void loadTasks(final LoadTaskCallback callback) {
        NetSceneGetTask netSceneGetTask = new NetSceneGetTask(type, new NetSceneResponseCallback<Racecar.GetTaskResponse>() {
            @Override
            public void onRequestFailed(IRequest request) {
                callback.onTaskLoadedFailed(ProtocolConstants.ErrorType.NETWORK_SVR_ERROR);
            }

            @Override
            public void onLocalNetworkIssue(IRequest request) {
                callback.onTaskLoadedFailed(ProtocolConstants.ErrorType.NETWORK_LOCAL_ERROR);
            }

            @Override
            public void onResponse(IRequest request, Racecar.GetTaskResponse result) {
                if (result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                    callback.onTaskLoaded(result.getTaskList());
                }
            }
        });
        netSceneGetTask.doScene();
    }
}
