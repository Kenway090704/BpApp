package com.bemetoy.stub.util;

import android.content.Context;
import android.content.Intent;
import android.util.Xml;

import com.bemetoy.bp.autogen.events.DownLoadFinishEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.bp.persistence.fs.CfgFs;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.arithmetic.MD5;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.core.event.AccurateEventObserver;
import com.bemetoy.bp.sdk.core.event.EventObserver;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.FileUtils;
import com.bemetoy.stub.network.NetSceneBase;
import com.bemetoy.stub.network.NetSceneResponseCallback;
import com.bemetoy.stub.service.DownloadService;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tom on 2016/4/5.
 */
public class RegionManager {

    private static final int INIT_REGION_VERSION = 2;

    private static final String TAG = "UI.RegionManager";
    private static final RegionManager manager = new RegionManager();
    private static String lastDownloadMD5 = null;
    private static int lastVersion = INIT_REGION_VERSION;
    private static String lastRegionUrl = null;
    private EventObserver eventObserver;

    private static Map<String, ArrayList<LinkedHashMap<String, ArrayList<String>>>> regionMap = new LinkedHashMap();

    private RegionManager() {
        eventObserver = new AccurateEventObserver<DownLoadFinishEvent>() {
            @Override
            public void onReceiveEvent(DownLoadFinishEvent event) {
                if (event.targetUrl != null && event.targetUrl.equals(lastRegionUrl)) {
                    String fileMD5 = MD5.getMD5(event.localPath);
                    if (fileMD5 == null || !fileMD5.equals(lastDownloadMD5)) {
                        return;
                    }

                    updateVersion(lastVersion);

                    if (regionMap.size() > 0) {
                        initRegion(null);
                    }
                }
            }
        };
        //因为RegionManager 是单例的，在整个应用的生命周期内一直存在，所以这里注册事件之后不取消订阅也没有问题
        RxEventBus.getBus().register(DownLoadFinishEvent.ID, eventObserver);
        lastVersion = getVersion();
    }

    public static RegionManager getInstance() {
        return manager;
    }

    public interface IOnRegionInitCallback {
        void onRegionInited(Map<String, ArrayList<LinkedHashMap<String, ArrayList<String>>>> maps);
    }

