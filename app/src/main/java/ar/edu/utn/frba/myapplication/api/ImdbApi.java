package ar.edu.utn.frba.myapplication.api;

import ar.edu.utn.frba.myapplication.model.Movie;
import ar.edu.utn.frba.myapplication.model.MovieListResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ImdbApi {

    @GET("movie/upcoming?api_key=1f54bd990f1cdfb230adb312546d765d")
    Call<MovieListResponse> getUpcomingMovies();

    @GET("movie/{id}?api_key=1f54bd990f1cdfb230adb312546d765d")
    Call<Movie> getMovie(@Path("id") String id);
}
