package ar.edu.utn.frba.myapplication.api;

import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by emanuel on 10/4/17.
 */
public class UrlRequest implements Runnable {

    public interface Listener {

        void onReceivedBody(int responseCode, byte body[]);
        void onError(Exception e);
    }

    public interface RequestFactory {

        @NonNull
        UrlRequest makeRequest(URL url, Listener listener);
    }
    private static RequestFactory factory = new RequestFactory() {
        @NonNull
        @Override
        public UrlRequest makeRequest(URL url, Listener listener) {
            return new UrlRequest(url, listener);
        }
    };

    private URL url;
    private Listener listener;

    public static UrlRequest makeRequest(URL url, Listener listener) {
        return factory.makeRequest(url, listener);
    }

    public static void setFactory(RequestFactory newFactory) {
        factory = newFactory;
    }

    public UrlRequest(URL url, Listener listener) {
        this.url = url;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream stream = connection.getInputStream();
            int responseCode = connection.getResponseCode();
            ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
            byte buffer[] = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = stream.read(buffer)) > 0) {
                responseBody.write(buffer, 0, bytesRead);
            }
            listener.onReceivedBody(responseCode, responseBody.toByteArray());
        }
        catch (Exception e) {
            listener.onError(e);
        }
    }
}
