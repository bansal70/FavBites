package co.fav.bites.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import co.fav.bites.R;
import co.fav.bites.models.Config;
import co.fav.bites.models.Utils;


public class AboutActivity extends BaseActivity {

    private WebView wvAbout;
    private Dialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initViews();
    }

    private void initViews() {

        wvAbout = findViewById(R.id.wvAbout);
        initWebView();
        pd = Utils.showDialog(this);
        pd.show();
        wvAbout.loadUrl(Config.about_url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        wvAbout.setWebChromeClient(new MyWebChromeClient(this));
        wvAbout.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            /*@Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                wvTerms.loadUrl(url);
                return true;
            }*/

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pd.dismiss();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                pd.dismiss();
            }
        });

        wvAbout.clearCache(true);
        wvAbout.clearHistory();
        wvAbout.getSettings().setJavaScriptEnabled(true);
        wvAbout.setHorizontalScrollBarEnabled(false);
    }

    private class MyWebChromeClient extends WebChromeClient {
        Context context;

        private MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }
    }

    public void backScreen(View v) {
        finish();
    }
}