    public void initRegion(final IOnRegionInitCallback callback) {
        if (regionMap != null && regionMap.size() > 0) {
            Log.d(TAG, "has init region map, size = %d", regionMap.size());
            if (callback != null) {
                callback.onRegionInited(regionMap);
            }
            return;
        }

        ThreadPool.post(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                Log.d(TAG, "Thread name is %s, start time is %d", Thread.currentThread().getName(), startTime);
                final XmlPullParser parser = Xml.newPullParser();
                InputStream ins = null;
                try {
                    int currentVersion = getVersion();
                    if (currentVersion == INIT_REGION_VERSION) {
                        try {
                            ins = ApplicationContext.getContext().getAssets().open("region/region.xml");
                        } catch (IOException e) {
                            Log.e(TAG, "can't load region from the resource. reason = %s", e.getMessage());
                            return;
                        }
                    } else {
                        try {
                            ins = new FileInputStream(new File(StorageConstants.DATAROOT_PUBLIC_PATH + "region.xml"));
                        } catch (FileNotFoundException e) {
                            try {
                                ins = ApplicationContext.getContext().getAssets().open("region/region.xml");
                            } catch (IOException e1) {
                                Log.e(TAG, "can't load region from the resource. reason = %s", e.getMessage());
                                return;
                            }
                        }
                    }
                    parser.setInput(ins, "UTF-8");
                    int eventType = parser.getEventType();
                    String lastProvinceName = null;
                    String lastCityName = null;
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                final String name = parser.getName();
                                if ("province".equalsIgnoreCase(name)) {
                                    lastProvinceName = parser.getAttributeValue(null, "value");
                                    Log.d(TAG, "province: %s", lastProvinceName);
                                    ArrayList<LinkedHashMap<String, ArrayList<String>>> cities = new ArrayList();
                                    regionMap.put(lastProvinceName, cities);
                                } else if (name.equals("city")) {
                                    lastCityName = parser.getAttributeValue(null, "value");
                                    LinkedHashMap<String, ArrayList<String>> cities = new LinkedHashMap();
                                    cities.put(lastCityName, new ArrayList<String>());
                                    regionMap.get(lastProvinceName).add(cities);
                                } else if (name.equals("county")) {
                                    ArrayList<LinkedHashMap<String, ArrayList<String>>> cities = regionMap.get(lastProvinceName);
                                    for (LinkedHashMap<String, ArrayList<String>> hashMap : cities) {
                                        if (hashMap.get(lastCityName) != null) {
                                            hashMap.get(lastCityName).add(parser.getAttributeValue(null, "value"));
                                            break;
                                        }
                                    }
                                }
                                break;
                        }
                        eventType = parser.next();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "can't load region from the resource. reason = %s", e.getMessage());
                } finally {
                    if (ins != null) {
                        try {
                            ins.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.i(TAG, "end time is %d, cost %d in total", startTime, System.currentTimeMillis() - startTime);
                if (callback != null) {
                    ThreadPool.postToMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onRegionInited(regionMap);
                        }
                    });
                }
            }
        });
    }


    public Map<String, ArrayList<LinkedHashMap<String, ArrayList<String>>>> getRegionMap() {
        return regionMap;
    }

    public void release() {
        if (regionMap != null) {
            regionMap.clear();
        }
    }

    public void updateFileFromServer(final Context context) {
        final int currentVersion = getVersion();
        UpdateRegionNetScene scene = new UpdateRegionNetScene(currentVersion, new NetSceneResponseCallback<Racecar.GetAddrDataResponse>() {
            @Override
            public void onLocalNetworkIssue(IRequest request) {

            }

            @Override
            public void onResponse(IRequest request, Racecar.GetAddrDataResponse result) {
                if (result.getVersion() != currentVersion) {
                    int lastIndex = result.getUrl().lastIndexOf("/");
                    if (lastIndex == -1) {
                        return;
                    }
                    String fileName = result.getUrl().substring(lastIndex + 1, result.getUrl().length());
                    String targetFile = StorageConstants.DATAROOT_PUBLIC_PATH + fileName;
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.putExtra(ProtocolConstants.IntentParams.DOWNLOAD_PATH, targetFile);
                    intent.putExtra(ProtocolConstants.IntentParams.URL_INFO, result.getUrl());
                    context.startService(intent);
                    lastDownloadMD5 = result.getMd5();
                    lastVersion = result.getVersion();
                    lastRegionUrl = result.getUrl();
                }
            }
        });
        scene.doScene();
    }

    private static class UpdateRegionNetScene extends NetSceneBase {
        private int version;

        public UpdateRegionNetScene(int version, ResponseCallBack callBack) {
            super(Racecar.CmdId.GET_ADDR_DATA_VALUE, callBack);
            this.version = version;
        }

        @Override
        public byte[] getRequestBody() {
            Racecar.GetAddrDataRequest.Builder builder = Racecar.GetAddrDataRequest.newBuilder();
            builder.setPrimaryReq(builderBaseRequest());
            builder.setVersion(version);
            return builder.build().toByteArray();
        }
    }

    public boolean matchLocalData(String province, String city, String district) {
        if (regionMap.isEmpty()) {
            return false;
        }

        if (!regionMap.containsKey(province)) {
            return false;
        }

        ArrayList<LinkedHashMap<String, ArrayList<String>>> cities = regionMap.get(province);
        if (cities == null || cities.isEmpty()) {
            return false;
        }

        for (LinkedHashMap<String, ArrayList<String>> map : cities) {
            if (map.containsKey(city) && map.get(city).indexOf(district) != -1) {
                return true;
            }
        }

        return false;
    }

    private static void updateVersion(int versionValue) {
        FileUtils.createFileIfNeed(new File(StorageConstants.COMM_SETTING_PATH));
        CfgFs cfgFs = new CfgFs(StorageConstants.COMM_SETTING_PATH);
        cfgFs.set(StorageConstants.SETTING_KEY.REGION_VERSION, versionValue);
    }

    private static int getVersion() {
        FileUtils.createFileIfNeed(new File(StorageConstants.COMM_SETTING_PATH));
        CfgFs cfgFs = new CfgFs(StorageConstants.COMM_SETTING_PATH);
        return cfgFs.getInt(StorageConstants.SETTING_KEY.REGION_VERSION, INIT_REGION_VERSION);
    }

}