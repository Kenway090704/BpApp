package com.bemetoy.bp.plugin.operatoins.ui;

import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.operatoins.R;
import com.bemetoy.bp.plugin.operatoins.databinding.UiWelfareBinding;
import com.bemetoy.bp.plugin.operatoins.ui.adapter.OperationListAdapter;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.bp.uikit.widget.recyclerview.DividerGridItemDecoration;

import java.util.ArrayList;

/**
 * Created by Tom on 2016/5/11.
 */
public class OperationCenterUI extends BaseDataBindingActivity<UiWelfareBinding>{

    public static final String TAG = "UI.OperationCenterUI";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        final OperationListAdapter adapter = new OperationListAdapter(this);
        mBinding.welfareVr.setAdapter(adapter);
        final StaggeredGridLayoutManager mgr = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        mBinding.welfareVr.setLayoutManager(mgr);
        mBinding.welfareVr.addItemDecoration(new DividerGridItemDecoration(this, R.drawable.divider));

        ArrayList<Racecar.Operation> operations = new ArrayList();
        if(getIntent().hasExtra(ProtocolConstants.IntentParams.OPERATION_LIST)) {
            operations = (ArrayList<Racecar.Operation>) getIntent().getSerializableExtra(ProtocolConstants.IntentParams.OPERATION_LIST);
        }
        adapter.setData(operations);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int viewSize = metrics.widthPixels  - 2 * getResources().getDimensionPixelSize(R.dimen.next_btn_margin_right)
                - 3 * getResources().getDimensionPixelSize(R.dimen.smaller_padding);
        float itemWidth = viewSize / 4;
        adapter.setItemWidth(itemWidth);
        adapter.notifyDataSetChanged();

        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_welfare;
    }
}
