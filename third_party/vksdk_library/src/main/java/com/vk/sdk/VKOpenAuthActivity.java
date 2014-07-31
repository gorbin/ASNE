//
//  Copyright (c) 2014 VK.com
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy of
//  this software and associated documentation files (the "Software"), to deal in
//  the Software without restriction, including without limitation the rights to
//  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
//  the Software, and to permit persons to whom the Software is furnished to do so,
//  subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
//  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
//  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
//  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
//  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

package com.vk.sdk;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.Locale;

/**
 * Activity for request OAuth authorization in case of missing VK app.
 */
public class VKOpenAuthActivity extends Activity {
    public static final String VK_EXTRA_CLIENT_ID = "client_id";
    public static final String VK_EXTRA_SCOPE = "scope";
    public static final String VK_EXTRA_API_VERSION = "version";
    public static final String VK_EXTRA_REVOKE = "revoke";

    public static final String VK_RESULT_INTENT_NAME = "com.vk.auth-token";
    public static final String VK_EXTRA_TOKEN_DATA = "extra-token-data";
	public static final String VK_EXTRA_VALIDATION_URL = "extra-validation-url";

    private static final String REDIRECT_URL = "https://oauth.vk.com/blank.html";

    protected WebView mWebView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new VKOpenAuthView(this));

        hideActionBar();
        findViewById(android.R.id.content).setBackgroundColor(Color.rgb(240, 242, 245));
        loadPage();
    }

    private void loadPage() {
        try {
	        String urlToLoad;
	        urlToLoad = getIntent().getStringExtra(VK_EXTRA_VALIDATION_URL);
	        if (urlToLoad == null)
	        {
	            int appId = getIntent().getIntExtra(VK_EXTRA_CLIENT_ID, 0);
	            String scope = getIntent().getStringExtra(VK_EXTRA_SCOPE),
	                    apiV = getIntent().getStringExtra(VK_EXTRA_API_VERSION);
	            boolean revoke = getIntent().getBooleanExtra(VK_EXTRA_REVOKE, false);
	            urlToLoad = String.format(Locale.US,
	                    "https://oauth.vk.com/authorize?client_id=%s" +
	                            "&scope=%s" +
	                            "&redirect_uri=%s" +
	                            "&display=mobile" +
	                            "&v=%s" +
	                            "&response_type=token&revoke=%d",
	                    appId, scope, REDIRECT_URL, apiV, revoke ? 1 : 0);
	        }
            mWebView = (WebView) findViewById(android.R.id.copyUrl);
            mWebView.setWebViewClient(new OAuthWebViewClient());
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            mWebView.loadUrl(urlToLoad);
            mWebView.setVisibility(View.INVISIBLE);

        } catch (Exception e) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void hideActionBar() {
        try {
            if (Build.VERSION.SDK_INT >= 11 && getActionBar() != null) {
                getActionBar().hide();
            }
        } catch (Exception ignored) {
        }
    }

    private class OAuthWebViewClient extends WebViewClient {
        public boolean canShowPage = true;
        private boolean processUrl(String url) {
            if (url.startsWith(REDIRECT_URL)) {
                Intent data = new Intent(VK_RESULT_INTENT_NAME);
                data.putExtra(VK_EXTRA_TOKEN_DATA, url.substring(url.indexOf('#') + 1));
                if (getIntent().hasExtra(VK_EXTRA_VALIDATION_URL))
                    data.putExtra(VK_EXTRA_VALIDATION_URL, true);
                setResult(RESULT_OK, data);
                finish();
                return true;
            }
            return false;
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (processUrl(url))
                return true;
            canShowPage = true;
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            processUrl(url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (canShowPage)
                view.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            canShowPage = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(VKOpenAuthActivity.this)
                    .setMessage(description)
                    .setPositiveButton(R.string.vk_retry, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadPage();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
            try {
                builder.show();
            } catch (Exception e) {
                if (VKSdk.DEBUG)
                	e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent(VK_RESULT_INTENT_NAME);
        setResult(RESULT_CANCELED, data);
        super.onBackPressed();
    }

    private static class VKOpenAuthView extends RelativeLayout {
        public VKOpenAuthView(Context context) {
            super(context);
            ProgressBar progress = new ProgressBar(context);
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
            progress.setLayoutParams(lp);
            addView(progress);

            WebView webView = new WebView(context);
            lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            webView.setLayoutParams(lp);
            addView(webView);
            webView.setId(android.R.id.copyUrl);
            webView.setVisibility(View.INVISIBLE);
        }
    }
}
