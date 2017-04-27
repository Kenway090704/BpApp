package com.bemetoy.bp.ui.auth;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.bemetoy.bp.R;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.databinding.UiAccessLocationDialogBinding;
import com.bemetoy.bp.databinding.UiLocationConfirmDialogBinding;
import com.bemetoy.bp.databinding.UiRegisterLocationBinding;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.ui.LauncherUI;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.bp.uikit.BindingDialog;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.stub.app.AppCore;
import com.bemetoy.stub.model.LocationLogic;
import com.bemetoy.stub.network.NetSceneBase;
import com.bemetoy.stub.network.NetSceneResponseCallback;
import com.bemetoy.stub.util.RegionManager;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Tom on 2016/4/11.
 */
public class RegisterLocationUI extends BaseDataBindingActivity<UiRegisterLocationBinding> {

    private static final String TAG = "uikit.RegisterLocationUI";

    private String mProvince;
    private String mCity;
    private String mDistrict;
    private LocationClient mLocationClient;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private BDLocationListener bdLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        mBinding.setCity(mCity);
        mBinding.setProvince(mProvince);
        mBinding.setDistrict(mDistrict);
        mBinding.setDisplay(getString(R.string.click_to_choose_location));
        showAccessLocation();
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(
                InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "RegisterLocationUI destroyed.");
        release();
        super.onDestroy();
    }

    private void release() {
        if (mLocationClient != null) {
            if(bdLocationListener != null) {
                mLocationClient.unRegisterLocationListener(bdLocationListener);
                bdLocationListener = null;
            }
            mLocationClient.stop();
            mLocationClient = null;
        }

        if(!compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
        }
        RegionManager.getInstance().release();
    }



    @Override
    protected int getLayoutId() {
        return R.layout.ui_register_location;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LauncherUI.class));
        AppCore.getCore().getAccountManager().reset();
        release();
        super.onBackPressed();
    }

    @Override
    protected void init() {
        mProvince = getIntent().getStringExtra(ProtocolConstants.IntentParams.LOCATION_PROVINCE);
        mCity = getIntent().getStringExtra(ProtocolConstants.IntentParams.LOCATION_CITY);
        mDistrict = getIntent().getStringExtra(ProtocolConstants.IntentParams.LOCATION_DISTRICT);
        if (Util.isNullOrBlank(mDistrict)) {
            mDistrict = "";
        }

        RegionManager.getInstance().initRegion(new RegionManager.IOnRegionInitCallback() {

            @Override
            public void onRegionInited(Map<String, ArrayList<LinkedHashMap<String, ArrayList<String>>>> maps) {
                if (maps.size() > 0) {
                    mBinding.chooseLocationTv.setEnabled(true);
                }
            }
        });

        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterLocationUI.this, LauncherUI.class));
                AppCore.getCore().getAccountManager().reset();
                release();
                finish();
            }
        });
        //TODO 有重复点击的问题。
        mBinding.actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BindingDialog<UiLocationConfirmDialogBinding> dialog = new BindingDialog(RegisterLocationUI.this,
                        R.layout.ui_location_confirm_dialog);
                if (Util.isNullOrNil(mProvince) || Util.isNullOrNil(mCity)) {
                    Toast.makeText(RegisterLocationUI.this, "请选择注册地址", Toast.LENGTH_SHORT).show();
                } else {
                    String locationText = getResources().getString(R.string.address_confirm_notice, mProvince, mCity, mDistrict).trim();
                    if (locationText.endsWith("|")) {
                        locationText = locationText.substring(0, locationText.lastIndexOf('|'));
                    }
                    dialog.mBinding.locationTv.setText(locationText);
                    dialog.mBinding.agreeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(RegisterLocationUI.this, RegisterDetailUI.class);
                            intent.putExtra(ProtocolConstants.IntentParams.USER_LOCATION_PROVINCE, mProvince);
                            intent.putExtra(ProtocolConstants.IntentParams.USER_LOCATION_CITY, mCity);
                            intent.putExtra(ProtocolConstants.IntentParams.USER_LOCATION_DISTRICT, mDistrict);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });

                    dialog.mBinding.disagreeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            }
        });
        mBinding.locationIconBgIm.setVisibility(View.INVISIBLE);
        mBinding.locationIconIm.setImageResource(R.drawable.location_unavailable);
        mBinding.gpsLocationTv.setText(R.string.unavailable_location_notice);

        Subscription subscription1 = RxView.clicks(mBinding.gpsLocationTv).
                throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                updateUserLocation();
            }
        });

        Subscription subscription2 = RxView.clicks(mBinding.chooseLocationTv).
                throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                LocationLogic.showLocationDialog(RegisterLocationUI.this, new LocationLogic.OnGetLocation() {
                    @Override
                    public void onGetLocation(String province, String city, String district) {
                        mProvince = province;
                        mCity = city;
                        mDistrict = (district == null ? "" : district);
                        mBinding.setProvince(mProvince);
                        mBinding.setCity(mCity);
                        mBinding.setDistrict(mDistrict);
                        if (mBinding.gpsLocationTv.isEnabled()) {
                            mBinding.gpsLocationTv.setText(R.string.click_to_get_gps);
                            mBinding.gpsLocationTv.setTextColor(getResources().getColor(R.color.golden));
                            mBinding.locationIconIm.setImageResource(R.drawable.location_icon);
                        }

                    }
                }, mProvince, mCity, mDistrict);
            }
        });

        compositeSubscription.add(subscription1);
        compositeSubscription.add(subscription2);

        bdLocationListener = new BDLocationListener() {
            @Override
            public void onReceiveLocation(final BDLocation bdLocation) {
                if (bdLocation != null && bdLocation.getProvince() != null && bdLocation.getCity() != null) {
                    CheckRegisterNetScene checkAddress = new CheckRegisterNetScene(
                            bdLocation.getProvince(), bdLocation.getCity(),bdLocation.getDistrict(), new NetSceneResponseCallback<Racecar.CheckAddrResponse>() {
                        @Override
                        public void onResponse(IRequest request, Racecar.CheckAddrResponse result) {
                            if (result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                                mProvince = result.getProvince();
                                mCity = result.getCity();
                                mDistrict = result.getDistrict();
                                mBinding.gpsLocationTv.setTextColor(getResources().getColor(R.color.white));
                                mBinding.gpsLocationTv.setText(getString(R.string.setGPSLocation, mProvince, mCity, mDistrict));
                                // 如果第一次定位失败，第二次定位成功，那么这个时候需要重置图标
                                mBinding.locationIconIm.setImageResource(R.drawable.location_icon_success);
                                mBinding.chooseLocationTv.setText(R.string.click_to_choose_location);
                            } else {
                                onGPSFailed();
                            }
                        }

                        @Override
                        public void onLocalNetworkIssue(IRequest request) {
                            onGPSFailed();
                        }

                        @Override
                        public void onRequestFailed(IRequest request) {
                            super.onRequestFailed(request);
                            onGPSFailed();
                        }
                    });
                    checkAddress.doScene();
                } else {
                    onGPSFailed();
                }
                mBinding.locationIconBgIm.clearAnimation();
                mBinding.locationIconBgIm.setVisibility(View.INVISIBLE);
            }
        };

        mLocationClient = LocationLogic.getLocationClient(ApplicationContext.getApplication());
        mLocationClient.registerLocationListener(bdLocationListener);
    }

    private void updateUserLocation() {
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        operatingAnim.setFillAfter(true);
        mBinding.locationIconBgIm.startAnimation(operatingAnim);
        mBinding.locationIconIm.setImageResource(R.drawable.location_icon);
        mBinding.gpsLocationTv.setText(R.string.getting_location_now);
        mLocationClient.start();
    }

    private void onGPSFailed() {
        Log.e(TAG, "get user location fail.");
        mBinding.locationIconIm.setImageResource(R.drawable.location_unavailable);
        mBinding.gpsLocationTv.setText(R.string.unavailable_location_notice);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ProtocolConstants.PermissionCode.GPS:
                if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateUserLocation();
                } else {
                    mBinding.gpsLocationTv.setEnabled(false);
                }
                return;
        }
    }

    private void showAccessLocation() {
        final BindingDialog<UiAccessLocationDialogBinding> dialog = new BindingDialog(this, R.layout.ui_access_location_dialog);

        dialog.mBinding.agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (Util.isNullOrBlank(mProvince)) {
                    ActivityCompat.requestPermissions(RegisterLocationUI.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            ProtocolConstants.PermissionCode.GPS);
                }
            }
        });

        dialog.mBinding.disagreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mBinding.gpsLocationTv.setEnabled(false);
            }
        });

        dialog.show();
    }


    private static class CheckRegisterNetScene extends NetSceneBase {

        private String province;
        private String city;
        private String district;

        public CheckRegisterNetScene(String province, String city,  String district, ResponseCallBack callBack) {
            super(Racecar.CmdId.CHECK_ADDR_VALUE, callBack);
            if(province == null) {
                this.province = "";
            } else {
                this.province = province;
            }

            if(city == null) {
                this.city = "";
            } else {
                this.city = city;
            }

            if(district == null) {
                this.district = "";
            } else {
                this.district =  district;
            }
        }

        @Override
        public byte[] getRequestBody() {
            Racecar.CheckAddrRequest.Builder builder = Racecar.CheckAddrRequest.newBuilder();
            builder.setPrimaryReq(builderBaseRequest());
            builder.setProvince(province);
            builder.setCity(city);
            builder.setDistrict(district);
            return builder.build().toByteArray();
        }
    }

}
