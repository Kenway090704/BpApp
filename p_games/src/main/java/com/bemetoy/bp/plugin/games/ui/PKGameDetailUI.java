package com.bemetoy.bp.plugin.games.ui;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.games.R;
import com.bemetoy.bp.plugin.games.databinding.UiPkGameDetailBinding;
import com.bemetoy.bp.plugin.games.network.NetSceneGetGameResult;
import com.bemetoy.bp.plugin.games.ui.adapter.GameResultAdapter;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.bp.uikit.widget.recyclerview.DividerItemDecoration;
import com.bemetoy.bp.uikit.widget.recyclerview.LoadMoreRecyclerView;
import com.bemetoy.stub.model.GameInfo;
import com.bemetoy.stub.network.NetSceneResponseCallback;

import java.util.Date;

/**
 * Created by Tom on 2016/7/14.
 */
public class PKGameDetailUI extends BaseDataBindingActivity<UiPkGameDetailBinding> {

    private int mOffset;
    private GameResultAdapter resultAdapter;
    private int gameId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        GameInfo mGameInfo = getIntent().getExtras().getParcelable(ProtocolConstants.IntentParams.GAME_INFO);
        resultAdapter = new GameResultAdapter(this, true);
        mBinding.setGame(mGameInfo);
        Date date = Util.getDate("yyyy-MM-dd hh:mm:ss", mGameInfo.getBeginDate());
        mBinding.gameTimeTv.setText(Util.getDateFormat("MM月dd日 hh:ss",date.getTime()));
        mBinding.gameResultVr.setAdapter(resultAdapter);
        gameId = mGameInfo.getId();
        mBinding.gameResultVr.setOnLoadingStateChangedListener(new LoadMoreRecyclerView.IOnLoadingStateChangedListener() {
            @Override
            public void onLoadMore(LoadMoreRecyclerView parent, RecyclerView.Adapter adapter, boolean isForward) {
                mBinding.gameResultVr.enableScroll(false);
                mBinding.gameResultVr.hideLoadingView(false);
                loadGameResult();
            }
        });
        mBinding.gameResultVr.hideLoadingView(false);
        mBinding.gameResultVr.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST, R.drawable.divider));
        mBinding.gameResultVr.setFooterLoadingView(R.layout.ui_loading_more);
        mBinding.gameResultVr.showLoading(true);
        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loadGameResult();
    }

    private void loadGameResult() {
        NetSceneGetGameResult netSceneGetGameResult = new NetSceneGetGameResult(gameId, mOffset,
                new NetSceneResponseCallback<Racecar.GetGameResultResponse>() {
                    @Override
                    public void onResponse(IRequest request, Racecar.GetGameResultResponse result) {
                        if (result != null && result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                            if (result.getResultCount() == 0) {
                                return;
                            }
                            resultAdapter.appendData(result.getResultList());
                            mOffset += result.getResultCount();
                            resultAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onNetSceneEnd() {
                        mBinding.gameResultVr.enableScroll(true);
                        mBinding.gameResultVr.hideLoadingView(true);
                    }

                });
        netSceneGetGameResult.doScene();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_pk_game_detail;
    }
}
