package com.bemetoy.bp.plugin.tasks;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.stub.app.base.BasePresent;
import com.bemetoy.stub.app.base.BaseView;

import java.util.List;

/**
 * Created by tomliu on 16-9-7.
 */
public interface TaskContract {

    interface View extends BaseView {
        void onTaskLoaded(List<Racecar.Task> tasks);
    }

    interface Present extends BasePresent {
        void loadTask(boolean showView);
    }
}
