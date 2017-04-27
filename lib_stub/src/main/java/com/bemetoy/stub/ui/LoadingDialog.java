package com.bemetoy.stub.ui;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.bemetoy.bp.stub.R;
import com.bemetoy.bp.uikit.BpAlertDialog;

/**
 * Created by Tom on 2016/5/10.
 */
public class LoadingDialog extends BpAlertDialog {

    private Animation operatingAnim = null;

    public LoadingDialog(Context context) {
        super(context);
        setContentView(R.layout.ui_bp_loading_dialog);
        operatingAnim = AnimationUtils.loadAnimation(context, R.anim.rotate);
        final LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        operatingAnim.setFillAfter(true);
        setCancelable(true);

    }

    public void setTipsText(String string) {
        TextView view = (TextView) getContentView().findViewById(R.id.tips_tv);
        view.setText(string);
    }

    @Override
    public void show() {
        getContentView().findViewById(R.id.loading_im).startAnimation(operatingAnim);
        super.show();
    }

    @Override
    public void dismiss() {
        getContentView().findViewById(R.id.loading_im).clearAnimation();
        super.dismiss();
    }
}
