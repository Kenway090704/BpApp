package com.bemetoy.bp.plugin.ranking.ui;

import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bemetoy.bp.autogen.events.GoToMyRankEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.persistence.fs.CfgFs;
import com.bemetoy.bp.plugin.ranking.R;
import com.bemetoy.bp.plugin.ranking.databinding.UiRankingBinding;
import com.bemetoy.bp.plugin.ranking.databinding.UiRankingRuleBinding;
import com.bemetoy.bp.plugin.ranking.network.NetSceneRankingRule;
import com.bemetoy.bp.plugin.ranking.ui.adapter.RankingListFragmentAdapter;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.uikit.BpDialog;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.network.NetSceneResponseCallback;
import com.bemetoy.stub.ui.AccountChangeActivity;
import com.bemetoy.stub.ui.LoadingDialog;
import com.bemetoy.stub.ui.LoadingDialogManager;
import com.bemetoy.stub.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 2016/4/18.
 */
public class RankingUI extends AccountChangeActivity<UiRankingBinding> {

    private static final String TAG = "ranking.RankingUI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onAccountUpdate(Racecar.AccountInfo accountInfo) {
        mBinding.setAccountInfo(accountInfo);
    }

    @Override
    protected void init() {
        mBinding.setAccountInfo(AccountLogic.getAccountInfo());
        mBinding.rankingListBts.setViewPager(mBinding.rankingListVp);
        List<AbstractRankingFragment> fragments = new ArrayList<>();

        final AbstractRankingFragment stateRankingListFragment = new NationalRankingListFragment();
        final AbstractRankingFragment provinceRankingListFragment = new ProvinceRankingListFragment();
        final AbstractRankingFragment citykingListFragment = new CityRankingListFragment();
        final WeekRankinListFragment weeklyRankingListFragment = new WeekRankinListFragment();

        fragments.add(stateRankingListFragment);
        fragments.add(provinceRankingListFragment);
        fragments.add(citykingListFragment);
        fragments.add(weeklyRankingListFragment);

        final RankingListFragmentAdapter rankingListFragmentAdapter = new RankingListFragmentAdapter(this.getSupportFragmentManager(), fragments);
        mBinding.rankingListVp.setOffscreenPageLimit(rankingListFragmentAdapter.getCount());
        mBinding.rankingListVp.setAdapter(rankingListFragmentAdapter);

        mBinding.myRankingTv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!AppUtil.checkNetworkFirst(RankingUI.this)) {
                    return;
                }
                GoToMyRankEvent event = new GoToMyRankEvent();
                event.isMyRanking = isChecked;
                RxEventBus.getBus().publish(event);
                LoadingDialog loadingDialog = new LoadingDialog(RankingUI.this);
                LoadingDialogManager.show(loadingDialog, 4);
            }
        });
        mBinding.actionBtn.setVisibility(View.INVISIBLE);// TODO 为13号上线做修改
        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final RankingRuleClickListener clickListener = new RankingRuleClickListener();

        CfgFs cfgFs = new CfgFs(StorageConstants.COMM_SETTING_PATH);
        Object resultList = cfgFs.get(StorageConstants.SETTING_KEY.RANK_RULE);
        List<Racecar.RankRule> rules = null;
        if(resultList != null && resultList instanceof List) {
            rules = (List<Racecar.RankRule>)resultList;
        }

        if(rules == null || rules.isEmpty()) {
             NetSceneRankingRule rankingRule = new NetSceneRankingRule(new NetSceneResponseCallback<Racecar.GetRankRuleResponse>() {
                 @Override
                 public void onRequestFailed(IRequest request) {
                    //do nothing
                 }

                 @Override
                public void onResponse(IRequest request, final Racecar.GetRankRuleResponse result) {
                     mBinding.actionBtn.setEnabled(result.getRankRuleCount() > 0);
                     clickListener.setContent(result.getRankRuleList());
                     mBinding.actionBtn.setOnClickListener(clickListener);
                    ThreadPool.post(new Runnable() {
                        @Override
                        public void run() {
                            CfgFs cfgFs = new CfgFs(StorageConstants.COMM_SETTING_PATH);
                            cfgFs.set(StorageConstants.SETTING_KEY.RANK_RULE, result.getRankRuleList());
                        }
                    });
                }
            });
            rankingRule.doScene();
        } else {
            clickListener.setContent(rules);
            mBinding.actionBtn.setOnClickListener(clickListener);
        }
        LoadingDialog loadingDialog = new LoadingDialog(this);
        LoadingDialogManager.show(loadingDialog, 4);
    }


    @Override
    public int getLayoutId() {
        return R.layout.ui_ranking;
    }

    private class RankingRuleClickListener implements View.OnClickListener {

        private List<Racecar.RankRule> data;

        public void setContent(List<Racecar.RankRule> content) {
            this.data = content;
        }

        @Override
        public void onClick(View v) {
            final BpDialog<UiRankingRuleBinding> dialog = new BpDialog(RankingUI.this, R.layout.ui_ranking_rule);
            dialog.setCancelable(true);
            LayoutInflater inflater = LayoutInflater.from(RankingUI.this);
            dialog.mBinding.closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.setCancelable(true);
            for(Racecar.RankRule rule : data) {
                TextView titleView = (TextView) inflater.inflate(R.layout.ui_ranking_rule_title, dialog.mBinding.contentView, false);
                titleView.setText(rule.getTitle());
                dialog.mBinding.contentView.addView(titleView);

                TextView txtView = (TextView) inflater.inflate(R.layout.ui_ranking_rule_txt, dialog.mBinding.contentView, false);
                txtView.setText(rule.getText().trim());
                dialog.mBinding.contentView.addView(txtView);
            }

            ViewGroup.LayoutParams params = dialog.mBinding.contentSv.getLayoutParams();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            params.height = (int) (displayMetrics.heightPixels * 0.65);
            dialog.mBinding.contentSv.setLayoutParams(params);

            dialog.show();
        }
    }
}
