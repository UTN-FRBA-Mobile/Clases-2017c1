package ar.edu.utn.frba.myapplication.api;

import ar.edu.utn.frba.myapplication.api.requests.UserPushDesregistration;
import ar.edu.utn.frba.myapplication.api.requests.UserPushRegistration;
import ar.edu.utn.frba.myapplication.api.responses.Post;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PushServerApi {
    @POST("/subscribe")
    Call<Post> registerUser(@Body UserPushRegistration data);

    @POST("/unsubscribe")
    Call<Post> desregisterUser(@Body UserPushDesregistration userPushDesregistration);
}
