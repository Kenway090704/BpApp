package com.bemetoy.bp.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.bemetoy.bp.BuildConfig;
import com.bemetoy.bp.R;
import com.bemetoy.bp.autogen.events.DownLoadFinishEvent;
import com.bemetoy.bp.autogen.events.NoticeUpdateEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.databinding.UiHomePageBinding;
import com.bemetoy.bp.model.HomeLogic;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.car.ui.UnityPlayerActivity;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.arithmetic.MD5;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.core.event.AccurateEventObserver;
import com.bemetoy.bp.sdk.core.event.EventObserver;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.plugin.imageloader.ImageLoader;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.FileUtils;
import com.bemetoy.bp.sdk.utils.NetWorkUtil;
import com.bemetoy.bp.ui.adapter.HomePageGameAdapter;
import com.bemetoy.bp.uikit.widget.recyclerview.VRecyclerView;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.app.AppCore;
import com.bemetoy.stub.app.WebViewUI;
import com.bemetoy.stub.db.StorageManager;
import com.bemetoy.stub.model.LocationLogic;
import com.bemetoy.stub.network.NetSceneResponseCallback;
import com.bemetoy.stub.network.home.NetSceneCheckUpdate;
import com.bemetoy.stub.network.home.NetSceneHomeRes;
import com.bemetoy.stub.network.sync.SyncManager;
import com.bemetoy.stub.service.DownloadService;
import com.bemetoy.stub.ui.AccountChangeActivity;
import com.bemetoy.stub.ui.BPDialogManager;
import com.bemetoy.stub.ui.LoadingDialog;
import com.bemetoy.stub.util.AppUtil;
import com.bemetoy.stub.util.JsonManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 2016/4/14.
 */
public class HomePageUI extends AccountChangeActivity<UiHomePageBinding> {

