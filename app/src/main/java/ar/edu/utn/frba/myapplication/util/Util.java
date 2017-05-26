package ar.edu.utn.frba.myapplication.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

import ar.edu.utn.frba.myapplication.api.ImdbApi;
import ar.edu.utn.frba.myapplication.api.PushServerApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Util {
    public final static String mImdbBaseURL = "http://api.themoviedb.org/3/";
    public final static String mPushServerBaseURL = "http://10.0.2.2:8000/";

    public static ImdbApi createImdbNetworkClient(){
        Retrofit mRetrofit = new Retrofit.Builder()
                                .baseUrl(mImdbBaseURL)
                                .addConverterFactory(
                                        GsonConverterFactory.create(
                                                new GsonBuilder()
                                                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                                        .create()
                                        )
                                )
                                .build();

        return mRetrofit.create(ImdbApi.class);
    }

    public static PushServerApi createPushServerNetworkClient(){
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(mPushServerBaseURL)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                new GsonBuilder()
                                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                        .create()
                        )
                )
                .build();

        return mRetrofit.create(PushServerApi.class);
    }
}