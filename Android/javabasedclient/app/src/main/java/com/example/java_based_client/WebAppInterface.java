package com.example.java_based_client;

import android.content.Context;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class WebAppInterface {

    private Context mContext;
    private ViewGroup mParentLayout;
    private WebView mWebView;

    public WebAppInterface(Context context, ViewGroup parentLayout, WebView webView) {
        this.mContext = context;
        this.mParentLayout = parentLayout;
        this.mWebView = webView;
    }

    // Show a toast from html.
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    // Exit the Immersive Reader.
    @JavascriptInterface
    public void immersiveReaderExit() {
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                destroyWebView();
            }
        });
    }

    private void destroyWebView() {
        // Removes the WebView from its parent view before doing anything.
        mParentLayout.removeView(mWebView);
        // Cleans things up before destroying the WebView.
        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.loadUrl("about:blank");
        mWebView.onPause();
        mWebView.removeAllViews();
        mWebView.pauseTimers();
        mWebView.destroy();
    }

}
