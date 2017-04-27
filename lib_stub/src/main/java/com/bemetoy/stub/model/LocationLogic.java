package com.bemetoy.stub.model;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.stub.R;
import com.bemetoy.bp.uikit.BpBottomAlertDialog;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.bp.uikit.widget.kankan.wheel.OnWheelChangedListener;
import com.bemetoy.bp.uikit.widget.kankan.wheel.WheelView;
import com.bemetoy.stub.app.DistrictTextAdapter;
import com.bemetoy.stub.util.RegionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Tom on 2016/5/18.
 */
public class LocationLogic {

    private static final String TAG = "APP.LocationLogic";


    private static Point lastLocationPoint;
    private static boolean hasLocationed = false;

    public static void setLastLocation(double lastLatitude, double lastLongitude) {
        lastLocationPoint = new Point(lastLatitude, lastLongitude);
    }

    public static void setHasLocationed(boolean locationed) {
        hasLocationed = locationed;
    }

    public static boolean isHasLocationed() {
        return hasLocationed;
    }

    public static Point getLastLocation() {
        return lastLocationPoint;
    }

    public  interface OnGetLocation {
        void onGetLocation(String province, String city, String district);
    }

    public static void showLocationDialog(Context context, final OnGetLocation onGetLocation, String province, String city, String district) {
        final BpBottomAlertDialog dialog = new BpBottomAlertDialog(context);
        dialog.setContentView(R.layout.ui_location_list_dialog);
        final WheelView provinceWv = (WheelView) dialog.findViewById(R.id.province_wv);
        final WheelView cityWv = (WheelView) dialog.findViewById(R.id.city_wv);
        final WheelView districtWv = (WheelView) dialog.findViewById(R.id.district_wv);
        final Button cancelBtn = (Button) dialog.findViewById(R.id.cancel_btn);
        final Button confirmBtn = (Button) dialog.findViewById(R.id.confirm_btn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        provinceWv.setWheelForeground(R.drawable.location_choose);
        cityWv.setWheelForeground(R.drawable.location_choose);
        districtWv.setWheelForeground(R.drawable.location_choose);

        final DistrictTextAdapter provinceAdapter = new DistrictTextAdapter(context, R.layout.ui_location_item);
        final DistrictTextAdapter cityAdapter = new DistrictTextAdapter(context, R.layout.ui_location_item);
        final DistrictTextAdapter districtTextAdapter = new DistrictTextAdapter(context, R.layout.ui_location_item);

        final List<String> provinces = new ArrayList<>(RegionManager.getInstance().getRegionMap().keySet());

        if(provinces.size() == 0) {
            Log.e(TAG, "region data load failed.");
            ToastUtil.show(R.string.region_data_load_failed);
            return;
        }

        provinceAdapter.setData(provinces);
        int provinceIndex = Util.isNullOrBlank(province) ? 0 : provinces.indexOf(province);
        if(provinceIndex < 0) {
            provinceIndex = 0;
        }
        final ArrayList<LinkedHashMap<String, ArrayList<String>>> cities = new ArrayList<>(RegionManager.getInstance().getRegionMap().values()).get(provinceIndex);

        final List<String> citiesString = new ArrayList<>();
        for (LinkedHashMap<String, ArrayList<String>> hashMaps : cities) {
            citiesString.addAll(hashMaps.keySet());
        }

        int cityIndex = Util.isNullOrBlank(city) ? 0 : citiesString.indexOf(city);
        cityAdapter.setData(citiesString);
        if(cityIndex < 0) {
            cityIndex = 0;
        }

        if(cities.get(cityIndex).get(citiesString.get(cityIndex)).size() == 0) {
            cities.get(cityIndex).get(citiesString.get(cityIndex)).add("");
        }

        List<String> districtData = cities.get(cityIndex).get(citiesString.get(cityIndex));
        districtTextAdapter.setData(districtData);

        cityWv.setViewAdapter(cityAdapter);
        provinceWv.setViewAdapter(provinceAdapter);
        districtWv.setViewAdapter(districtTextAdapter);

        provinceWv.setCurrentItem(provinceIndex);
        cityWv.setCurrentItem(cityIndex);
        int districtIndex = Util.isNullOrBlank(district) ? 0 : districtData.indexOf(district);
        if(districtIndex < 0) {
            districtIndex = 0;
        }
        districtWv.setCurrentItem(districtIndex);

        provinceWv.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                ArrayList<LinkedHashMap<String, ArrayList<String>>> cities = new ArrayList<>(RegionManager.getInstance().getRegionMap().values()).get(newValue);
                List<String> citiesString = new ArrayList<>();
                for (LinkedHashMap<String, ArrayList<String>> hashMaps : cities) {
                    citiesString.addAll(hashMaps.keySet());
                }
                cityAdapter.setData(citiesString);
                districtTextAdapter.setData(cities.get(0).get(citiesString.get(0)));
                cityWv.setCurrentItem(0);
                districtWv.setCurrentItem(0);
                cityWv.invalidateWheel(true);
                districtWv.invalidateWheel(true);
            }
        });

        cityWv.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String province = provinceAdapter.getItemText(provinceWv.getCurrentItem()).toString();
                List<String> contities = RegionManager.getInstance().getRegionMap().get(province).get(newValue).get(cityAdapter.getItemText(newValue).toString());
                districtTextAdapter.setData(contities);
                districtWv.setCurrentItem(0);
                districtWv.invalidateWheel(true);
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(onGetLocation != null) {
                    String province = provinceAdapter.getItemText(provinceWv.getCurrentItem()).toString();
                    String city = cityAdapter.getItemText(cityWv.getCurrentItem()).toString();
                    String district = null;
                    if(districtTextAdapter.getItemText(districtWv.getCurrentItem()) != null) {
                        district = districtTextAdapter.getItemText(districtWv.getCurrentItem()).toString();
                    }
                    onGetLocation.onGetLocation(province, city, district);
                }
            }
        });

        dialog.setCancelable(true);
        dialog.show();
    }

    /**
     * Generate the location client.
     * @param context
     * @return
     */
    public static LocationClient getLocationClient(final Context context) {
        if(context == null) {
            Log.e(TAG, "context should not be null");
            return null;
        }

        LocationClient client = new LocationClient(context);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000 * 60);
        option.setIsNeedAddress(true);
        option.setAddrType("all");
        option.setEnableSimulateGps(true);
        client.setLocOption(option);
        return client;
    }


    public static void mockGPS(Activity activity) {
        double longitude = 113.458503;
        double latitude = 30.368433;
        LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        manager.addTestProvider(LocationManager.GPS_PROVIDER,
                "requiresNetwork" == "", "requiresSatellite" == "",
                "requiresCell" == "", "hasMonetaryCost" == "",
                "supportsAltitude" == "", "supportsSpeed" == "",
                "supportsBearing" == "supportsBearing",
                Criteria.POWER_LOW,
                Criteria.ACCURACY_FINE);
        manager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);

        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setTime(System.currentTimeMillis());
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAltitude(2.0f);
        location.setAccuracy(Criteria.ACCURACY_FINE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        manager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location);

    }


    public static class Point {
        public double mLastLongitude;
        public double mLastLatitude;

        public Point(double lastLatitude, double longitude) {
            this.mLastLongitude = longitude;
            this.mLastLatitude = lastLatitude;
        }
    }



}
