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


public class TermsConditionsActivity extends BaseActivity {

    private WebView wvTerms;
    private Dialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);
        initViews();
    }

    private void initViews() {
        wvTerms = findViewById(R.id.wvTerms);
        pd = Utils.showDialog(this);

        pd.show();
        initWebView();
        wvTerms.loadUrl(Config.terms_conditions_url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        wvTerms.setWebChromeClient(new MyWebChromeClient(this));
        wvTerms.setWebViewClient(new WebViewClient() {
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

        wvTerms.clearCache(true);
        wvTerms.clearHistory();
        wvTerms.getSettings().setJavaScriptEnabled(true);
        wvTerms.setHorizontalScrollBarEnabled(false);
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
