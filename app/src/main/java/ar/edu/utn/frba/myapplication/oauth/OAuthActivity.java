package ar.edu.utn.frba.myapplication.oauth;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.ParseException;
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
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ar.edu.utn.frba.myapplication.BuildConfig;
import ar.edu.utn.frba.myapplication.R;
import ar.edu.utn.frba.myapplication.api.Callback;
import ar.edu.utn.frba.myapplication.api.PushServerApi;
import ar.edu.utn.frba.myapplication.api.requests.UserPushRegistration;
import ar.edu.utn.frba.myapplication.api.responses.IdentityResponse;
import ar.edu.utn.frba.myapplication.api.SlackApi;
import ar.edu.utn.frba.myapplication.api.responses.OAuthAccessResponse;
import ar.edu.utn.frba.myapplication.api.responses.Post;
import ar.edu.utn.frba.myapplication.storage.Preferences;
import ar.edu.utn.frba.myapplication.util.Util;
import retrofit2.Call;
import retrofit2.Response;

public class OAuthActivity extends AppCompatActivity {

    private static final Uri REDIRECT_URI = Uri.parse("https://mobile.frba.utn.edu.ar/");

    private WebView webView;
    private ContentLoadingProgressBar progressBar;
    private Executor executor = Executors.newSingleThreadExecutor();
    private Preferences preferences = Preferences.get(this);
    private PushServerApi mApiService = Util.createPushServerNetworkClient();

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
        //TODO: No pude combinar el scope para ser client y además poder pedir la identity del usuario
        //String url = SlackApi.getOAuthURL(BuildConfig.SLACK_CLIENT_ID, "identify,read,client", REDIRECT_URI.toString());
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
                    String accessToken = response.getAccessToken();
                    preferences.setAccessToken(accessToken);

                    //TODO Genero un UserId random porque no pude pedir la identity del usuario de Slack por tema de permisos (ver método loadLoginUrl)
                    String userId = UUID.randomUUID().toString();
                    preferences.setUserId(userId);

                    Call<Void> registrationResponse = mApiService.registerUser(new UserPushRegistration(userId, preferences.getFirebaseToken()));
                    registrationResponse.enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(OAuthActivity.this, "User Registered in Push Server", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OAuthActivity.this, "Error while registering User in Push Server", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(OAuthActivity.this, R.string.connection_error + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

//                    executor.execute(SlackApi.identity(accessToken, new Callback<IdentityResponse>() {
//                        @Override
//                        public void onSuccess(IdentityResponse response) {
//                            Gson gson = new Gson();
//                            preferences.setUserId(gson.toJson(response));
//
////                            if(response == null){
////                                preferences.setUserId("No response");
////                            }
////                            if(response.user == null){
////                                preferences.setUserId("No user");
////                            }
////
////                            if(response != null && response.user != null){
////                                preferences.setUserId(response.user.id);
////
////                                String firebaseToken = preferences.getFirebaseToken();
////                                //Send FirebaseToken To Server
////                            }
//                        }
//
//                        @Override
//                        public void onError(Exception e) {
//                            preferences.setUserId("Error");
//                            //TODO Handle this better
//                            finish();
//                        }
//                    }));
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
