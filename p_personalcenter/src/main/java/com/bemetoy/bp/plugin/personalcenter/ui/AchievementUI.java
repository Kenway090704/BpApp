package com.bemetoy.bp.plugin.personalcenter.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.personalcenter.R;
import com.bemetoy.bp.plugin.personalcenter.databinding.UiAchievementBinding;
import com.bemetoy.bp.plugin.personalcenter.databinding.UiAchievementItemBinding;
import com.bemetoy.bp.plugin.personalcenter.model.NetSceneGetAchieve;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.bp.uikit.widget.recyclerview.ExtRecyclerViewAdapter;
import com.bemetoy.bp.uikit.widget.recyclerview.VRecyclerView;
import com.bemetoy.stub.network.NetSceneCallbackLoadingWrapper;
import com.bemetoy.stub.ui.LoadingDialog;

/**
 * Created by albieliang on 16/4/21.
 */
public class AchievementUI extends BaseDataBindingActivity<UiAchievementBinding> {

    private static final String TAG = "Personal.AchievementUI";

    private NetSceneGetAchieve getAchieve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onDestroy() {
        if(getAchieve != null) {
            getAchieve.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_achievement;
    }

    protected void init() {

        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final DefaultAdapter adapter = new DefaultAdapter(this);

        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.show();
        getAchieve = new NetSceneGetAchieve(new NetSceneCallbackLoadingWrapper<Racecar.GetAchieveResponse>() {

            @Override
            public void onResponse(IRequest request, Racecar.GetAchieveResponse result) {
                if(request == null) {
                    Log.e(TAG,"GetAchieveResponse is null");
                    return;
                }

                if(result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                    adapter.appendData(result.getAchieveList());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNetSceneEnd() {
                super.onNetSceneEnd();
                dialog.dismiss();
            }
        });

        getAchieve.doScene();


//        mBinding.dataRv.setLayoutManager(new LinearLayoutManager(this));
        final GridLayoutManager mgr = new GridLayoutManager(this, 5);
        mBinding.dataRv.setLayoutManager(mgr);
//        mBinding.dataRv.addItemDecoration(new DividerItemDecoration(this));
        mBinding.dataRv.setHasFixedSize(true);
        mBinding.dataRv.setAdapter(adapter);
        mBinding.dataRv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int count = mBinding.dataRv.getMeasuredWidth() / getResources().getDimensionPixelSize(R.dimen.achievement_item_width);
                Log.i(TAG, "span count : %d.", count);
                if (count > 0) {
                    mgr.setSpanCount(count);
                }
            }
        });

        mBinding.dataRv.setOnItemClickListener(new VRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                Racecar.Achieve achieve = adapter.getItem(position);
                Intent intent = new Intent(AchievementUI.this, AchievementDetailInfoUI.class);
                intent.putExtra(ProtocolConstants.IntentParams.ACHIEVE_INFO,  achieve);
                startActivity(intent);
            }
        });
        mBinding.actionBtn.setVisibility(View.INVISIBLE);// TODO 为13号上线做修改
        mBinding.actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AchievementUI.this, AchievementRuleUI.class);
                startActivity(intent);
            }
        });
    }
    
    private static class DefaultAdapter extends ExtRecyclerViewAdapter<Racecar.Achieve> {

        private Context context;
        
        public DefaultAdapter(Context context) {
            this.context = context;
        }
        
        @Override
        public View createItemView(ViewGroup parent, int viewType) {
            return LayoutInflater.from(context).inflate(R.layout.ui_achievement_item, parent, false);
        }

        @Override
        public IViewHolder<Racecar.Achieve> onCreateViewHolder(View view, int viewType) {
            return new DataBindingViewHolder<UiAchievementItemBinding, Racecar.Achieve>() {

                @Override
                public void onBind(Racecar.Achieve item, int viewType) {
                    mBinding.setAchieve(item);
                }
            };
        }
    }
}
