package ar.edu.utn.frba.myapplication.api;

import android.util.Log;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import ar.edu.utn.frba.myapplication.api.UrlRequest;
import ar.edu.utn.frba.myapplication.api.responses.RtmStartResponse;

/**
 * Created by emanuel on 10/4/17.
 */

public class SlackApi {

    private static final String TAG = SlackApi.class.getName();

    public static Runnable rtmStart(String token, final Callback<RtmStartResponse> callback) {
        URL url;
        try {
            url = new URL("https://slack.com/api/rtm.start?token=" + token);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        Log.d(TAG, "Adquiriendo URL del WebSocket.");
        UrlRequest request = UrlRequest.makeRequest(url, new UrlRequest.Listener() {
            @Override
            public void onReceivedBody(int responseCode, String body) {
                if (responseCode == 200) {
                    try {
                        RtmStartResponse response = ResponseParser.instance.parse(body, RtmStartResponse.class);
                        callback.onSuccess(response);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
                else {
                    Log.d(TAG, "Falló la llamada a rtm.start.");
                    callback.onError(null); // TODO: agregar un error
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG, "Falló la llamada a rtm.start.");
                callback.onError(e);
            }
        });
        return request;
    }
}
