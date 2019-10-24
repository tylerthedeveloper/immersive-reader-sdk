package com.example.java_based_client;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.java_based_client.Model.Chunk;
import com.example.java_based_client.Model.Content;
import com.example.java_based_client.Model.Message;
import com.example.java_based_client.Model.Options;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class ImmersiveReaderLauncher {

    private WebView mWebView;
    private ViewGroup mWebViewParent;
    private Context mContext;

    public ImmersiveReaderLauncher(Context context, ViewGroup webViewParent) {
        this.mContext = context;
        this.mWebViewParent = webViewParent;
    }

    public void launch(final ReadableContent readableContent) {

        GetAccessTokenAsyncTask getAccessTokenAsyncTask = new GetAccessTokenAsyncTask();
        GetAccessTokenAsyncTask.TaskParams taskParams = getAccessTokenAsyncTask.new TaskParams(Constants.CLIENT_ID, Constants.CLIENT_SECRET, Constants.TENANT_ID, new GetAccessTokenAsyncTask.IAccessTokenListener() {
            @Override
            public void onAccessTokenObtained(String accessToken) {

                Toast.makeText(mContext, "Launching Immersive Reader", Toast.LENGTH_SHORT).show();

                List<Chunk> chunkList = new ArrayList<>();
                for (ReadableTextChunk textChunk : readableContent.getTextChunks()) {
                    chunkList.add(new Chunk(textChunk.mText, textChunk.mLocale, "text/plain"));
                }

                Content content = new Content(readableContent.getTitle(), chunkList);
                Options options = new Options("ImmersiveReader-Exit", "en", 0);

                try {
                    loadImmersiveReaderWebView(accessToken, Constants.SUBDOMAIN, content, options);
                } catch (Exception ex) {
                    Toast.makeText(mContext, "Exception: " + ex.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        getAccessTokenAsyncTask.setTaskParams(taskParams);
        Toast.makeText(mContext, "Generating Access Token", Toast.LENGTH_SHORT).show();
        getAccessTokenAsyncTask.execute();
    }

    private void loadImmersiveReaderWebView(String accessToken, String subdomain, Content content, Options options) throws IllegalArgumentException {
        if (TextUtils.isEmpty(accessToken))
            throw new IllegalArgumentException("Access Token can not be empty");
        if (TextUtils.isEmpty(subdomain))
            throw new IllegalArgumentException("Subdomain can not be empty");
        if (content.chunks == null || content.chunks.size() == 0)
            throw new IllegalArgumentException("Content can not be empty");

        initializeWebView(accessToken, subdomain, content, options);
        mWebView.loadUrl("file:///android_asset/immersiveReader.html");
    }

    private void initializeWebView(String accessToken, String subdomain, Content content, Options options) {
        mWebView = new WebView(mContext);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setUserAgentString("Android");
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(false);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.setInitialScale(1);

        // Enable web view cookies
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }

        // Create the Message
        Message messageData = new Message(accessToken, subdomain, null, content, 0, options);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        final String messageJson = gson.toJson(messageData);

        // Set WebView Client
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mWebView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    StringBuilder scriptStringBuilder = new StringBuilder().append("handleLaunchImmersiveReader(").append(messageJson).append(")");
                    view.evaluateJavascript(scriptStringBuilder.toString(), null);
                } else {
                    StringBuilder urlStringBuilder = new StringBuilder().append("javascript:handleLaunchImmersiveReader()").append(messageJson).append(")");
                    view.loadUrl(urlStringBuilder.toString());
                }
                mWebView.setVisibility(View.VISIBLE);
            }
        });

        WebAppInterface jsInterface = new WebAppInterface(mContext, mWebView, mWebView);
        mWebView.addJavascriptInterface(jsInterface, "Android");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mWebViewParent.addView(mWebView, 0, layoutParams);
        mWebViewParent.invalidate();

    }

}
