package com.bemetoy.bp.plugin.personalcenter.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewTreeObserver;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.personalcenter.R;
import com.bemetoy.bp.plugin.personalcenter.databinding.UiChooseAvatarBinding;
import com.bemetoy.bp.plugin.personalcenter.model.NetSceneGetAvatarList;
import com.bemetoy.bp.plugin.personalcenter.model.NetSceneModifyAccountInfo;
import com.bemetoy.bp.plugin.personalcenter.model.PersonalCenterLogic;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.bp.uikit.widget.recyclerview.DividerGridItemDecoration;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.network.NetSceneResponseCallback;
import com.bemetoy.stub.network.NetSceneCallbackLoadingWrapper;
import com.bemetoy.stub.ui.LoadingDialog;

import java.util.List;

/**
 * Created by Tom on 2016/6/6.
 */
public class ChooseAvatarUI extends BaseDataBindingActivity<UiChooseAvatarBinding> {

    private static final String TAG = "PersonalCenter.ChooseAvatarUI";
    private int mUserId;
    private String mOldImgUrl;
    private NetSceneGetAvatarList getAvatarList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_choose_avatar;
    }

    @Override
    protected void init() {
        final AvatarChooseAdapter avatarChooseAdapter = new AvatarChooseAdapter(this);

        mOldImgUrl = getIntent().getStringExtra(ProtocolConstants.IntentParams.USER_AVATAR);
        mUserId = getIntent().getIntExtra(ProtocolConstants.IntentParams.USER_ID, 0);

        mBinding.avatarVr.setAdapter(avatarChooseAdapter);
        final StaggeredGridLayoutManager mgr = new StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.VERTICAL);
        mBinding.avatarVr.setLayoutManager(mgr);
        mBinding.avatarVr.addItemDecoration(new DividerGridItemDecoration(this, R.drawable.divider));
        avatarChooseAdapter.setCurrentUrl(mOldImgUrl);

        mBinding.avatarVr.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int count = mBinding.avatarVr.getMeasuredWidth() / getResources().getDimensionPixelSize(R.dimen.choose_avatar_ui_avatar_item_size);
                Log.i(TAG, "span count : %d.", count);
                if (count > 0) {
                    mgr.setSpanCount(count);
                }
            }
        });

        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        getAvatarList = new NetSceneGetAvatarList(new NetSceneCallbackLoadingWrapper<Racecar.AofeiGetIconsResponse>() {
            @Override
            public void onResponse(IRequest request, Racecar.AofeiGetIconsResponse result) {
                if(result.getPrimaryResp().getResult() != Racecar.ErrorCode.ERROR_OK_VALUE) {
                    ToastUtil.show(R.string.personal_load_avatar_list_error);
                    return;
                }

                String jsonData = result.getJson();
                Log.v(TAG, "user avatar json data %s", jsonData);
                List<String> avatars = PersonalCenterLogic.transformJsonData(jsonData);
                avatarChooseAdapter.appendData(avatars);
                avatarChooseAdapter.notifyDataSetChanged();
                mBinding.actionBtn.setEnabled(avatars.size() > 0);
            }

            @Override
            public void onNetSceneEnd() {
                super.onNetSceneEnd();
                loadingDialog.dismiss();
            }
        });

        getAvatarList.doScene();
        mBinding.titleBarLt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newAvatarImg = avatarChooseAdapter.getChooseAvatarUri();
                String fileName = newAvatarImg.substring(newAvatarImg.lastIndexOf("/") + 1, newAvatarImg.length());

                if(Util.isNullOrNil(newAvatarImg)) {
                    ToastUtil.show(R.string.personal_choose_avatar_notice);
                    return;
                }

                if(mOldImgUrl.equals(newAvatarImg)) {
                    setResult(ProtocolConstants.ResultCode.ACTION_DONE, getIntent());
                    ThreadPool.postToMainThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                } else {
                    updateUserAvatar(fileName);
                }
            }
        });

    }


    /**
     * update the user avatar.
     *
     * @param url
     */
    private void updateUserAvatar(final String url) {
        Racecar.AccountInfo.Builder builder = Racecar.AccountInfo.newBuilder();
        builder.setUserId(mUserId);
        builder.setIcon(url);

        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        NetSceneModifyAccountInfo netSceneModifyAccountInfo = new NetSceneModifyAccountInfo(builder.build(),
                new NetSceneResponseCallback<Racecar.ModifyAccountInfoResponse>() {
                    @Override
                    public void onResponse(IRequest request, Racecar.ModifyAccountInfoResponse result) {
                        if(result == null || result.getPrimaryResp().getResult() != Racecar.ErrorCode.ERROR_OK_VALUE) {
                            ToastUtil.show(R.string.personal_save_avatar_failed);
                            Log.e(TAG, "modify user avatar failed");
                            return;
                        }

                        ThreadPool.post(new Runnable() {
                            @Override
                            public void run() {
                                AccountLogic.updateAccountInfo(StorageConstants.Info_Key.HDHEAD_IMG_URL, url);
                            }
                        });
                        ThreadPool.postToMainThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                intent.putExtra(ProtocolConstants.IntentParams.USER_AVATAR, url);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }, 1000);

                    }

                    @Override
                    public void onNetSceneEnd() {
                        loadingDialog.dismiss();
                    }
                });
        netSceneModifyAccountInfo.doScene();
    }

    @Override
    protected void onDestroy() {
        if(getAvatarList != null) {
            getAvatarList.cancel();
        }
        super.onDestroy();
    }
}
