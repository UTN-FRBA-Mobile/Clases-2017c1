package ar.edu.utn.frba.myapplication.oauth;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ar.edu.utn.frba.myapplication.BuildConfig;
import ar.edu.utn.frba.myapplication.R;
import ar.edu.utn.frba.myapplication.api.Callback;
import ar.edu.utn.frba.myapplication.api.SlackApi;
import ar.edu.utn.frba.myapplication.api.responses.OAuthAccessResponse;
import ar.edu.utn.frba.myapplication.storage.Preferences;

public class OAuthActivity extends AppCompatActivity {

    private static final Uri REDIRECT_URI = Uri.parse("https://mobile.frba.utn.edu.ar/");

    private WebView webView;
    private ContentLoadingProgressBar progressBar;
    private Executor executor = Executors.newSingleThreadExecutor();
    private Preferences preferences = Preferences.get(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);
        progressBar = (ContentLoadingProgressBar) findViewById(R.id.progressBar);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return OAuthActivity.this.shouldOverrideUrlLoading(request.getUrl());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return OAuthActivity.this.shouldOverrideUrlLoading(Uri.parse(url));
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                setLoading(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                setLoading(false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadLoginUrl();
    }

    void loadLoginUrl() {
        String url = SlackApi.getOAuthURL(BuildConfig.SLACK_CLIENT_ID, "client", REDIRECT_URI.toString());
        webView.loadUrl(url);
    }

    boolean shouldOverrideUrlLoading(Uri url) {
        if (url.getScheme().equals(REDIRECT_URI.getScheme()) && url.getHost().equals(REDIRECT_URI.getHost())) {
            if (SlackApi.isOAuthDenied(url)) {
                logoutAndTryAgain();
            }
            else {
                getAuthToken(url);
            }
            return true;
        }
        return false;
    }

    void logoutAndTryAgain() {
        clearCookies();
        loadLoginUrl();
    }

    public void clearCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this);
            cookieSyncMngr.startSync();
            CookieManager cookieManager= CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    void getAuthToken(Uri url) {
        setLoading(true);
        executor.execute(SlackApi.oauthAccess(BuildConfig.SLACK_CLIENT_ID, BuildConfig.SLACK_CLIENT_SECRET, url, new Callback<OAuthAccessResponse>() {

            @Override
            public void onSuccess(OAuthAccessResponse response) {
                if (response.isOk()) {
                    preferences.setAccessToken(response.getAccessToken());
                }
                finish();
            }

            @Override
            public void onError(Exception e) {
                finish();
            }
        }));
    }

    void setLoading(boolean loading) {
        if (loading) {
            progressBar.show();
        }
        else {
            progressBar.hide();
        }
    }
}
