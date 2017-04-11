package ar.edu.utn.frba.myapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.internal.ws.WebSocket;
import com.squareup.okhttp.internal.ws.WebSocketListener;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ar.edu.utn.frba.myapplication.api.Callback;
import ar.edu.utn.frba.myapplication.api.ResponseParser;
import ar.edu.utn.frba.myapplication.api.SlackApi;
import ar.edu.utn.frba.myapplication.api.responses.RtmStartResponse;
import ar.edu.utn.frba.myapplication.api.responses.event.Event;
import ar.edu.utn.frba.myapplication.session.Session;
import ar.edu.utn.frba.myapplication.session.SessionImpl;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by emanuel on 25/9/16.
 */
public class RTMService extends Service {

    public static final String SessionChangedIntentAction = "ar.edu.utn.frba.mobile.clases.SessionChangedIntentAction";
    public static final String NewEventIntentAction = "ar.edu.utn.frba.mobile.clases.NewEventIntentAction";
    public static final String EventExtraKey = "EventExtra";

    private static final String TAG = RTMService.class.getName();
    private static final String apiToken = "SU TOKEN AQUÍ";

    private final IBinder binder = new Binder();
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler();
    private boolean shouldConnect = false;
    private boolean obtainingUrl = false;
    private String websocketUrl = null;
    private WebSocket webSocket = null;
    private boolean connected = false;
    private int lastId = 1;
    private SessionImpl session;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        shouldConnect = true;
        connectIfRequired();
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        disconnect();
        executor.shutdown();
        return super.onUnbind(intent);
    }

    public void connect() {
        shouldConnect = true;
        connectIfRequired();
    }

    private void connectIfRequired() {
        if (!shouldConnect || obtainingUrl || webSocket != null) {
            return;
        }
        if (websocketUrl == null) {
            obtainWebSocketURI();
        }
        else {
            connectWebSocket();
        }
    }

    private void connectWebSocket() {
        final OkHttpClient client = new OkHttpClient();

        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(websocketUrl)
                .build();
        webSocket = WebSocket.newWebSocket(client, request);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "Conectando al WebSocket.");
                    webSocket.connect(new WebSocketListener() {
                        @Override
                        public void onMessage(BufferedSource payload, WebSocket.PayloadType type) throws IOException {
                            Log.d(TAG, "Mensaje recibido.");
                            final String message = payload.readUtf8();
                            Event event = ResponseParser.instance.parseEvent(message);
                            Intent intent = new Intent(NewEventIntentAction);
                            intent.putExtra(EventExtraKey, event);
                            sendBroadcast(intent);
                            payload.close();
                        }

                        @Override
                        public void onClose(int code, String reason) {
                            webSocket = null;
                            connected = false;
                            Log.d(TAG, "Conexión cerrada.");
                            retryConnectionDelayed();
                        }

                        @Override
                        public void onFailure(IOException e) {
                            e.printStackTrace();
                            Log.d(TAG, "Error de conexión.");
                            retryConnectionDelayed();
                        }
                    });
                    connected = true;
                    websocketUrl = null;
                    Log.d(TAG, "Conectado.");
                    // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
                    client.getDispatcher().getExecutorService().shutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Falló la conexión al WebSocket.");
                    retryConnectionDelayed();
                }
            }
        });
    }

    private void obtainWebSocketURI() {
        obtainingUrl = true;
        Runnable request = SlackApi.rtmStart(apiToken, new Callback<RtmStartResponse>() {
            @Override
            public void onSuccess(RtmStartResponse response) {
                obtainingUrl = false;
                websocketUrl = response.getUrl();
                session = new SessionImpl();
                session.setSelf(response.getSelf());
                session.setTeam(response.getTeam());
                session.setChannels(response.getChannels());
                session.setUsers(response.getUsers());
                broadcastSessionChanged();
                Log.d(TAG, "ws url: " + websocketUrl);
                connectIfRequired();
            }

            @Override
            public void onError(Exception e) {
                obtainingUrl = false;
                if (e != null) {
                    e.printStackTrace();
                }
                session = null;
                broadcastSessionChanged();
                retryConnectionDelayed();
            }
        });
        executor.execute(request);
    }

    public void disconnect() {
        shouldConnect = false;
        session = null;
        if (webSocket != null && connected) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        webSocket.close(1000, "end");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        broadcastSessionChanged();
    }

    private void broadcastSessionChanged() {
        Intent intent = new Intent(SessionChangedIntentAction);
        sendBroadcast(intent);
    }

    private void retryConnectionDelayed() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connectIfRequired();
            }
        }, 1000);
    }

    public boolean isConnecting() {
        return obtainingUrl;
    }

    public boolean isConnected() {
        return connected;
    }

    public Session getSession() {
        return session;
    }

    public void sendMessage(final String channelId, final String text) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject();
                    json.put("type", "message");
                    json.put("id", nextId());
                    json.put("channel", channelId);
                    json.put("text", text);
                    Buffer payload = new Buffer();
                    payload.writeString(json.toString(), Charset.defaultCharset());
                    webSocket.sendMessage(WebSocket.PayloadType.TEXT, payload);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    int nextId() {
        return lastId++;
    }

    public class Binder extends android.os.Binder {

        public RTMService getService() {
            return RTMService.this;
        }
    }
}
