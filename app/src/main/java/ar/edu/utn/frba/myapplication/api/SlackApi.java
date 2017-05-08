package ar.edu.utn.frba.myapplication.api;

import android.net.Uri;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import ar.edu.utn.frba.myapplication.api.responses.AuthRevokeResponse;
import ar.edu.utn.frba.myapplication.api.responses.BaseResponse;
import ar.edu.utn.frba.myapplication.api.responses.Channel;
import ar.edu.utn.frba.myapplication.api.responses.Chat;
import ar.edu.utn.frba.myapplication.api.responses.ChatHistoryResponse;
import ar.edu.utn.frba.myapplication.api.responses.IM;
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

    public static Runnable chatHistory(String token, Chat chat, String latest, String oldest, boolean inclusive, int count, boolean unreads, Callback<ChatHistoryResponse> callback) {
        String chatType = "";
        if (chat instanceof Channel) {
            chatType = "channels";
        }
        else if (chat instanceof IM) {
            chatType = "im";
        }
        StringBuilder urlBuilder = new StringBuilder(chatType).append(".history?token=").append(token).append("&channel=").append(chat.getId()).append("&inclusive=").append(urlEncode(inclusive)).append("&count=").append(urlEncode(count)).append("&unreads=").append(urlEncode(unreads));
        if (latest != null) {
            urlBuilder.append("&latest=").append(latest);
        }
        if (oldest != null) {
            urlBuilder.append("&oldest=").append(oldest);
        }
        URL url = parseURL(urlBuilder.toString());
        return jsonRequest(url, callback, ChatHistoryResponse.class);
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

    private static String urlEncode(boolean value) {
        return value ? "true" : "false";
    }

    private static String urlEncode(int value) {
        return String.valueOf(value);
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
            public void onReceivedBody(int responseCode, byte body[]) {
                String bodyString = new String(body);
                if (responseCode == 200) {
                    try {
                        T response = parseJson.parse(bodyString);
                        callback.onSuccess(response);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
                else {
                    String message = null;
                    try {
                        BaseResponse response = ResponseParser.instance.parse(bodyString, BaseResponse.class);
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
