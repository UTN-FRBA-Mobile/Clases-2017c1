package ar.edu.utn.frba.myapplication.api;

import ar.edu.utn.frba.myapplication.api.requests.UserPushDesregistration;
import ar.edu.utn.frba.myapplication.api.requests.UserPushRegistration;
import ar.edu.utn.frba.myapplication.api.responses.Post;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PushServerApi {
    @POST("/subscribe")
    Call<Void> registerUser(@Body UserPushRegistration data);

    @POST("/unsubscribe")
    Call<Void> desregisterUser(@Body UserPushDesregistration userPushDesregistration);
}