    private static final String TAG = "UI.HomePageUI";
    private EventObserver mObserver;
    private StorageManager.OnStorageManagerStateChangeListener mListener;
    private HomePageGameAdapter mAdapter;
    private LoadingDialog loadingDialog = null;
    private boolean isFirstEnter = true;
    private ArrayList<Racecar.Operation> operations = new ArrayList();
    private EventObserver downloadApkObserver;
    private String leastVersionUrl = null;
    private LocationClient mClient;
    private BDLocationListener bdLocationListener;
    private static String defaultTmallUrl = ProtocolConstants.DEFAULT_TMALL_URL;
    private U3DLaunchFinishBroadCastReceiver receiver = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isFirstEnter) {
            loadingDialog.show();
            isFirstEnter = false;
        }

        NetSceneHomeRes netSceneHomeRes = new NetSceneHomeRes(new HomeResResponseCallback());
        netSceneHomeRes.doScene();
        SyncManager.getInstance().doSync(Racecar.SyncRequest.Selector.JSON, null);
    }

    @Override
    protected void init() {
        loadingDialog = new LoadingDialog(this);

        mObserver = new AccurateEventObserver<NoticeUpdateEvent>() {
            @Override
            public void onReceiveEvent(NoticeUpdateEvent event) {
                mBinding.noticeDotIm.setVisibility(View.VISIBLE);
            }
        };

        mListener = new StorageManager.OnStorageManagerStateChangeListener() {
            @Override
            public void onStateChange(boolean isInit) {
                if (isInit) {
                    checkUnReadMessage();
                }
            }
        };

        StorageManager.getMgr().addOnStorageManagerStateChangeListener(mListener);
        RxEventBus.getBus().register(NoticeUpdateEvent.ID, mObserver);
        mAdapter = new HomePageGameAdapter(getLayoutInflater());
        mBinding.dataRv.setHasFixedSize(true);
        mBinding.dataRv.postDelayed(new Runnable() {
            @Override
            public void run() {
                adjustItemCountAndHeight(generateEmptyGameList(new ArrayList<Racecar.Game>()));
            }
        }, 10);

        mBinding.dataRv.setLayoutManager(new LinearLayoutManager(this));
        mBinding.dataRv.setAdapter(mAdapter);
        initFunction();
        checkVersion();
        ActivityCompat.requestPermissions(this ,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                ProtocolConstants.PermissionCode.GPS);
    }

    /**
     * Set the listener to the view.
     */
    private void initFunction() {
        mBinding.dataRv.setOnItemClickListener(new VRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                PluginStubBus.doAction(HomePageUI.this, PluginConstants.Plugin.PLUGIN_NAME_P_GAMES,
                        PluginConstants.Games.Action.CMD_START_GAMES_UI, 0, null);
            }
        });

        mBinding.rankLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppUtil.checkNetworkFirst(HomePageUI.this)) {
                    return;
                }
                PluginStubBus.doAction(HomePageUI.this, PluginConstants.Plugin.PLUGIN_NAME_P_RANKING,
                        PluginConstants.Ranking.Action.CMD_START_RANKING_UI, 0, null);
            }
        });

        mBinding.shopLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginStubBus.doAction(HomePageUI.this, PluginConstants.Plugin.PLUGIN_NAME_P_MALL,
                        PluginConstants.Mall.Action.CMD_START_MALL_UI, 0, null);
            }
        });

        mBinding.friendsLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginStubBus.doAction(HomePageUI.this, PluginConstants.Plugin.PLUGIN_NAME_P_FRIENDS,
                        PluginConstants.FRIENDS.Action.CMD_START_FRIENDS_UI, 0, null);
            }
        });

        mBinding.taskLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppUtil.checkNetworkFirst(HomePageUI.this)) {
                    return;
                }

                PluginStubBus.doAction(HomePageUI.this, PluginConstants.Plugin.PLUGIN_NAME_P_TASKS_CENTER,
                        PluginConstants.TasksCenter.Action.CMD_START_TASKS_CENTER_UI, 0, null);
            }
        });

        mBinding.noticeLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginStubBus.doAction(HomePageUI.this, PluginConstants.Plugin.PLUGIN_NAME_P_NOTICE,
                        PluginConstants.Notice.Action.CMD_START_NOTICE_CENTER_UI, 0, null);
                mBinding.noticeDotIm.setVisibility(View.INVISIBLE);
                ThreadPool.post(new Runnable() {
                    @Override
                    public void run() {
                        if (StorageManager.getMgr().hasInit()) {
                            StorageManager.getMgr().getSysMessageStorage().markAllHasRead();
                            StorageManager.getMgr().getMyMessageStorage().markAllHasRead();
                        }
                    }
                });
            }
        });

        mBinding.tmallLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppUtil.checkNetworkFirst(HomePageUI.this)) {
                    return;
                }

                Intent intent = new Intent(HomePageUI.this, WebViewUI.class);
                intent.putExtra(ProtocolConstants.IntentParams.URL_INFO, defaultTmallUrl);
                startActivity(intent);
            }
        });

        mBinding.databasesLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppUtil.checkNetworkFirst(HomePageUI.this)) {
                    return;
                }

                Intent intent = new Intent(HomePageUI.this, WebViewUI.class);
                intent.putExtra(ProtocolConstants.IntentParams.URL_INFO, ProtocolConstants.DATABASE_URL);
                intent.putExtra(ProtocolConstants.IntentParams.SHOW_BACK, false);
                startActivity(intent);
            }
        });

        mBinding.runWindowIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(ProtocolConstants.IntentParams.OPERATION_LIST, operations);
                PluginStubBus.doAction(HomePageUI.this, PluginConstants.Plugin.PLUGIN_NAME_P_OPERATIONS,
                        PluginConstants.Operations.Action.CMD_START_OPERATIONS_UI, 0, bundle);
            }
        });

        mBinding.activationLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeLogic.showExchangeDialog(HomePageUI.this);
            }
        });

        mBinding.myCarIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.myCarIm.setEnabled(false);
                Intent intent = new Intent(HomePageUI.this, UnityPlayerActivity.class);
                String accountJson = JsonManager.getAccountJson(AccountLogic.getAccountInfo(false));
                intent.putExtra(ProtocolConstants.IntentParams.USER_INFO, accountJson);
                intent.putExtra(ProtocolConstants.IntentParams.USER_ID, PluginStubBus.getInt(PluginConstants.Plugin.PLUGIN_NAME_APP,
                        PluginConstants.App.DataKey.GET_USER_ID, -1));
                intent.putExtra(ProtocolConstants.IntentParams.SESSION_ID,
                        AppCore.getCore().getAccountManager().getAccountInfo().getSessionKey());
                startActivity(intent);
            }
        });

        mBinding.avatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginStubBus.doAction(HomePageUI.this, PluginConstants.Plugin.PLUGIN_NAME_P_PERSONAL_CENTER,
                        PluginConstants.PersonalCenter.Action.CMD_START_PERSONAL_CENTER_UI, 0, null);
            }
        });

        mBinding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                adjustTabWith();
            }
        });

        onAccountUpdate(AccountLogic.getAccountInfo());
        // load the user friends data this data is needed in the ranking module
        PluginStubBus.doAction(this, PluginConstants.Plugin.PLUGIN_NAME_P_FRIENDS, PluginConstants.FRIENDS.Action.CMD_INIT_FRIENDS_DATA, 0, null);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ProtocolConstants.BroadCastAction.U3D_LAUNCH_FINISH);
        receiver = new U3DLaunchFinishBroadCastReceiver();
        this.registerReceiver(receiver, intentFilter);

    }

    /**
     * check version.
     */
    private void checkVersion() {
        downloadApkObserver = new AccurateEventObserver<DownLoadFinishEvent>() {
            @Override
            public void onReceiveEvent(DownLoadFinishEvent event) {
                if (leastVersionUrl.equals(event.targetUrl)) {
                    String fileMd5 = MD5.getMD5(new File(event.localPath));
                    if(PluginStubBus.getString(PluginConstants.Plugin.PLUGIN_NAME_APP,
                            PluginConstants.App.DataKey.GET_DOWN_FILE_MD5, "").equals(fileMd5)) {
                        HomeLogic.showUpdateDialog(HomePageUI.this, PluginStubBus.getString(PluginConstants.Plugin.PLUGIN_NAME_APP,
                                PluginConstants.App.DataKey.GET_UPDATE_LOG, ""), event.localPath);
                    }
                }
            }
        };

        RxEventBus.getBus().register(DownLoadFinishEvent.ID, downloadApkObserver);
        NetSceneCheckUpdate update = new NetSceneCheckUpdate(new NetSceneResponseCallback<Racecar.CheckVersionResponse>() {
            @Override
            public void onResponse(IRequest request, Racecar.CheckVersionResponse result) {
                if (result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                    if (result.getVersion().equals(BuildConfig.VERSION_NAME)) {
                        return;
                    }

                    int fileNameIndex = result.getUrl().lastIndexOf("/");
                    if (fileNameIndex == -1) {
                        return;
                    }
                    leastVersionUrl = result.getUrl();
                    PluginStubBus.setString(PluginConstants.Plugin.PLUGIN_NAME_APP,
                            PluginConstants.App.DataKey.SET_UPDATE_LOG, result.getChangelog());
                    String fileName = result.getUrl().substring(fileNameIndex + 1, result.getUrl().length());
                    String filePath = StorageConstants.APK_DOWN_LOAD_PATH + File.separator + fileName;
                    PluginStubBus.setString(PluginConstants.Plugin.PLUGIN_NAME_APP,
                            PluginConstants.App.DataKey.SET_DOWN_FILE_MD5, result.getMd5());
                    if (FileUtils.isFileExist(filePath) && result.getMd5().equals(MD5.getMD5(filePath))) {
                        HomeLogic.showUpdateDialog(HomePageUI.this, result.getChangelog(), filePath);
                    } else {
                        if (NetWorkUtil.isWifi(ApplicationContext.getContext())) {
                            Intent intent = new Intent(HomePageUI.this, DownloadService.class);
                            intent.putExtra(ProtocolConstants.IntentParams.URL_INFO, result.getUrl());
                            intent.putExtra(ProtocolConstants.IntentParams.DOWNLOAD_PATH, filePath);
                            startService(intent);
                        }
                    }
                }
            }
        });
        update.doScene();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ProtocolConstants.PermissionCode.GPS:
                if(permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initLocationInfo();
                }
                break;
        }
    }

    private void initLocationInfo() {
        mClient = LocationLogic.getLocationClient(ApplicationContext.getApplication());
        bdLocationListener =  new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation != null) {
                    if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 161) {
                        LocationLogic.setLastLocation(bdLocation.getLatitude(), bdLocation.getLongitude());
                    }
                    LocationLogic.setHasLocationed(true);
                }
            }
        };

        mClient.registerLocationListener(bdLocationListener);
        mClient.start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_home_page;
    }

    @Override
    public void onDestroy() {
        RxEventBus.getBus().unregister(NoticeUpdateEvent.ID, mObserver);
        RxEventBus.getBus().unregister(DownLoadFinishEvent.ID, downloadApkObserver);
        StorageManager.getMgr().removeStorageManagerStateChangeListener(mListener);
        //unregisterReceiver(receiver);
        if(mClient != null) {
            if(bdLocationListener != null) {
                mClient.unRegisterLocationListener(bdLocationListener);
            }
            mClient.stop();
            mClient = null;
        }
        this.unregisterReceiver(receiver);
        if(loadingDialog != null) {
            loadingDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            adjustItemCountAndHeight(mAdapter.getItems());
        }
    }

    @Override
    public void onAccountUpdate(Racecar.AccountInfo accountInfo) {
        mBinding.setAccountInfo(accountInfo);
        ImageLoader.load(AccountLogic.getAvatarImageUrl(accountInfo.getIcon()), mBinding.avatarIv);
    }

    private void checkUnReadMessage() {
        ThreadPool.post(new Runnable() {
            @Override
            public void run() {
                if(!StorageManager.getMgr().hasInit()) {
                    return;
                }

                if (StorageManager.getMgr().getSysMessageStorage().hasUnReadMessage()
                        || StorageManager.getMgr().getMyMessageStorage().hasUnReadMessage()) {
                    RxEventBus.getBus().publish(new NoticeUpdateEvent());
                }
            }
        });
    }


    private void adjustItemCountAndHeight(List<Racecar.Game> list) {
        if (!mAdapter.getItems().isEmpty() && list.isEmpty()) {
            return;
        }
        list = generateEmptyGameList(list);
        // 保证始终是以横屏状态下的高度来显示列表、
        int height = mBinding.containerVg.getMeasuredHeight() > mBinding.containerVg.getMeasuredWidth() ? mBinding.containerVg.getMeasuredWidth() : mBinding.containerVg.getMeasuredHeight();
        int originalItemHeight = getResources().getDimensionPixelSize(R.dimen.home_page_game_item_height);
        int count = height / originalItemHeight;
        if (count <= 4) {
            int itemHeight = height / 4;
            mAdapter.setData(list.subList(0, list.size() >= 4 ? 4 : list.size()));
            // TODO: 16/6/14 albieliang, update item height.
            mAdapter.setItemHeight(itemHeight);
        } else {
            int itemHeight = height / count;
            if (list.size() >= count) {
                mAdapter.setData(list.subList(0, count));
            } else {
                itemHeight = height / list.size();
                mAdapter.setData(list);
            }
            // TODO: 16/6/14 albieliang, update item height.
            mAdapter.setItemHeight(itemHeight);
        }
        mAdapter.notifyDataSetChanged();
        mBinding.dataRv.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.dataRv.smoothScrollBy(0, 100);
            }
        }, 1000);
    }

    /**
     * Append empty game info.
     *
     * @param currentList
     * @return
     */
    private List<Racecar.Game> generateEmptyGameList(List<Racecar.Game> currentList) {
        List<Racecar.Game> newList = new ArrayList<>(currentList);
        int currentSize = currentList.size();
        for (int i = 0; i < 6 - currentSize; i++) {
            newList.add(Racecar.Game.newBuilder().setId(-1).
                    setTitle("本地区无更多比赛").build());
        }
        return newList;
    }

    private void adjustTabWith() {
        int screenWidth = mBinding.functionLl.getMeasuredWidth();
        int itemCount = mBinding.functionLl.getChildCount();
        final int maxTextCount = 4;
        int itemWidth = screenWidth / itemCount;
        int defaultTextSize = getResources().getDimensionPixelSize(R.dimen.home_page_tab_text_default_size);
        int padding = getResources().getDimensionPixelSize(R.dimen.function_btn_padding);
        int textSize = (itemWidth - padding * 2) / maxTextCount;
        if (textSize >= defaultTextSize) {
            return;
        }
        mBinding.setTabTextSize(textSize);
    }

    public static String getTmallStoreUrl() {
        return defaultTmallUrl;
    }


    private class HomeResResponseCallback extends NetSceneResponseCallback<Racecar.GetHomeResponse> {

        @Override
        public void onRequestFailed(IRequest request) {
            adjustItemCountAndHeight(new ArrayList<Racecar.Game>());
        }

        @Override
        public void onLocalNetworkIssue(IRequest request) {
            adjustItemCountAndHeight(new ArrayList<Racecar.Game>());
            super.onLocalNetworkIssue(request);
        }

        @Override
        public void onResponse(final IRequest request, final Racecar.GetHomeResponse result) {
            if (result != null && result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                ThreadPool.post(new Runnable() {
                    @Override
                    public void run() {
                        Racecar.AccountInfo info = result.getAccountInfo();
                        AccountLogic.saveAccountInfo(info);
                        operations = new ArrayList(result.getOperationList());
                    }
                });
                checkUnReadMessage();
                adjustItemCountAndHeight(result.getGameList());
                defaultTmallUrl = result.getOnlineStore();
            }
        }

        @Override
        public void onNetSceneEnd() {
            ThreadPool.postToMainThread(new Runnable() {
                @Override
                public void run() {
                    if (loadingDialog != null) {
                        loadingDialog.dismiss();
                    }
                }
            });
        }
    }

    /**
     * change the mycarim status.
     */
    private class U3DLaunchFinishBroadCastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mBinding != null && mBinding.myCarIm != null) {
                mBinding.myCarIm.setEnabled(true);
            }
        }
    }

}
