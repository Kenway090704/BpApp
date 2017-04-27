package com.bemetoy.bp.plugin.games.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.district.DistrictSearchOption;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bemetoy.bp.autogen.events.GPSLocationUpdateEvent;
import com.bemetoy.bp.autogen.events.GameStatusUpdateEvent;
import com.bemetoy.bp.autogen.events.LocationUpdateEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.games.R;
import com.bemetoy.bp.plugin.games.model.GameLogic;
import com.bemetoy.bp.plugin.games.network.NetSceneGetPlace;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.core.event.AccurateEventObserver;
import com.bemetoy.bp.sdk.core.event.EventObserver;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.model.GameAddress;
import com.bemetoy.stub.model.LocationLogic;
import com.bemetoy.stub.network.NetSceneResponseCallback;
import com.bemetoy.stub.ui.BPDialogManager;
import com.bemetoy.stub.util.BitmapUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Tom on 2016/4/21.
 */
@SuppressWarnings("unchecked")
public class GamesLocationFragment extends SupportMapFragment {

    private static final String TAG = "Games.GamesLocationFragment";

    private boolean showDialog = false;

    private LocationClient mLocationClient;
    private Racecar.AccountInfo mAccountInfo = AccountLogic.getAccountInfo();
    private boolean mIsFirstLoc;
    private BDLocationListener bdLocationListener;

    // the last gps location object.
    private BDLocation mLastGPSLocation;
    BitmapDescriptor gamePlayingIcon = BitmapUtil.getBitmapDescriptor(R.drawable.game_map_playing);
    ;
    BitmapDescriptor gameAddressIcon = BitmapUtil.getBitmapDescriptor(R.drawable.game_map_point);
    BitmapDescriptor gameFinishedIcon = BitmapUtil.getBitmapDescriptor(R.drawable.game_map_finished);
    BitmapDescriptor gameComingSoonIcon = BitmapUtil.getBitmapDescriptor(R.drawable.game_map_coming);

    private Runnable zoomMapsRunnable = null;

