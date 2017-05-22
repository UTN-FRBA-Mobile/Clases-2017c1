/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ar.edu.utn.frba.myapplication.firebase;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import ar.edu.utn.frba.myapplication.storage.Preferences;

public class MyFirebaseTokenService extends Service {
    private Preferences preferences = Preferences.get(this);
    private IBinder binder = new Binder();

    /*Guarda el token en las preferencias - En este caso porque no hay usuario logueado a quien
    asociar ese token (y mandarle notificaciones), por lo que se guarda para cuando un usuario
     hago login */
    private void storeRegistration(String firebaseToken) {
        preferences.setFirebaseToken(firebaseToken);
    }

    public void sendRegistrationToServer() {
        String firebaseToken = preferences.getFirebaseToken();
        sendRegistrationToServer(firebaseToken);
    }

    public void sendRegistrationToServer(String firebaseToken) {
        String userId = preferences.getUserId();
        if(userId != null && firebaseToken != null){
            /*Sólo envia el token al server si hay un usuario logueado, para asi asociarlo a ese token,
            y cuando alguien le manda una notificación a ese usuario específico se le manda a ese token
            de fireabse. */
                // TODO
        } else {
            storeRegistration(firebaseToken);
        }
    }

    public void unregistrateFromServer() {
        String firebaseToken = preferences.getFirebaseToken();
        String userId = preferences.getUserId();

        if(firebaseToken != null && userId != null){
            // TODO
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class Binder extends android.os.Binder {

        public MyFirebaseTokenService getService() {
            return MyFirebaseTokenService.this;
        }
    }
}
