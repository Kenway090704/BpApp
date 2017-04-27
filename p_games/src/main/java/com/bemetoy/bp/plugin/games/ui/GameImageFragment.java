package com.bemetoy.bp.plugin.games.ui;

import android.os.Bundle;

import com.bemetoy.bp.plugin.games.R;
import com.bemetoy.bp.plugin.games.databinding.UiGameImageBinding;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.BaseDataBindingFragment;

/**
 * Created by Tom on 2016/6/18.
 */
public class GameImageFragment extends BaseDataBindingFragment<UiGameImageBinding>{

    private static final String TAG = "Games.GameImageFragment";

    @Override
    public void init() {
        Bundle bundle = getArguments();
        if(bundle == null) {
            Log.w(TAG, "GameImageFragment argument is null");
            return;
        }

        String imageUrl = bundle.getString(ProtocolConstants.IntentParams.GAME_IMAGE);
        mBinding.setImageUrl(imageUrl);
    }

    @Override
    public int getLayoutId() {
        return R.layout.ui_game_image;
    }
}
