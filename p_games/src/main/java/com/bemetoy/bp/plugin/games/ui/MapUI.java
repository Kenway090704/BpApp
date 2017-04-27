package com.bemetoy.bp.plugin.games.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bemetoy.bp.plugin.games.R;
import com.bemetoy.bp.plugin.games.model.GameLogic;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.BpActivity;
import com.bemetoy.stub.model.GameAddress;
import com.bemetoy.stub.model.LocationLogic;
import com.bemetoy.stub.util.BitmapUtil;

/**
 * Created by Tom on 2016/5/14.
 */
public class MapUI extends BpActivity {

    private static final String TAG = "Games.MapUI";

    private ImageButton backBtn;
    private MapView mapView;
    private GameAddress address;


    private BitmapDescriptor gamePlayingIcon =
            BitmapUtil.getBitmapDescriptor(R.drawable.game_map_playing);

    @Override
    protected int getLayoutId() {
        return R.layout.ui_map;
    }

    @Override
    protected void initView() {
        backBtn = (ImageButton) this.findViewById(R.id.back_btn);
        mapView = (MapView) this.findViewById(R.id.map);
        address = getIntent().getParcelableExtra(ProtocolConstants.IntentParams.ADDRESS_INFO);

        final double latitude = getIntent().getDoubleExtra(ProtocolConstants.IntentParams.ADDRESS_LATITUDE, 0d);
        final double longitude = getIntent().getDoubleExtra(ProtocolConstants.IntentParams.ADDRESS_LONGITUDE, 0d);
        Log.i(TAG, "target location latitude is %s and longitude is %s", latitude, longitude);
        final LatLng point = new LatLng(latitude, longitude);

        MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(18).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mapView.getMap().setMapStatus(mMapStatusUpdate);

        //添加Overlay
        MarkerOptions ooA = new MarkerOptions().position(point).icon(gamePlayingIcon)
                .zIndex(9).draggable(false);
        ooA.animateType(MarkerOptions.MarkerAnimateType.grow);
        mapView.getMap().addOverlay(ooA);

        //设置点击事件
        mapView.getMap().setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Button view = (Button) LayoutInflater.from(MapUI.this).inflate(R.layout.ui_distance_btn, mapView, false);
                long d = ProtocolConstants.DEFAULT_DISTANCE;
                if(LocationLogic.getLastLocation() != null) {
                    d  = (long)DistanceUtil.getDistance(new LatLng(LocationLogic.getLastLocation().mLastLatitude,
                            LocationLogic.getLastLocation().mLastLongitude) ,point);
                } else {
                    d = ProtocolConstants.ERROR_DISTANCE;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("赛点").append(address.getName());
                if(d != ProtocolConstants.ERROR_DISTANCE) {
                    sb.append(", 离你的距离:").append(GameLogic.getDistanceString(d));
                }
                view.setText(sb.toString());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ProtocolConstants.IntentParams.ADDRESS_INFO, address);
                        PluginStubBus.doAction(MapUI.this, PluginConstants.Plugin.PLUGIN_NAME_P_GAMES,
                                PluginConstants.Games.Action.CMD_START_ADDRESS_DETAIL, 0, bundle);
                    }
                });

                InfoWindow mInfoWindow = new InfoWindow(view, marker.getPosition(), -47);
                mapView.getMap().showInfoWindow(mInfoWindow);
                return false;
            }
        });
    }

    @Override
    protected void initListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
