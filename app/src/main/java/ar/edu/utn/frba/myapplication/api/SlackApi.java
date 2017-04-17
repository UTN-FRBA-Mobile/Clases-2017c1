package ar.edu.utn.frba.myapplication.api;

import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import ar.edu.utn.frba.myapplication.api.UrlRequest;
import ar.edu.utn.frba.myapplication.api.responses.AuthRevokeResponse;
import ar.edu.utn.frba.myapplication.api.responses.BaseResponse;
import ar.edu.utn.frba.myapplication.api.responses.OAuthAccessResponse;
import ar.edu.utn.frba.myapplication.api.responses.RtmStartResponse;

/**
 * Created by emanuel on 10/4/17.
 */

public class SlackApi {

    private static final String TAG = SlackApi.class.getName();

    public static String getOAuthURL(String clientId, String scope, String redirectUri) {
        return "https://slack.com/oauth/authorize?client_id=" + clientId + "&scope=" + scope + "&redirect_uri=" + urlEncode(redirectUri);
    }

    public static boolean isOAuthDenied(Uri url) {
        return "access_denied".equals(url.getQueryParameter("error"));
    }

    public static Runnable oauthAccess(String cliendId, String clientSecret, Uri redirectedUri, Callback<OAuthAccessResponse> callback) {
        String code = redirectedUri.getQueryParameter("code");
        Uri redirectUri = redirectedUri.buildUpon().clearQuery().build();
        URL url = parseURL("oauth.access?client_id=" + cliendId + "&client_secret=" + clientSecret + "&code=" + code + "&redirect_uri=" + redirectUri.toString());
        return jsonRequest(url, callback, OAuthAccessResponse.class);
    }

    public static Runnable rtmStart(String token, final Callback<RtmStartResponse> callback) {
        URL url = parseURL("rtm.start?token=" + token);
        Log.d(TAG, "Adquiriendo URL del WebSocket.");
        return jsonRequest(url, callback, RtmStartResponse.class);
    }

    public static Runnable authRevoke(String token, Callback<AuthRevokeResponse> callback) {
        URL url = parseURL("auth.revoke?token=" + token);
        return jsonRequest(url, callback, AuthRevokeResponse.class);
    }

    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private static URL parseURL(String urlString) {
        try {
            return new URL("https://slack.com/api/" + urlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> Runnable jsonRequest(URL url, final Callback<T> callback, final Class<T> clazz) {
        return jsonRequest(url, callback, new ParseJson<T>() {
            @Override
            T parse(String body) {
                return ResponseParser.instance.parse(body, clazz);
            }
        });
    }

    private static <T> Runnable jsonRequest(URL url, final Callback<T> callback, final ParseJson<T> parseJson) {
        return UrlRequest.makeRequest(url, new UrlRequest.Listener() {
            @Override
            public void onReceivedBody(int responseCode, String body) {
                if (responseCode == 200) {
                    try {
                        T response = parseJson.parse(body);
                        callback.onSuccess(response);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
                else {
                    String message = null;
                    try {
                        BaseResponse response = ResponseParser.instance.parse(body, BaseResponse.class);
                        message = response.getError();
                    }
                    catch (Exception e) {
                        message = e.toString();
                    }
                    callback.onError(new RuntimeException(message)); // TODO: agregar un error
                }
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    private static abstract class ParseJson<T> {
        abstract T parse(String body);
    }
}
