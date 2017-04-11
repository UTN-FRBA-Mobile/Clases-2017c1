package ar.edu.utn.frba.myapplication.api;

/**
 * Created by emanuel on 10/4/17.
 */

public interface Callback<T> {

    void onSuccess(T response);
    void onError(Exception e);
}
