package com.bemetoy.bp.plugin.personalcenter.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.bemetoy.bp.autogen.events.GPSLocationUpdateEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.personalcenter.R;
import com.bemetoy.bp.plugin.personalcenter.databinding.UiGamePerformanceBinding;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.model.LocationLogic;
import com.bemetoy.stub.ui.LoadingDialog;
import com.bemetoy.stub.ui.LoadingDialogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albieliang on 16/4/29.
 */
public class GamePerformanceUI extends BaseDataBindingActivity<UiGamePerformanceBinding> {

    private Racecar.AccountInfo mAccountInfo;
    private LocationClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();
    }

    @Override
    protected void onDestroy() {
        if(client != null) {
            client.stop();
            client = null;
        }
        super.onDestroy();
    }

    @Override
    protected void init() {

        mBinding.gamesListBts.setViewPager(mBinding.listVp);
        mBinding.listVp.setOffscreenPageLimit(3);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new WeeklyGameListFragment());
        fragmentList.add(new MonthlyGameListFragment());
        fragmentList.add(new PKGameListFragment());
        LoadingDialog dialog = new LoadingDialog(this);
        LoadingDialogManager.show(dialog, 3);

        mBinding.listVp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        mAccountInfo = AccountLogic.getAccountInfo();
        mBinding.setAccountInfo(mAccountInfo);

        ActivityCompat.requestPermissions(this ,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                ProtocolConstants.PermissionCode.GPS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ProtocolConstants.PermissionCode.GPS:
                if(permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    client = LocationLogic.getLocationClient(ApplicationContext.getApplication());
                    client.registerLocationListener(new BDLocationListener() {
                        @Override
                        public void onReceiveLocation(BDLocation bdLocation) {
                            LocationLogic.setHasLocationed(true);
                            GPSLocationUpdateEvent event = new GPSLocationUpdateEvent();
                            if(bdLocation.getLocType() != 61 && bdLocation.getLocType() != 161) {
                                event.isValid = false;
                            } else {
                                LocationLogic.setLastLocation(bdLocation.getLatitude(), bdLocation.getLongitude());
                                event.isValid = true;
                                event.latitude = bdLocation.getLatitude();
                                event.longitude = bdLocation.getLongitude();
                            }
                            RxEventBus.getBus().publish(event);
                        }
                    });
                    client.start();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // 只有在授权失败的情况下才提示用户
                        if(!this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            ToastUtil.show(R.string.gps_permission_reject);
                        }
                    }
                    LocationLogic.setHasLocationed(true);
                    GPSLocationUpdateEvent event = new GPSLocationUpdateEvent();
                    event.isValid = false;
                    RxEventBus.getBus().publish(event);
                }
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.ui_game_performance;
    }

}
