package com.bemetoy.bp.plugin.personalcenter.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bemetoy.bp.plugin.personalcenter.R;
import com.bemetoy.bp.plugin.personalcenter.databinding.UiAvatarRaidoButtonBinding;
import com.bemetoy.bp.sdk.plugin.imageloader.ImageLoader;

import com.bemetoy.bp.uikit.widget.BpRadioButton;
import com.bemetoy.bp.uikit.widget.recyclerview.ExtRecyclerViewAdapter;

/**
 * Created by Tom on 2016/6/6.
 */
public class AvatarChooseAdapter extends ExtRecyclerViewAdapter<String> {

    private int checkId;
    private BpRadioButton lastRadioButton;
    private String currentUrl;
    private Context context;

    public AvatarChooseAdapter(Context context) {
        this.context = context;
    }

    public String getChooseAvatarUri() {
        if (lastRadioButton != null) {
            return lastRadioButton.getImageUrl();
        }
        return "";
    }

    public void setCurrentUrl(String str) {
        currentUrl = str;
    }


    @Override
    public IViewHolder<String> onCreateViewHolder(View view, int viewType) {
        return new SingleChooseViewHold();
    }

    @Override
    public View createItemView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(context).inflate(R.layout.ui_avatar_raido_button, parent, false);
    }


    private final class SingleChooseViewHold extends ExtRecyclerViewAdapter.DataBindingViewHolder<UiAvatarRaidoButtonBinding, String> {

        @Override
        public void onBind(final String item, int viewType) {
            super.onBind(item, viewType);
            ImageLoader.load(item, mBinding.avatarRb);
            if(currentUrl != null  && (item.equalsIgnoreCase(currentUrl))) {
                lastRadioButton = mBinding.avatarRb;
                lastRadioButton.setChecked(true);
            } else {
                mBinding.avatarRb.setChecked(false);
            }

            mBinding.avatarRb.setOnCheckedChangeListener(new BpRadioButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(BpRadioButton buttonView, boolean isChecked) {
                    if (lastRadioButton != null && lastRadioButton != buttonView) {
                        lastRadioButton.setChecked(false);
                    }

                    if (isChecked) {
                        lastRadioButton = buttonView;
                        checkId = buttonView.getId();
                        currentUrl = item;
                    }
                }
            });
        }
    }
}
