package com.bemetoy.bp.plugin.games.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.view.View;

import com.bemetoy.bp.autogen.events.GameNoticeUpdateEvent;
import com.bemetoy.bp.autogen.events.LocationUpdateEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.games.R;
import com.bemetoy.bp.plugin.games.databinding.UiGamesBinding;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.core.base.IPluginStub;
import com.bemetoy.bp.sdk.core.event.AccurateEventObserver;
import com.bemetoy.bp.sdk.core.event.EventObserver;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.app.DefaultFragmentAdapter;
import com.bemetoy.stub.ui.AccountChangeActivity;
import com.bemetoy.stub.util.RegionManager;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Tom on 2016/4/21.
 */
public class GamesUI extends AccountChangeActivity<UiGamesBinding> {

    private static final String TAG = "Games.GamesUI";

    private Racecar.AccountInfo mAccountInfo;
    private EventObserver eventObserver;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private String province;
    private String city;
    private String district;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        refresh();
        province = mAccountInfo.getProvince();
        city = mAccountInfo.getCity();
        district = mAccountInfo.getDistrict();

        eventObserver = new AccurateEventObserver<GameNoticeUpdateEvent>() {
            @Override
            public void onReceiveEvent(GameNoticeUpdateEvent event) {
                String notice = event.gameNotice.trim();
                if(Util.isNullOrBlank(notice)) {
                    notice = "欢迎来到零速争霸";
                }

                if(!notice.equals(mBinding.gamesNoticeTv.getText().toString().trim())) {
                    TextPaint textPaint = mBinding.gamesNoticeTv.getPaint();
                    float txtLength = textPaint.measureText(notice);
                    String text = notice;
                    if( txtLength < mBinding.gamesNoticeTv.getMeasuredWidth()) {
                        float lengthEmpty = mBinding.gamesNoticeTv.getMeasuredWidth() - txtLength;
                        float singleEmpty = mBinding.gamesNoticeTv.getPaint().measureText(" ");
                        int emptyCount = (int)(lengthEmpty / singleEmpty + 1);
                        StringBuilder stringBuilder = new StringBuilder(notice);
                        for(int i = 0;  i < emptyCount; i++) {
                            if(i % 2 == 0) {
                                stringBuilder.insert(0," ");
                            } else {
                                stringBuilder.append(" ");
                            }
                        }
                        text = stringBuilder.toString();
                    }
                    mBinding.gamesNoticeTv.setText(text);
                }
            }
        };

        RxEventBus.getBus().register(GameNoticeUpdateEvent.ID ,eventObserver);

        mBinding.gameListVp.setOffscreenPageLimit(3);
        mBinding.gamesListBts.setViewPager(mBinding.gameListVp);
        mBinding.gameListVp.setPagingEnabled(false);

        final List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new GameListFragment());
        fragmentList.add(new GamesLocationFragment());
        fragmentList.add(new AccountGameFragment());

        mBinding.gameListVp.setAdapter(new DefaultFragmentAdapter(this.getSupportFragmentManager(),
                fragmentList));

        String locationTxt = city;
        if(!Util.isNullOrNil(district)) {
            locationTxt = (locationTxt + "/" + district);
        }
        
        mBinding.cityBtn.setText(locationTxt);

        RegionManager.getInstance().initRegion(new RegionManager.IOnRegionInitCallback() {
            @Override
            public void onRegionInited(Map<String, ArrayList<LinkedHashMap<String, ArrayList<String>>>> maps) {
                if(maps.size() > 0) {
                    mBinding.cityBtn.setEnabled(true);
                }
            }
        });

        compositeSubscription.add(RxView.clicks(mBinding.cityBtn).throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Bundle bundle = new Bundle();
                bundle.putString(ProtocolConstants.IntentParams.LOCATION_PROVINCE, province);
                bundle.putString(ProtocolConstants.IntentParams.LOCATION_CITY, city);
                bundle.putString(ProtocolConstants.IntentParams.LOCATION_DISTRICT, district);

                PluginStubBus.doActionForResult(GamesUI.this, PluginConstants.Plugin.PLUGIN_NAME_APP,
                        PluginConstants.App.Action.CMD_SHOW_LOCATION_DIALOG, bundle, 0, new IPluginStub.OnActionResult(){
                            @Override
                            public void onActionResult(Context context, int cmd, int resultCode, Bundle data) {
                                updateGameList(data);
                            }
                        });
            }
        }));

        mBinding.headerBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBinding.headerBar.avatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginStubBus.doAction(GamesUI.this,
                        PluginConstants.Plugin.PLUGIN_NAME_P_PERSONAL_CENTER,
                        PluginConstants.PersonalCenter.Action.CMD_START_PERSONAL_CENTER_UI, 0, null);
            }
        });

    }

    private void refresh() {
        mAccountInfo = AccountLogic.getAccountInfo();
        mBinding.setUserInfo(mAccountInfo);
    }

    private void updateGameList(Bundle bundle) {
        province = bundle.getString(ProtocolConstants.IntentParams.LOCATION_PROVINCE);
        city = bundle.getString(ProtocolConstants.IntentParams.LOCATION_CITY);
        district = bundle.getString(ProtocolConstants.IntentParams.LOCATION_DISTRICT);

        String locationTxt = Util.isNullOrNil(district) ? city : city + "/" + district;
        mBinding.cityBtn.setText(locationTxt);

        LocationUpdateEvent event = new LocationUpdateEvent();
        event.province = province;
        event.city = city;
        event.district = district;
        RxEventBus.getBus().publish(event);
    }

    @Override
    protected void onDestroy() {
        RxEventBus.getBus().unregister(GameNoticeUpdateEvent.ID ,eventObserver);
        RegionManager.getInstance().release();
        compositeSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void onAccountUpdate(Racecar.AccountInfo accountInfo) {
        mAccountInfo = accountInfo;
        mBinding.setUserInfo(accountInfo);
        String locationTxt = city;
        if(!Util.isNullOrNil(district)) {
            locationTxt = (locationTxt + "/" + district);
        }
        mBinding.cityBtn.setText(locationTxt);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_games;
    }



}
