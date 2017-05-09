package com.bemetoy.stub.app;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.stub.R;
import com.bemetoy.bp.uikit.BpActivity;
import com.bemetoy.stub.ui.LoadingDialog;

/**
 * Created by Tom on 2016/5/11.
 */
public class WebViewUI extends BpActivity {

    private static final String TAG = "Stub.WebViewUI";

    private WebView mWebView;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidBug5497Workaround.assistActivity(this);
          //无法获取网页
        String url = getIntent().getExtras().getString(ProtocolConstants.IntentParams.URL_INFO);
        Log.e(TAG,"url=="+url);
        boolean showBack = getIntent().getBooleanExtra(ProtocolConstants.IntentParams.SHOW_BACK, true);
        if(!showBack) {
            backBtn.setVisibility(View.INVISIBLE);
        }
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        mWebView.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        mWebView.getSettings().setSupportZoom(false);//是否可以缩放，默认true
        mWebView.getSettings().setBuiltInZoomControls(false);//是否显示缩放按钮，默认false
        mWebView.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        mWebView.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        mWebView.getSettings().setAppCacheEnabled(true);//是否使用缓存
        mWebView.getSettings().setDomStorageEnabled(true);//DOM Storage
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        // when open the webView for the first time shouldOverrideUrlLoading will not in invoke.
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if((!url.startsWith("http") && (!url.startsWith("https")))) {
                    return false;
                }

                if(shouldFinishActivity(url)) {
                    finish();
                    return false;
                }
                view.loadUrl(url);
                return false;
            }
        });

        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.show();
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress >= 100) {
                    dialog.dismiss();
                }
            }
        });

        Log.d(TAG, url);
        mWebView.loadUrl(url);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mWebView.getClass().getMethod("onPause").invoke(mWebView,(Object[])null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected int getLayoutId() {
        return R.layout.ui_webview;
    }


    protected void initView() {
        mWebView = (WebView) this.findViewById(R.id.content_webView);
        backBtn = (ImageButton) this.findViewById(R.id.back_btn);
    }


    protected void initListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private boolean shouldFinishActivity(String targetUrl) {
        if(targetUrl.equalsIgnoreCase(ProtocolConstants.GO_BACK_URL) || targetUrl.equalsIgnoreCase(ProtocolConstants.GO_BACK_URL + "/")) {
            return true;
        }
        return false;
    }
}

class AndroidBug5497Workaround {

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    public static void assistActivity (Activity activity) {
        new AndroidBug5497Workaround(activity);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private AndroidBug5497Workaround(Activity activity) {
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard/4)) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                // keyboard probably just became hidden
                frameLayoutParams.height = usableHeightSansKeyboard;
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }

}