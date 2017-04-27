package com.bemetoy.bp.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bemetoy.bp.BuildConfig;
import com.bemetoy.bp.R;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.ui.auth.AuthUINew;
import com.bemetoy.bp.ui.auth.RegisterUINEW;
import com.bemetoy.bp.uikit.BpActivity;

public class LauncherUI extends BpActivity {

    private static final String TAG = "UI.LauncherUI";
    private ImageButton registerBtn;
    private ImageButton loginBtn;
    private TextView versionTv;
    private TextView buildTimeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_launcher;
    }

    @Override
    protected void initView() {
        registerBtn = (ImageButton) this.findViewById(R.id.register_btn);
        loginBtn = (ImageButton) this.findViewById(R.id.login_btn);
        versionTv = (TextView) this.findViewById(R.id.version_tv);
        buildTimeTv = (TextView) this.findViewById(R.id.buildTime_tv);
        versionTv.setText(BuildConfig.VERSION_NAME);
        if(BuildConfig.DEBUG) {
            buildTimeTv.setText(String.valueOf(BuildConfig.buildTime));
        } else {
            buildTimeTv.setVisibility(View.GONE);

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged is invoked, newConfig is" + newConfig.toString());
    }

    @Override
    protected void initListener() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LauncherUI.this, RegisterUINEW.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LauncherUI.this, AuthUINew.class));
            }
        });
    }
}
