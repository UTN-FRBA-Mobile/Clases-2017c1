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

import android.text.BoringLayout;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import ar.edu.utn.frba.myapplication.storage.Preferences;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();

        Preferences preferences = Preferences.get(this);
        String userId = preferences.getUserId();
        Boolean userIsLoggedIn = userId != null;

        if(userIsLoggedIn){
            //Send to server
        } else {
            //Saves the Firebase token
            preferences.setFirebaseToken(firebaseToken);
        }
    }
}
