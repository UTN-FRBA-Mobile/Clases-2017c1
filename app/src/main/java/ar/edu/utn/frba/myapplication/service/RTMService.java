package ar.edu.utn.frba.myapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.internal.ws.WebSocket;
import com.squareup.okhttp.internal.ws.WebSocketListener;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ar.edu.utn.frba.myapplication.api.Callback;
import ar.edu.utn.frba.myapplication.api.PushServerApi;
import ar.edu.utn.frba.myapplication.api.ResponseParser;
import ar.edu.utn.frba.myapplication.api.SlackApi;
import ar.edu.utn.frba.myapplication.api.requests.UserPushUnregistration;
import ar.edu.utn.frba.myapplication.api.responses.AuthRevokeResponse;
import ar.edu.utn.frba.myapplication.api.responses.Chat;
import ar.edu.utn.frba.myapplication.api.responses.ChatHistoryResponse;
import ar.edu.utn.frba.myapplication.api.responses.RtmStartResponse;
import ar.edu.utn.frba.myapplication.api.responses.event.Event;
import ar.edu.utn.frba.myapplication.api.responses.event.MessageEvent;
import ar.edu.utn.frba.myapplication.api.responses.event.ResponseEvent;
import ar.edu.utn.frba.myapplication.session.Session;
import ar.edu.utn.frba.myapplication.session.SessionImpl;
import ar.edu.utn.frba.myapplication.storage.Preferences;
import ar.edu.utn.frba.myapplication.util.Util;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by emanuel on 25/9/16.
 */
public class RTMService extends Service {

    public static final String SessionChangedIntentAction = "ar.edu.utn.frba.mobile.clases.SessionChangedIntentAction";
    public static final String NewEventIntentAction = "ar.edu.utn.frba.mobile.clases.NewEventIntentAction";
    public static final String EventExtraKey = "EventExtra";

    private static final String TAG = RTMService.class.getName();

    private final IBinder binder = new Binder();
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Preferences preferences = Preferences.get(this);
    private Handler handler = new Handler();
    private boolean shouldConnect = false;
    private boolean obtainingUrl = false;
    private String websocketUrl = null;
    private WebSocket webSocket = null;
    private boolean connected = false;
    private int lastId = 1;
    private SessionImpl session;
    private SparseArray<Event> pendingEvents = new SparseArray<>();
    private PushServerApi mApiService = Util.createPushServerNetworkClient();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        connect();
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        disconnect(new Runnable() {
            @Override
            public void run() {
                executor.shutdown();
            }
        });
        return super.onUnbind(intent);
    }

    public void connect() {
        shouldConnect = true;
        connectIfRequired();
    }

    private void connectIfRequired() {
        if (!shouldConnect || obtainingUrl || webSocket != null || preferences.getAccessToken() == null) {
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
                            if (event instanceof ResponseEvent) {
                                event = updatePendingEvent((ResponseEvent) event);
                            }
                            if (event != null) {
                                broadcastEvent(event);
                            }
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

    private Event updatePendingEvent(ResponseEvent event) {
        try {
            int id = Integer.parseInt(event.getReplyTo());
            Event realEvent = pendingEvents.get(id);
            if (realEvent != null && event.isOk()) {
                realEvent.setTs(event.getTs());
            }
            return realEvent;
        }
        catch (Exception e) {
            return null;
        }
    }

    private void broadcastEvent(Event event) {
        Intent intent = new Intent(NewEventIntentAction);
        intent.putExtra(EventExtraKey, event);
        sendBroadcast(intent);
    }

    private void obtainWebSocketURI() {
        obtainingUrl = true;
        Runnable request = SlackApi.rtmStart(preferences.getAccessToken(), new Callback<RtmStartResponse>() {
            @Override
            public void onSuccess(RtmStartResponse response) {
                obtainingUrl = false;
                websocketUrl = response.getUrl();
                session = new SessionImpl();
                session.setSelf(response.getSelf());
                session.setTeam(response.getTeam());
                session.setChannels(response.getChannels());
                session.setUsers(response.getUsers());
                session.setIMs(response.getIMs());
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

    private void disconnect(final Runnable next) {
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
                    next.run();
                }
            });
        }
        else {
            executor.execute(next);
        }
        broadcastSessionChanged();
    }

    public void logout() {
        String currentAccessToken = preferences.getAccessToken();
        String currentUserId = preferences.getUserId();
        preferences.setAccessToken(null);
        preferences.setUserId(null);
        session = null;
        broadcastSessionChanged();
        disconnect(SlackApi.authRevoke(currentAccessToken, new Callback<AuthRevokeResponse>() {
            @Override
            public void onSuccess(AuthRevokeResponse response) {
            }

            @Override
            public void onError(Exception e) {
            }
        }));

        Call<Void> response = mApiService.unregisterUser(new UserPushUnregistration(currentUserId));
        response.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(RTMService.this, "User unregistered from Push server", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RTMService.this, "Error while unregistering User from Push server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RTMService.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
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
                    int id = nextId();
                    String userId = session.getMe().getId();
                    String type = "message";
                    JSONObject json = new JSONObject();
                    json.put("type", type);
                    json.put("id", id);
                    json.put("channel", channelId);
                    json.put("text", text);
                    MessageEvent message = new MessageEvent(id, channelId, userId, type, text);
                    pendingEvents.put(id, message);
                    broadcastEvent(message);
                    Buffer payload = new Buffer();
                    payload.writeString(json.toString(), Charset.defaultCharset());
                    webSocket.sendMessage(WebSocket.PayloadType.TEXT, payload);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Runnable retrieveChatHistory(Chat chat, Callback<ChatHistoryResponse> callback) {
        Runnable runnable = SlackApi.chatHistory(preferences.getAccessToken(), chat, null, null, true, 100, false, callback);
        executor.execute(runnable);
        return runnable;
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
