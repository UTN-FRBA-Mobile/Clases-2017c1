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
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import ar.edu.utn.frba.myapplication.R;
import ar.edu.utn.frba.myapplication.api.ImdbApi;
import ar.edu.utn.frba.myapplication.api.PushServerApi;
import ar.edu.utn.frba.myapplication.api.requests.UserPushRegistration;
import ar.edu.utn.frba.myapplication.api.responses.Post;
import ar.edu.utn.frba.myapplication.model.MovieListResponse;
import ar.edu.utn.frba.myapplication.storage.Preferences;
import ar.edu.utn.frba.myapplication.util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private PushServerApi mApiService = Util.createPushServerNetworkClient();
    Preferences preferences = Preferences.get(this);

    @Override
    public void onTokenRefresh() {
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        preferences.setFirebaseToken(firebaseToken);

        String userId = preferences.getUserId();
        Boolean userIsLoggedIn = userId != null;

        if(userIsLoggedIn){
            Call<Post> response = mApiService.registerUser(new UserPushRegistration(userId, firebaseToken));
            response.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    //TODO
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    //TODO
                }
            });
        }
    }
}
