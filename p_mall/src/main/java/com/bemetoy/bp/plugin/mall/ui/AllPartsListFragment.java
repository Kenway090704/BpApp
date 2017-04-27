package com.bemetoy.bp.plugin.mall.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.mall.Injection;
import com.bemetoy.bp.plugin.mall.MallContract;
import com.bemetoy.bp.plugin.mall.MallPresent;
import com.bemetoy.bp.plugin.mall.R;
import com.bemetoy.bp.plugin.mall.databinding.UiPartsListBinding;
import com.bemetoy.bp.plugin.mall.ui.adapter.PartsListAdapter;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.widget.recyclerview.DividerGridItemDecoration;
import com.bemetoy.bp.uikit.widget.recyclerview.VRecyclerView;
import com.bemetoy.stub.app.base.BaseViewFragmentImpl;

import java.util.List;

/**
 * Created by Tom on 2016/4/22.
 */
public class AllPartsListFragment extends BaseViewFragmentImpl<UiPartsListBinding> implements MallContract.View {

    private static final String TAG = "Mall.AllPartsListFragment";
    private PartsListAdapter mPartsListAdapter;
    private MallContract.Present mallPresent;
    private boolean isFirstLoad = true;


    public static AllPartsListFragment getInstance() {
        return new AllPartsListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mallPresent = new MallPresent(Injection.provideMallDataSource(), this);
        int type = getArguments().getInt(ProtocolConstants.IntentParams.PART_TYPE);
        mallPresent.setFilter(type);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isFirstLoad && isVisibleToUser) {
            isFirstLoad = false;
            mallPresent.start();
        }
    }

    @Override
    public void init() {
        mPartsListAdapter = new PartsListAdapter(this.getContext());
        mBinding.partVr.setAdapter(mPartsListAdapter);
        mBinding.partVr.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int viewSize = metrics.widthPixels  - 2 * getResources().getDimensionPixelSize(R.dimen.next_btn_margin_right)
                - 2 * getResources().getDimensionPixelSize(R.dimen.smallest_padding);
        int itemWidth = viewSize / 3;
        mPartsListAdapter.setItemWidth(itemWidth);

        mBinding.partVr.addItemDecoration(new DividerGridItemDecoration(this.getContext(), R.drawable.divider));
        mBinding.partVr.setOnItemClickListener(new VRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                final Racecar.GoodsListResponse.Item part = mPartsListAdapter.getItem(position);
                Log.d(TAG, "part id is %s", part.getGoodsId());
                Bundle bundle = new Bundle();
                bundle.putSerializable(ProtocolConstants.IntentParams.PART_ITEM, part);
                PluginStubBus.doAction(getContext(), PluginConstants.Plugin.PLUGIN_NAME_P_MALL, PluginConstants.Mall.Action.CMD_START_PART_UI, 0, bundle);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.ui_parts_list;
    }

    @Override
    public void loadAddParts(List<Racecar.GoodsListResponse.Item> partList) {
        mPartsListAdapter.setData(partList);
        mPartsListAdapter.notifyDataSetChanged();
        mBinding.placeHolderTv.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showNetworkConnectionError() {
        super.showNetworkConnectionError();
        mBinding.placeHolderTv.setVisibility(View.VISIBLE);
        mBinding.placeHolderTv.setText(R.string.network_error_notice);
    }

    @Override
    public void showLocalNetworkError() {
        super.showLocalNetworkError();
        mBinding.placeHolderTv.setVisibility(View.VISIBLE);
        mBinding.placeHolderTv.setText(R.string.network_error_notice);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mallPresent.stop();
    }

    @Override
    protected Runnable getRetryAction() {
        return new Runnable() {
            @Override
            public void run() {
                mallPresent.loadAllPart(false);
            }
        };
    }
}