    // keep the map of the overlay in the map and the game address.
    private HashMap<Overlay, GameAddress> maps = new HashMap<>();
    private EventObserver mLocationObserver; // for the user change location. refresh the game list.
    private EventObserver gameStatusObserver;
    private DistrictSearch districtSearch = null;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = super.onCreateView(layoutInflater, viewGroup, bundle);
        init();
        Log.d(TAG, "current GamesLocationFragment %s invoke onCreateView()", this.toString());
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && showDialog) {
            showEmptyDialog();
        }
    }

    private void showEmptyDialog() {
        showDialog = !showDialog;
        if (activity == null) {
            return;
        }

        BPDialogManager.showNoticeDialog(activity, getString(R.string.sorry), getString(R.string.no_game_and_address_in_region));
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    private void init() {
        mLocationObserver = new AccurateEventObserver<LocationUpdateEvent>() {
            @Override
            public void onReceiveEvent(LocationUpdateEvent event) {
                updateMap(event.province, event.city, event.district, mLastGPSLocation);
            }
        };
        gameStatusObserver = new AccurateEventObserver<GameStatusUpdateEvent>() {
            @Override
            public void onReceiveEvent(GameStatusUpdateEvent event) {
                Log.d(TAG, "receive game status event");
                if (event.game == null) {
                    return;
                }

                // when the game status changed update the game address status.
                for (Map.Entry<Overlay, GameAddress> entry : maps.entrySet()) {
                    GameAddress address = entry.getValue();
                    if (address.getLastGameId() == event.game.getId()) {
                        BitmapDescriptor descriptor;
                        switch (event.game.getCurrentState()) {
                            case ProtocolConstants.GameStates.COMING_SOON:
                                descriptor = gameComingSoonIcon;
                                break;
                            case ProtocolConstants.GameStates.PLAYING:
                                descriptor = gamePlayingIcon;
                                break;
                            case ProtocolConstants.GameStates.FINISHED:
                                descriptor = gameFinishedIcon;
                                break;
                            default:
                                descriptor = gameAddressIcon;
                        }

                        Marker marker = (Marker) entry.getKey();
                        marker.setIcon(descriptor);

                    }
                }
            }
        };

        districtSearch = DistrictSearch.newInstance();
        RxEventBus.getBus().register(LocationUpdateEvent.ID, mLocationObserver);
        RxEventBus.getBus().register(GameStatusUpdateEvent.ID, gameStatusObserver);
        getMapView().getMap().setMyLocationEnabled(true);

        getMapView().getMap().setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Button view = (Button) LayoutInflater.from(getActivity()).inflate(R.layout.ui_distance_btn, getMapView(), false);
                Overlay overlay = marker;
                final GameAddress address = maps.get(overlay);
                if (GameLogic.getDistanceString(address.getDistance()).equals(ApplicationContext.getApplication().getResources().getString(R.string.get_location_fail))) {
                    view.setText(address.getName());
                } else {
                    view.setText(address.getName() + ", 离你的距离:" + GameLogic.getDistanceString(address.getDistance()));
                }

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (address.getLastGameId() == 0) {
                            Intent intent = new Intent(getActivity(), GameAddressUI.class);
                            intent.putExtra(ProtocolConstants.IntentParams.ADDRESS_INFO, address);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getActivity(), GameDetailUI.class);
                            intent.putExtra(ProtocolConstants.IntentParams.ADDRESS_INFO, address);
                            intent.putExtra(ProtocolConstants.IntentParams.GAME_ID, address.getLastGameId());
                            startActivity(intent);
                        }
                    }
                });

                InfoWindow mInfoWindow = new InfoWindow(view, marker.getPosition(), -47);
                getMapView().getMap().showInfoWindow(mInfoWindow);

                return false;
            }
        });

        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ProtocolConstants.PermissionCode.GPS);
    }

    /**
     * When get the gps permission invoke this method.
     */
    private void initLocationInfo() {
        mLocationClient = new LocationClient(ApplicationContext.getApplication());

        bdLocationListener = new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {

                if (bdLocation == null || getMapView() == null || getMapView().getMap() == null) {
                    return;
                }
                boolean isLocationValid = false;
                LocationLogic.setHasLocationed(true);
                GPSLocationUpdateEvent updateEvent = new GPSLocationUpdateEvent();
                if (bdLocation.getLocType() != 61 && bdLocation.getLocType() != 161) {
                    updateEvent.isValid = false;
                } else {
                    LocationLogic.setLastLocation(bdLocation.getLatitude(), bdLocation.getLongitude());
                    isLocationValid = true;
                    updateEvent.isValid = true;
                }
                updateEvent.latitude = bdLocation.getLatitude();
                updateEvent.longitude = bdLocation.getLongitude();
                RxEventBus.getBus().publish(updateEvent);

                mLastGPSLocation = bdLocation;
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(bdLocation.getRadius())
                        .direction(100).latitude(bdLocation.getLatitude())
                        .longitude(bdLocation.getLongitude()).build();
                getMapView().getMap().setMyLocationData(locData);
                if (!mIsFirstLoc) {
                    LatLng ll = new LatLng(bdLocation.getLatitude(),
                            bdLocation.getLongitude());
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(ll).zoom(14.0f);

                    getMapView().getMap().animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                    mIsFirstLoc = true;
                    if (isLocationValid) {
                        updateMap(mAccountInfo.getProvince(), mAccountInfo.getCity(), mAccountInfo.getDistrict(), bdLocation);
                    } else {
                        updateMap(mAccountInfo.getProvince(), mAccountInfo.getCity(), mAccountInfo.getDistrict(), null);
                    }

                }
                if (mLocationClient != null) {
                    mLocationClient.stop();
                }
            }
        };
        mLocationClient.registerLocationListener(bdLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(false); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000 * 60);
        option.setAddrType("all");
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        Log.d(TAG, "start get the user location info.");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ProtocolConstants.PermissionCode.GPS:
                initLocationInfo();
                if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // 只有在授权失败的情况下才提示用户
                        if (!getActivity().shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            ToastUtil.show(R.string.gps_permission_reject);
                        }
                    }
                }
                break;
        }
    }

    /**
     * Get the nearest top 10 place to the gps location in the city, province.
     *
     * @param province
     * @param city
     * @param district
     * @param bdLocation the gps location
     */
    private void updateMap(String province, String city, String district, final BDLocation bdLocation) {
        NetSceneGetPlace netSceneGetPlace = null;
        if (bdLocation == null) {
            netSceneGetPlace = new NetSceneGetPlace(province,
                    city, district, new GetPlaceCallback(-1, -1));
        } else {
            netSceneGetPlace = new NetSceneGetPlace(province,
                    city, district, new GetPlaceCallback(bdLocation.getLatitude(), bdLocation.getLongitude()));
        }
        netSceneGetPlace.doScene();
    }

    /**
     * Zoom the map to show all the overlay
     *
     * @param overlays
     */
    private void zoomToSpan(Set<Overlay> overlays) {
        if (overlays.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Overlay overlay : overlays) {
                // polyline 中的点可能太多，只按marker 缩放
                if (overlay instanceof Marker && ((Marker) overlay).getPosition() != null) {
                    builder.include(((Marker) overlay).getPosition());
                }
            }

            final BaiduMap map = getMapView().getMap();
            final MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build());

            if (map != null) {
                try {
                    zoomMapsRunnable = (new Runnable() {
                        @Override
                        public void run() {
                            map.setMapStatus(mapStatusUpdate);
                        }
                    });
                    ThreadPool.postToMainThread(zoomMapsRunnable, 500);
                } catch (Exception e) {
                    Log.e(TAG, "game placezoomToSpan exception %s", e.getMessage());
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "current GamesLocationFragment %s invoke onDestroyView()", this.toString());
        getMapView().getMap().setMyLocationEnabled(false);
        getMapView().getMap().clear();
        maps.clear();
        maps = null;
        RxEventBus.getBus().unregister(LocationUpdateEvent.ID, mLocationObserver);
        RxEventBus.getBus().unregister(GameStatusUpdateEvent.ID, gameStatusObserver);
        districtSearch.destroy();
        districtSearch = null;
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(bdLocationListener);
            mLocationClient.stop();
            mLocationClient = null;
        }
        ThreadPool.removeOnMainThread(zoomMapsRunnable);
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    private final class GetPlaceCallback extends NetSceneResponseCallback<Racecar.GetPlaceResponse> {

        private double latitude;
        private double longitude;

        public GetPlaceCallback(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onResponse(IRequest request, Racecar.GetPlaceResponse result) {
            Log.d(TAG, "the place size is %d", result.getPlaceCount());

            // if there no game address here, then zoom the map to display the district.
            if (result.getPlaceCount() == 0) {

                if (!(request instanceof NetSceneGetPlace)) {
                    Log.e(TAG, "instance type error.");
                    return;
                }

                showDialog = true;
                final NetSceneGetPlace getPlace = (NetSceneGetPlace) request;
                String city = getPlace.getCity();
                String district = getPlace.getDistrict();

                DistrictSearchOption districtSearchOption = new DistrictSearchOption();
                districtSearchOption.cityName(city).districtName(district);
                if (districtSearch != null) {
                    districtSearch.setOnDistrictSearchListener(new DistrictSearchCallback());
                    districtSearch.searchDistrict(districtSearchOption);
                    if (getUserVisibleHint()) {
                        showEmptyDialog();
                    }
                }
                return;
            }

            showDialog = false;
            final List<GameAddress> gameAddressList = new ArrayList();
            for (Racecar.Place place : result.getPlaceList()) {
                GameAddress gameAddress = new GameAddress(place);
                if (latitude != -1 && longitude != -1) {
                    gameAddress.setDistance((int) DistanceUtil.getDistance(new LatLng(gameAddress.getLatitude(), gameAddress.getLongitude())
                            , new LatLng(latitude, longitude)));
                } else {
                    gameAddress.setDistance(ProtocolConstants.ERROR_DISTANCE);
                }
                gameAddressList.add(gameAddress);
            }
            final double center[] = new double[]{latitude, longitude};

            List<GameAddress> top10 = GameLogic.getNearest10(gameAddressList, center);
            if (top10 == null) {
                return;
            }

            if (maps == null) {
                return;
            }

            maps.clear();
            getMapView().getMap().clear();
            for (GameAddress place : top10) {
                LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());

                BitmapDescriptor bitmapDescriptor = null;
                switch (place.getGamesState()) {
                    case ProtocolConstants.GameStates.COMING_SOON:
                        bitmapDescriptor = gameComingSoonIcon;
                        break;
                    case ProtocolConstants.GameStates.PLAYING:
                        bitmapDescriptor = gamePlayingIcon;
                        break;
                    case ProtocolConstants.GameStates.FINISHED:
                        bitmapDescriptor = gameFinishedIcon;
                        break;
                    default:
                        bitmapDescriptor = gameAddressIcon;
                }

                MarkerOptions ooA = new MarkerOptions().position(latLng).icon(bitmapDescriptor)
                        .zIndex(9).draggable(false);
                maps.put(getMapView().getMap().addOverlay(ooA), place);
            }
            try {
                zoomToSpan(maps.keySet());
            } catch (Exception e) {
                Log.e(TAG, "district search zoomToSpan exception: %s", e.getMessage());
            }

        }
    }

    private final class DistrictSearchCallback implements OnGetDistricSearchResultListener {

        @Override
        public void onGetDistrictResult(DistrictResult districtResult) {
            final BaiduMap map = getMapView().getMap();
            if (map == null) {
                Log.e(TAG, "map is null");
                return;
            }
            map.clear();
            if (districtResult.error == SearchResult.ERRORNO.NO_ERROR) {
                List<List<LatLng>> polyLines = districtResult.getPolylines();
                if (polyLines == null) {
                    return;
                }
                final LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (List<LatLng> polyline : polyLines) {
                    OverlayOptions ooPolyline11 = new PolylineOptions().width(10)
                            .points(polyline).dottedLine(true).color(Color.BLUE);
                    map.addOverlay(ooPolyline11);
                    OverlayOptions ooPolygon = new PolygonOptions().points(polyline)
                            .fillColor(0xAAFFFF00);
                    map.addOverlay(ooPolygon);
                    for (LatLng latLng : polyline) {
                        builder.include(latLng);
                    }
                }
                try {
                    MapStatusUpdate status = MapStatusUpdateFactory
                            .newLatLngBounds(builder.build());
                    map.setMapStatus(status);
                } catch (Exception e) {
                    Log.e(TAG, "map.setMapStatus exception : %s", e.getMessage());
                }
            }
        }
    }
}
