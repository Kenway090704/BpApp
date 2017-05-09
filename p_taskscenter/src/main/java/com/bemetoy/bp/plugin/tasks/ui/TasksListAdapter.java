package com.bemetoy.bp.plugin.tasks.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.tasks.R;
import com.bemetoy.bp.plugin.tasks.databinding.TasksListItemBinding;
import com.bemetoy.bp.plugin.tasks.model.TaskCenterLogic;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.widget.recyclerview.ExtRecyclerViewAdapter;
import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import rx.functions.Action;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by albieliang on 16/5/10.
 */
public class TasksListAdapter extends ExtRecyclerViewAdapter<Racecar.Task> {

    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private LayoutInflater mInflater;
    private String mCategory;

    public TasksListAdapter(LayoutInflater inflater, String category) {
        mInflater = inflater;
        mCategory = category;
    }

    @Override
    public IViewHolder<Racecar.Task> onCreateViewHolder(View view, int viewType) {
        return new GameRecordViewHolder(mCategory);
    }

    @Override
    public View createItemView(ViewGroup parent, int viewType) {
        return mInflater.inflate(R.layout.tasks_list_item, parent, false);
    }

    /**
     *
     */
    private class GameRecordViewHolder extends DataBindingViewHolder<TasksListItemBinding, Racecar.Task> {
        GameRecordViewHolder(String category) {
            mCategory = category;
        }

        @Override
        public void onCreateBinding(View itemView, int viewType) {
            super.onCreateBinding(itemView, viewType);
        }

        @Override
        public void onBind(final Racecar.Task item, int viewType) {
            super.onBind(item, viewType);
            mBinding.setTask(item);
            switch (item.getStatus()) {
                case ProtocolConstants.TaskStatus.UN_FINISH:
                    mBinding.actionBtn.setBackgroundResource(R.drawable.task_to_do_drawable);
                    mBinding.titleTv.setBackgroundResource(R.drawable.task_undone_bg);
                    break;
                case ProtocolConstants.TaskStatus.FINISHED:
                    mBinding.actionBtn.setBackgroundResource(R.drawable.task_get_reward_drawable);
                    mBinding.titleTv.setBackgroundResource(R.drawable.task_undone_bg);
                    break;
                case ProtocolConstants.TaskStatus.DONE:
                    mBinding.actionBtn.setBackgroundResource(R.drawable.task_finished);
                    mBinding.titleTv.setBackgroundResource(R.drawable.task_finished_bg);
                    break;
            }

            if(mBinding.actionBtn.getTag(R.id.click_action) == null ||
                    !(mBinding.actionBtn.getTag(R.id.click_action) instanceof ClickAction)) {
                ClickAction action = new ClickAction();
                action.task = item;
                action.context = mBinding.actionBtn.getContext();
                compositeSubscription.add(RxView.clicks(mBinding.actionBtn).
                        throttleFirst(3 * 1000, TimeUnit.MILLISECONDS).subscribe(action));
                mBinding.actionBtn.setTag(R.id.click_action, action);
            } else {
                ClickAction action = (ClickAction) mBinding.actionBtn.getTag(R.id.click_action);
                action.task = item;
                action.context = mBinding.actionBtn.getContext();
            }
        }

        @Override
        public void onRecycle() {
            super.onRecycle();
        }
    }

    /**
     * unsubscribe all click action.
     */
    public void reset() {
        compositeSubscription.unsubscribe();
    }


    private class ClickAction implements Action1<Void> {
        Racecar.Task task;
        Context context;

        @Override
        public void call(Void v) {
            if(context == null){
                return;
            }
            switch (task.getStatus()) {
                case ProtocolConstants.TaskStatus.UN_FINISH:
                    Log.e("TaskListAdapter",task.getJumpId()+"");
                    TaskCenterLogic.toDoTask(context, task.getJumpId());
                    break;
                case ProtocolConstants.TaskStatus.FINISHED:
                    TaskCenterLogic.getReward(context, task.getTaskId(), new TaskCenterLogic.OnGetRewardCallback() {
                        @Override
                        public void onGetReward() {
                            if(getItems().remove(task)) {
                                Racecar.Task newTask = Racecar.Task.newBuilder().mergeFrom(task).setStatus(ProtocolConstants.TaskStatus.DONE).build();
                                getItems().add(newTask);
                                notifyDataSetChanged();
                            }
                        }
                    });
                    break;
                case ProtocolConstants.TaskStatus.DONE:
                    Intent intent = new Intent(context, TaskDetailUI.class);
                    intent.putExtra(ProtocolConstants.IntentParams.TASK_INFO,  task);
                    context.startActivity(intent);
                    break;
            }
        }
    }

}