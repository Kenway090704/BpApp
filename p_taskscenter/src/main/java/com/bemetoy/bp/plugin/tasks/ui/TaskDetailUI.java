package com.bemetoy.bp.plugin.tasks.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.tasks.R;
import com.bemetoy.bp.plugin.tasks.databinding.UiTaskDetailBinding;
import com.bemetoy.bp.plugin.tasks.model.TaskCenterLogic;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;

/**
 * Created by Tom on 2016/6/7.
 */
public class TaskDetailUI extends BaseDataBindingActivity<UiTaskDetailBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        final Racecar.Task task = (Racecar.Task) (getIntent().getSerializableExtra(ProtocolConstants.IntentParams.TASK_INFO));
        mBinding.setTask(task);
        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        View.OnClickListener listener = null;

        switch (task.getStatus()) {
            case ProtocolConstants.TaskStatus.UN_FINISH:
                mBinding.actionBtn.setBackgroundResource(R.drawable.task_detail_to_do_drawable);
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TaskCenterLogic.toDoTask(TaskDetailUI.this, task.getJumpId());
                    }
                };
                break;
            case ProtocolConstants.TaskStatus.FINISHED:
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TaskCenterLogic.getReward(TaskDetailUI.this, task.getTaskId(), new TaskCenterLogic.OnGetRewardCallback() {
                            @Override
                            public void onGetReward() {
                                mBinding.actionBtn.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent();
                                intent.setAction(ProtocolConstants.BroadCastAction.TASK_UPDATE_ACTION);
                                sendBroadcast(intent);
                            }
                        });
                    }
                };
                mBinding.actionBtn.setBackgroundResource(R.drawable.task_detail_get_reward_drawable);
                break;
            case ProtocolConstants.TaskStatus.DONE:
                mBinding.actionBtn.setVisibility(View.INVISIBLE);
                break;
        }

        mBinding.actionBtn.setOnClickListener(listener);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_task_detail;
    }
}
