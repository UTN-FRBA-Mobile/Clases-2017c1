package ar.edu.utn.frba.myapplication.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by emanuel on 17/4/17.
 */

public class Preferences {

    private static final String PREF_NAME = Preferences.class.getName();
    private static final String ACCESS_TOKEN = "AccessToken";
    private static final String MAIN_PICTURE = "MainPicture";
    private static final String FIREBASE_TOKEN = "FirebaseToken";
    private static final String USER_ID = "UserId";
    private final Context context;
    private SharedPreferences sp;

    public static Preferences get(Context context) {
        return new Preferences(context);
    }

    private Preferences(Context context) {
        this.context = context;
    }

    private SharedPreferences getSp() {
        if (sp == null) {
            sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        return sp;
    }

    public String getAccessToken() {
        return getSp().getString(ACCESS_TOKEN, null);
    }

    public void setAccessToken(String accessToken) {
        getSp().edit().putString(ACCESS_TOKEN, accessToken).apply();
    }

    public String getMainPicture() {
        return getSp().getString(MAIN_PICTURE, null);
    }

    public void setMainPicture(String mainPicture) {
        getSp().edit().putString(MAIN_PICTURE, mainPicture).apply();
    }

    public void setFirebaseToken(String firebaseToken) {
        getSp().edit().putString(FIREBASE_TOKEN, firebaseToken).apply();
    }

    public String getFirebaseToken() {
        return getSp().getString(FIREBASE_TOKEN, null);
    }

    public void setUserId(String userId) {
        getSp().edit().putString(USER_ID, userId).apply();
    }

    public String getUserId() {
        return getSp().getString(USER_ID, null);
    }
}
